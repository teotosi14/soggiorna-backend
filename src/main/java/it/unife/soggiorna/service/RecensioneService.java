package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.RecensioneDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.*;
import it.unife.soggiorna.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Creazione recensioni con le regole VR-06 (solo su prenotazione completata, propria, non gia' recensita) e lettura per alloggio. */
@Service
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    public RecensioneService(RecensioneRepository recensioneRepository,
                             PrenotazioneRepository prenotazioneRepository) {
        this.recensioneRepository = recensioneRepository;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    @Transactional
    public RecensioneView crea(RecensioneRequest req, Utente autore) {
        Prenotazione p = prenotazioneRepository.findById(req.prenotazioneId())
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata"));

        if (!p.getGuest().getId().equals(autore.getId())) {
            throw new BusinessException("Puoi recensire solo le tue prenotazioni");
        }
        // VR-06: recensione solo su prenotazione completata
        if (p.getStato() != StatoPrenotazione.COMPLETATA) {
            throw new BusinessException("Puoi recensire solo dopo un soggiorno completato");
        }
        if (recensioneRepository.existsByPrenotazioneId(p.getId())) {
            throw new BusinessException("Hai gia' recensito questa prenotazione");
        }

        Recensione r = Recensione.builder()
                .prenotazione(p)
                .rating(req.rating())
                .commento(req.commento())
                .build();
        r = recensioneRepository.save(r);
        return Mappers.toView(r);
    }

    @Transactional(readOnly = true)
    public List<RecensioneView> perAlloggio(Long alloggioId) {
        return recensioneRepository.findByAlloggioId(alloggioId).stream().map(Mappers::toView).toList();
    }
}
