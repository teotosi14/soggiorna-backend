package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.AdminDtos.*;
import it.unife.soggiorna.dto.AlloggioDtos.AlloggioView;
import it.unife.soggiorna.dto.AlloggioDtos.ServizioView;
import it.unife.soggiorna.dto.EsperienzaDtos.EsperienzaView;
import it.unife.soggiorna.dto.PuntoInteresseDtos.PuntoInteresseRequest;
import it.unife.soggiorna.dto.PuntoInteresseDtos.PuntoInteresseView;
import it.unife.soggiorna.dto.RecensioneDtos.RecensioneView;
import it.unife.soggiorna.dto.VeicoloDtos.VeicoloView;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.Alloggio;
import it.unife.soggiorna.model.Esperienza;
import it.unife.soggiorna.model.PuntoInteresse;
import it.unife.soggiorna.model.Servizio;
import it.unife.soggiorna.model.StatoPrenotazione;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.model.Veicolo;
import it.unife.soggiorna.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logica applicativa riservata all'Amministratore: gestione del catalogo servizi (dichiarata
 * come sua responsabilita' fin dall'analisi dei requisiti) e moderazione dei contenuti
 * (disattivazione di un alloggio altrui, eliminazione di una recensione, sospensione di un
 * utente). Tutti i metodi presuppongono che il chiamante sia gia' stato autorizzato a livello
 * di endpoint ({@code hasRole("ADMIN")} in SecurityConfig): qui non si ripete quel controllo,
 * solo le regole di dominio specifiche dell'operazione.
 */
@Service
public class AdminService {

    private final ServizioRepository servizioRepository;
    private final AlloggioRepository alloggioRepository;
    private final FotoRepository fotoRepository;
    private final RecensioneRepository recensioneRepository;
    private final UtenteRepository utenteRepository;
    private final VeicoloRepository veicoloRepository;
    private final EsperienzaRepository esperienzaRepository;
    private final PrenotazioneEsperienzaRepository prenotazioneEsperienzaRepository;
    private final PuntoInteresseRepository puntoInteresseRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    public AdminService(ServizioRepository servizioRepository,
                        AlloggioRepository alloggioRepository,
                        FotoRepository fotoRepository,
                        RecensioneRepository recensioneRepository,
                        UtenteRepository utenteRepository,
                        VeicoloRepository veicoloRepository,
                        EsperienzaRepository esperienzaRepository,
                        PrenotazioneEsperienzaRepository prenotazioneEsperienzaRepository,
                        PuntoInteresseRepository puntoInteresseRepository,
                        PrenotazioneRepository prenotazioneRepository) {
        this.servizioRepository = servizioRepository;
        this.alloggioRepository = alloggioRepository;
        this.fotoRepository = fotoRepository;
        this.recensioneRepository = recensioneRepository;
        this.utenteRepository = utenteRepository;
        this.veicoloRepository = veicoloRepository;
        this.esperienzaRepository = esperienzaRepository;
        this.prenotazioneEsperienzaRepository = prenotazioneEsperienzaRepository;
        this.puntoInteresseRepository = puntoInteresseRepository;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    // ---- Catalogo servizi ----

    @Transactional
    public ServizioView creaServizio(ServizioRequest req) {
        String nome = req.nome().trim();
        if (servizioRepository.existsByNomeIgnoreCase(nome)) {
            throw new BusinessException("Esiste gia' un servizio con questo nome");
        }
        Servizio s = servizioRepository.save(Servizio.builder().nome(nome).build());
        return new ServizioView(s.getId(), s.getNome());
    }

    @Transactional
    public ServizioView rinominaServizio(Long id, ServizioRequest req) {
        Servizio s = servizioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servizio non trovato: " + id));
        String nome = req.nome().trim();
        if (servizioRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new BusinessException("Esiste gia' un servizio con questo nome");
        }
        s.setNome(nome);
        return new ServizioView(s.getId(), s.getNome());
    }

    @Transactional
    public void eliminaServizio(Long id) {
        if (!servizioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servizio non trovato: " + id);
        }
        long inUso = alloggioRepository.contaAlloggiConServizio(id);
        if (inUso > 0) {
            throw new BusinessException("Servizio usato da " + inUso + " alloggi: rimuovilo dagli annunci prima di eliminarlo");
        }
        servizioRepository.deleteById(id);
    }

    // ---- Moderazione alloggi: a differenza di AlloggioService.disattiva, qui non si verifica
    //      la proprieta' dell'annuncio, perche' l'amministratore agisce su alloggi altrui. ----

    @Transactional(readOnly = true)
    public List<AlloggioView> elencoAlloggi() {
        return Mappers.toViews(alloggioRepository.findAll(), fotoRepository);
    }

    @Transactional
    public AlloggioView impostaAttivoAlloggio(Long id, boolean attivo) {
        Alloggio a = alloggioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato: " + id));
        a.setAttivo(attivo);
        // VR-26: come AlloggioService.disattiva, un alloggio disattivato non puo' mantenere
        // prenotazioni ancora da onorare (qui l'admin agisce su un alloggio altrui).
        if (!attivo) {
            annullaPrenotazioniAttive(id, "Un amministratore ha disattivato l'alloggio");
        }
        return Mappers.toView(a, fotoRepository.findByAlloggioId(id));
    }

    // VR-26: annulla le prenotazioni IN_ATTESA/CONFERMATA di un alloggio che smette di essere
    // prenotabile, con un motivo diverso a seconda di chi/cosa ha causato la disattivazione.
    // Non tocca COMPLETATA/ANNULLATA: solo gli impegni futuri non piu' onorabili.
    private void annullaPrenotazioniAttive(Long alloggioId, String motivo) {
        prenotazioneRepository.findByAlloggioIdAndStatoIn(alloggioId,
                        List.of(StatoPrenotazione.IN_ATTESA, StatoPrenotazione.CONFERMATA))
                .forEach(p -> {
                    p.setStato(StatoPrenotazione.ANNULLATA);
                    p.setMotivoAnnullamento(motivo);
                });
    }

    // ---- Moderazione recensioni ----

    @Transactional(readOnly = true)
    public List<RecensioneView> elencoRecensioni() {
        return recensioneRepository.findAll().stream().map(Mappers::toView).toList();
    }

    @Transactional
    public void eliminaRecensione(Long id) {
        if (!recensioneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recensione non trovata: " + id);
        }
        recensioneRepository.deleteById(id);
    }

    // ---- Gestione utenti: sospensione = blocco del login, non cancellazione dell'account
    //      (le prenotazioni/recensioni/alloggi storici dell'utente restano coerenti). ----

    @Transactional(readOnly = true)
    public List<UtenteAdminView> elencoUtenti() {
        return utenteRepository.findAll().stream()
                .map(u -> new UtenteAdminView(u.getId(), u.getEmail(), u.getNome(), u.getCognome(),
                        u.getRuolo(), u.getAttivo(), u.getDataRegistrazione()))
                .toList();
    }

    @Transactional
    public void sospendiUtente(Long id, Utente admin) {
        if (id.equals(admin.getId())) {
            throw new BusinessException("Non puoi sospendere te stesso");
        }
        Utente u = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato: " + id));
        u.setAttivo(false);
        // Bugfix: sospendere un host bloccava solo il suo login, ma i suoi alloggi restavano
        // attivi e prenotabili da chiunque (contraddice lo scopo della sospensione come
        // moderazione dei contenuti). Non si riattivano automaticamente in riattivaUtente:
        // l'admin puo' farlo singolarmente dalla tab Alloggi dopo aver verificato ciascun caso,
        // cosi' non si riaccendono per errore annunci che l'host aveva gia' disattivato lui stesso.
        alloggioRepository.findByHostId(id).forEach(a -> {
            a.setAttivo(false);
            annullaPrenotazioniAttive(a.getId(), "L'host e' stato sospeso");
        });
    }

    @Transactional
    public void riattivaUtente(Long id) {
        Utente u = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato: " + id));
        u.setAttivo(true);
    }

    // ---- Moderazione veicoli a noleggio: stessa logica di impostaAttivoAlloggio, senza
    //      verificare la proprieta' (l'amministratore agisce su veicoli altrui). ----

    @Transactional(readOnly = true)
    public List<VeicoloView> elencoVeicoli() {
        return veicoloRepository.findAll().stream().map(Mappers::toView).toList();
    }

    @Transactional
    public VeicoloView impostaAttivoVeicolo(Long id, boolean attivo) {
        Veicolo v = veicoloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veicolo non trovato: " + id));
        v.setAttivo(attivo);
        return Mappers.toView(v);
    }

    // ---- Moderazione esperienze ----

    @Transactional(readOnly = true)
    public List<EsperienzaView> elencoEsperienze() {
        return esperienzaRepository.findAll().stream()
                .map(e -> Mappers.toView(e, (int) Math.max(0, e.getPostiMax() - prenotazioneEsperienzaRepository.sommaPartecipanti(e.getId()))))
                .toList();
    }

    @Transactional
    public EsperienzaView impostaAttivoEsperienza(Long id, boolean attivo) {
        Esperienza e = esperienzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esperienza non trovata: " + id));
        e.setAttivo(attivo);
        long occupati = prenotazioneEsperienzaRepository.sommaPartecipanti(id);
        return Mappers.toView(e, (int) Math.max(0, e.getPostiMax() - occupati));
    }

    // ---- CRUD guida "Scopri la citta'" ----

    @Transactional
    public PuntoInteresseView creaPuntoInteresse(PuntoInteresseRequest req) {
        PuntoInteresse p = PuntoInteresse.builder()
                .citta(req.citta()).nome(req.nome()).categoria(req.categoria())
                .descrizione(req.descrizione()).indirizzo(req.indirizzo()).immagineUrl(req.immagineUrl())
                .build();
        return Mappers.toView(puntoInteresseRepository.save(p));
    }

    @Transactional
    public PuntoInteresseView aggiornaPuntoInteresse(Long id, PuntoInteresseRequest req) {
        PuntoInteresse p = puntoInteresseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto di interesse non trovato: " + id));
        p.setCitta(req.citta());
        p.setNome(req.nome());
        p.setCategoria(req.categoria());
        p.setDescrizione(req.descrizione());
        p.setIndirizzo(req.indirizzo());
        p.setImmagineUrl(req.immagineUrl());
        return Mappers.toView(p);
    }

    @Transactional
    public void eliminaPuntoInteresse(Long id) {
        if (!puntoInteresseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Punto di interesse non trovato: " + id);
        }
        puntoInteresseRepository.deleteById(id);
    }
}
