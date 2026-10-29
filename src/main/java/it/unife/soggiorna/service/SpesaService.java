package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.SpesaDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.Prenotazione;
import it.unife.soggiorna.model.Spesa;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.PrenotazioneRepository;
import it.unife.soggiorna.repository.SpesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Logica applicativa per il registro spese di viaggio (VR-24: solo il guest della prenotazione puo'
 * gestire le proprie spese). Il totale in {@link RiepilogoSpeseView} e' sempre ricalcolato dal server.
 */
@Service
public class SpesaService {

    private final SpesaRepository spesaRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    public SpesaService(SpesaRepository spesaRepository, PrenotazioneRepository prenotazioneRepository) {
        this.spesaRepository = spesaRepository;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    @Transactional(readOnly = true)
    public RiepilogoSpeseView elenco(Long prenotazioneId, Utente guest) {
        assicuraPropriaPrenotazione(prenotazioneId, guest);
        List<SpesaView> spese = spesaRepository.findByPrenotazioneId(prenotazioneId).stream()
                .map(Mappers::toView).toList();
        BigDecimal totale = spese.stream().map(SpesaView::importo).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new RiepilogoSpeseView(spese, totale);
    }

    @Transactional
    public SpesaView aggiungi(Long prenotazioneId, SpesaRequest req, Utente guest) {
        Prenotazione p = assicuraPropriaPrenotazione(prenotazioneId, guest);
        Spesa s = Spesa.builder()
                .prenotazione(p).categoria(req.categoria()).importo(req.importo())
                .descrizione(req.descrizione()).data(req.data())
                .build();
        return Mappers.toView(spesaRepository.save(s));
    }

    @Transactional
    public void elimina(Long prenotazioneId, Long spesaId, Utente guest) {
        assicuraPropriaPrenotazione(prenotazioneId, guest);
        Spesa s = spesaRepository.findById(spesaId)
                .orElseThrow(() -> new ResourceNotFoundException("Spesa non trovata: " + spesaId));
        if (!s.getPrenotazione().getId().equals(prenotazioneId)) {
            throw new ResourceNotFoundException("Spesa non trovata: " + spesaId);
        }
        spesaRepository.delete(s);
    }

    // VR-24: solo il guest della prenotazione puo' vedere/gestire le spese collegate.
    private Prenotazione assicuraPropriaPrenotazione(Long prenotazioneId, Utente guest) {
        Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata: " + prenotazioneId));
        if (!p.getGuest().getId().equals(guest.getId())) {
            throw new BusinessException("Puoi gestire solo le spese delle tue prenotazioni");
        }
        return p;
    }
}
