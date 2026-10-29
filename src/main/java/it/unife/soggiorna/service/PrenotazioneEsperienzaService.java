package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.PrenotazioneEsperienzaDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.Esperienza;
import it.unife.soggiorna.model.PrenotazioneEsperienza;
import it.unife.soggiorna.model.StatoPrenotazione;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.EsperienzaRepository;
import it.unife.soggiorna.repository.PrenotazioneEsperienzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica applicativa per le prenotazioni di esperienze: applica le regole VR-20..VR-23 alla
 * creazione, gestisce conferma/rifiuto/annullamento. Mirror di {@link PrenotazioneService}.
 */
@Service
public class PrenotazioneEsperienzaService {

    private final PrenotazioneEsperienzaRepository prenotazioneEsperienzaRepository;
    private final EsperienzaRepository esperienzaRepository;

    public PrenotazioneEsperienzaService(PrenotazioneEsperienzaRepository prenotazioneEsperienzaRepository,
                                         EsperienzaRepository esperienzaRepository) {
        this.prenotazioneEsperienzaRepository = prenotazioneEsperienzaRepository;
        this.esperienzaRepository = esperienzaRepository;
    }

    @Transactional
    public PrenotazioneEsperienzaView crea(PrenotazioneEsperienzaRequest req, Utente guest) {
        Esperienza e = esperienzaRepository.findById(req.esperienzaId())
                .orElseThrow(() -> new ResourceNotFoundException("Esperienza non trovata"));

        // VR-23: non si puo' prenotare un'esperienza gia' passata
        if (!e.getDataOra().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Questa esperienza si e' gia' svolta");
        }
        // VR-21: un host non puo' prenotare la propria esperienza
        if (e.getHost().getId().equals(guest.getId())) {
            throw new BusinessException("Non puoi prenotare una tua esperienza");
        }
        // VR-20: capienza residua
        long occupati = prenotazioneEsperienzaRepository.sommaPartecipanti(e.getId());
        if (occupati + req.numPartecipanti() > e.getPostiMax()) {
            throw new BusinessException("Posti insufficienti: disponibili " + Math.max(0, e.getPostiMax() - occupati));
        }

        // VR-22: prezzo = prezzo_persona * numero partecipanti
        BigDecimal totale = e.getPrezzoPersona().multiply(BigDecimal.valueOf(req.numPartecipanti()));

        PrenotazioneEsperienza pe = PrenotazioneEsperienza.builder()
                .esperienza(e).guest(guest)
                .numPartecipanti(req.numPartecipanti()).prezzoTotale(totale).stato(StatoPrenotazione.IN_ATTESA)
                .build();
        return Mappers.toView(prenotazioneEsperienzaRepository.save(pe));
    }

    @Transactional(readOnly = true)
    public List<PrenotazioneEsperienzaView> mie(Utente guest) {
        return prenotazioneEsperienzaRepository.findByGuestId(guest.getId()).stream().map(Mappers::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<PrenotazioneEsperienzaView> ricevute(Utente host) {
        return prenotazioneEsperienzaRepository.findByEsperienzaHostId(host.getId()).stream().map(Mappers::toView).toList();
    }

    @Transactional
    public PrenotazioneEsperienzaView conferma(Long id, Utente host) {
        PrenotazioneEsperienza pe = caricaInAttesaDellaPropriaEsperienza(id, host, "confermare", "confermata");
        pe.setStato(StatoPrenotazione.CONFERMATA);
        return Mappers.toView(pe);
    }

    @Transactional
    public PrenotazioneEsperienzaView rifiuta(Long id, Utente host) {
        PrenotazioneEsperienza pe = caricaInAttesaDellaPropriaEsperienza(id, host, "rifiutare", "rifiutata");
        pe.setStato(StatoPrenotazione.ANNULLATA);
        return Mappers.toView(pe);
    }

    @Transactional
    public PrenotazioneEsperienzaView annulla(Long id, Utente guest) {
        PrenotazioneEsperienza pe = carica(id);
        if (!pe.getGuest().getId().equals(guest.getId())) {
            throw new BusinessException("Puoi annullare solo le tue prenotazioni");
        }
        if (pe.getStato() != StatoPrenotazione.IN_ATTESA && pe.getStato() != StatoPrenotazione.CONFERMATA) {
            throw new BusinessException("Solo una prenotazione in attesa o confermata puo' essere annullata");
        }
        pe.setStato(StatoPrenotazione.ANNULLATA);
        return Mappers.toView(pe);
    }

    private PrenotazioneEsperienza carica(Long id) {
        return prenotazioneEsperienzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata: " + id));
    }

    private PrenotazioneEsperienza caricaInAttesaDellaPropriaEsperienza(Long id, Utente host, String infinito, String participio) {
        PrenotazioneEsperienza pe = carica(id);
        if (!pe.getEsperienza().getHost().getId().equals(host.getId())) {
            throw new BusinessException("Solo l'organizzatore puo' " + infinito + " la prenotazione");
        }
        if (pe.getStato() != StatoPrenotazione.IN_ATTESA) {
            throw new BusinessException("Solo una prenotazione in attesa puo' essere " + participio);
        }
        return pe;
    }
}
