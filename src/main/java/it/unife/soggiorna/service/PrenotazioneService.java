package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.PrenotazioneDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.*;
import it.unife.soggiorna.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Cuore del dominio "prenotazioni": applica tutte le regole di validazione (VR-01..VR-05, VR-11, VR-25)
 * alla creazione, gestisce il cambio di stato (conferma con generazione del {@link Pagamento},
 * rifiuto e annullamento) e le query per guest/host.
 */
@Service
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final AlloggioRepository alloggioRepository;
    private final PagamentoRepository pagamentoRepository;
    private final RecensioneRepository recensioneRepository;
    private final BloccoRepository bloccoRepository;

    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository,
                               AlloggioRepository alloggioRepository,
                               PagamentoRepository pagamentoRepository,
                               RecensioneRepository recensioneRepository,
                               BloccoRepository bloccoRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.alloggioRepository = alloggioRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.recensioneRepository = recensioneRepository;
        this.bloccoRepository = bloccoRepository;
    }

    @Transactional
    public PrenotazioneView crea(PrenotazioneRequest req, Utente guest) {
        Alloggio a = alloggioRepository.findById(req.alloggioId())
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato"));

        // VR-25: un alloggio disattivato dall'host non e' piu' prenotabile
        if (!a.getAttivo()) {
            throw new BusinessException("Questo alloggio non e' piu' disponibile");
        }
        // VR-01: date coerenti
        if (!req.dataCheckin().isBefore(req.dataCheckout())) {
            throw new BusinessException("La data di check-in deve precedere il check-out");
        }
        // VR-03: non puoi prenotare il tuo alloggio
        if (a.getHost().getId().equals(guest.getId())) {
            throw new BusinessException("Non puoi prenotare un tuo alloggio");
        }
        // VR-04: capienza
        if (req.numOspiti() > a.getNumOspitiMax()) {
            throw new BusinessException("Numero di ospiti superiore alla capienza massima");
        }
        // VR-02: niente sovrapposizioni
        long sovrap = prenotazioneRepository.contaSovrapposizioni(a.getId(), req.dataCheckin(), req.dataCheckout());
        if (sovrap > 0) {
            throw new BusinessException("L'alloggio non e' disponibile per le date selezionate");
        }
        // VR-11: le date non devono ricadere in un periodo di indisponibilita' definito dall'host
        long bloccate = bloccoRepository.contaSovrapposizioni(a.getId(), req.dataCheckin(), req.dataCheckout());
        if (bloccate > 0) {
            throw new BusinessException("L'host ha segnato queste date come non disponibili");
        }

        // VR-05: prezzo = prezzo_notte * numero notti
        long notti = ChronoUnit.DAYS.between(req.dataCheckin(), req.dataCheckout());
        BigDecimal totale = a.getPrezzoNotte().multiply(BigDecimal.valueOf(notti));

        Prenotazione p = Prenotazione.builder()
                .alloggio(a)
                .guest(guest)
                .dataCheckin(req.dataCheckin())
                .dataCheckout(req.dataCheckout())
                .numOspiti(req.numOspiti())
                .prezzoTotale(totale)
                .stato(StatoPrenotazione.IN_ATTESA)
                .build();
        p = prenotazioneRepository.save(p);
        return Mappers.toView(p, false);
    }

    @Transactional(readOnly = true)
    public List<PrenotazioneView> mie(Utente guest) {
        return prenotazioneRepository.findByGuestId(guest.getId()).stream()
                .map(p -> Mappers.toView(p, recensioneRepository.existsByPrenotazioneId(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrenotazioneView> ricevute(Utente host) {
        return prenotazioneRepository.findByAlloggioHostId(host.getId()).stream()
                .map(p -> Mappers.toView(p, recensioneRepository.existsByPrenotazioneId(p.getId())))
                .toList();
    }

    @Transactional
    public PrenotazioneView conferma(Long id, Utente host) {
        Prenotazione p = caricaInAttesaDelProprioAlloggio(id, host, "confermare", "confermata");
        p.setStato(StatoPrenotazione.CONFERMATA);
        // Registra il pagamento associato (RF-15), se non esiste gia'
        if (pagamentoRepository.findByPrenotazioneId(p.getId()).isEmpty()) {
            pagamentoRepository.save(Pagamento.builder()
                    .prenotazione(p)
                    .importo(p.getPrezzoTotale())
                    .metodo(MetodoPagamento.CARTA)
                    .stato(StatoPagamento.COMPLETATO)
                    .build());
        }
        return Mappers.toView(p, false);
    }

    // RF-08: l'host rifiuta una prenotazione in attesa (annullamento lato host, distinto da quello del guest)
    @Transactional
    public PrenotazioneView rifiuta(Long id, Utente host) {
        Prenotazione p = caricaInAttesaDelProprioAlloggio(id, host, "rifiutare", "rifiutata");
        p.setStato(StatoPrenotazione.ANNULLATA);
        return Mappers.toView(p, false);
    }

    @Transactional
    public PrenotazioneView annulla(Long id, Utente guest) {
        Prenotazione p = carica(id);
        if (!p.getGuest().getId().equals(guest.getId())) {
            throw new BusinessException("Puoi annullare solo le tue prenotazioni");
        }
        // Bugfix: prima non c'era alcun controllo di stato, quindi via API si poteva annullare
        // anche una prenotazione gia' COMPLETATA (con tanto di recensione pubblicata) o gia'
        // ANNULLATA. Stessi due stati che il frontend gia' considera annullabili (prenotazioni.js).
        if (p.getStato() != StatoPrenotazione.IN_ATTESA && p.getStato() != StatoPrenotazione.CONFERMATA) {
            throw new BusinessException("Solo una prenotazione in attesa o confermata puo' essere annullata");
        }
        p.setStato(StatoPrenotazione.ANNULLATA);
        return Mappers.toView(p, false);
    }

    private Prenotazione carica(Long id) {
        return prenotazioneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata: " + id));
    }

    // Guardia comune a conferma/rifiuta: solo l'host dell'alloggio puo' agire, e solo su una
    // prenotazione ancora IN_ATTESA. Prende infinito/participio del verbo per riusare gli stessi
    // messaggi d'errore ("Solo l'host puo' confermare..." / "...puo' essere confermata").
    private Prenotazione caricaInAttesaDelProprioAlloggio(Long id, Utente host, String infinito, String participio) {
        Prenotazione p = carica(id);
        if (!p.getAlloggio().getHost().getId().equals(host.getId())) {
            throw new BusinessException("Solo l'host puo' " + infinito + " la prenotazione");
        }
        if (p.getStato() != StatoPrenotazione.IN_ATTESA) {
            throw new BusinessException("Solo una prenotazione in attesa puo' essere " + participio);
        }
        return p;
    }
}
