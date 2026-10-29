package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.PrenotazioneVeicoloDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.PrenotazioneVeicolo;
import it.unife.soggiorna.model.StatoPrenotazione;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.model.Veicolo;
import it.unife.soggiorna.repository.PrenotazioneVeicoloRepository;
import it.unife.soggiorna.repository.VeicoloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Logica applicativa per il noleggio auto: applica le regole VR-16..VR-19 alla creazione, gestisce
 * conferma/rifiuto/annullamento. Mirror di {@link PrenotazioneService} per il dominio Veicolo.
 */
@Service
public class PrenotazioneVeicoloService {

    private final PrenotazioneVeicoloRepository prenotazioneVeicoloRepository;
    private final VeicoloRepository veicoloRepository;

    public PrenotazioneVeicoloService(PrenotazioneVeicoloRepository prenotazioneVeicoloRepository,
                                      VeicoloRepository veicoloRepository) {
        this.prenotazioneVeicoloRepository = prenotazioneVeicoloRepository;
        this.veicoloRepository = veicoloRepository;
    }

    @Transactional
    public PrenotazioneVeicoloView crea(PrenotazioneVeicoloRequest req, Utente guest) {
        Veicolo v = veicoloRepository.findById(req.veicoloId())
                .orElseThrow(() -> new ResourceNotFoundException("Veicolo non trovato"));

        // VR-16: date coerenti
        if (!req.dataRitiro().isBefore(req.dataRiconsegna())) {
            throw new BusinessException("La data di ritiro deve precedere quella di riconsegna");
        }
        // VR-18: non puoi noleggiare il tuo veicolo
        if (v.getHost().getId().equals(guest.getId())) {
            throw new BusinessException("Non puoi noleggiare un tuo veicolo");
        }
        // VR-17: niente sovrapposizioni
        long sovrap = prenotazioneVeicoloRepository.contaSovrapposizioni(v.getId(), req.dataRitiro(), req.dataRiconsegna());
        if (sovrap > 0) {
            throw new BusinessException("Il veicolo non e' disponibile per le date selezionate");
        }

        // VR-19: prezzo = prezzo_giorno * numero giorni
        long giorni = ChronoUnit.DAYS.between(req.dataRitiro(), req.dataRiconsegna());
        BigDecimal totale = v.getPrezzoGiorno().multiply(BigDecimal.valueOf(giorni));

        PrenotazioneVeicolo pv = PrenotazioneVeicolo.builder()
                .veicolo(v).guest(guest)
                .dataRitiro(req.dataRitiro()).dataRiconsegna(req.dataRiconsegna())
                .prezzoTotale(totale).stato(StatoPrenotazione.IN_ATTESA)
                .build();
        return Mappers.toView(prenotazioneVeicoloRepository.save(pv));
    }

    @Transactional(readOnly = true)
    public List<PrenotazioneVeicoloView> mie(Utente guest) {
        return prenotazioneVeicoloRepository.findByGuestId(guest.getId()).stream().map(Mappers::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<PrenotazioneVeicoloView> ricevute(Utente host) {
        return prenotazioneVeicoloRepository.findByVeicoloHostId(host.getId()).stream().map(Mappers::toView).toList();
    }

    @Transactional
    public PrenotazioneVeicoloView conferma(Long id, Utente host) {
        PrenotazioneVeicolo pv = caricaInAttesaDelProprioVeicolo(id, host, "confermare", "confermata");
        pv.setStato(StatoPrenotazione.CONFERMATA);
        return Mappers.toView(pv);
    }

    @Transactional
    public PrenotazioneVeicoloView rifiuta(Long id, Utente host) {
        PrenotazioneVeicolo pv = caricaInAttesaDelProprioVeicolo(id, host, "rifiutare", "rifiutata");
        pv.setStato(StatoPrenotazione.ANNULLATA);
        return Mappers.toView(pv);
    }

    @Transactional
    public PrenotazioneVeicoloView annulla(Long id, Utente guest) {
        PrenotazioneVeicolo pv = carica(id);
        if (!pv.getGuest().getId().equals(guest.getId())) {
            throw new BusinessException("Puoi annullare solo i tuoi noleggi");
        }
        if (pv.getStato() != StatoPrenotazione.IN_ATTESA && pv.getStato() != StatoPrenotazione.CONFERMATA) {
            throw new BusinessException("Solo un noleggio in attesa o confermato puo' essere annullato");
        }
        pv.setStato(StatoPrenotazione.ANNULLATA);
        return Mappers.toView(pv);
    }

    private PrenotazioneVeicolo carica(Long id) {
        return prenotazioneVeicoloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noleggio non trovato: " + id));
    }

    private PrenotazioneVeicolo caricaInAttesaDelProprioVeicolo(Long id, Utente host, String infinito, String participio) {
        PrenotazioneVeicolo pv = carica(id);
        if (!pv.getVeicolo().getHost().getId().equals(host.getId())) {
            throw new BusinessException("Solo l'host puo' " + infinito + " il noleggio");
        }
        if (pv.getStato() != StatoPrenotazione.IN_ATTESA) {
            throw new BusinessException("Solo un noleggio in attesa puo' essere " + participio);
        }
        return pv;
    }
}
