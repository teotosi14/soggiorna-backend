package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.EsperienzaDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.CategoriaEsperienza;
import it.unife.soggiorna.model.Esperienza;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.EsperienzaRepository;
import it.unife.soggiorna.repository.PrenotazioneEsperienzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica applicativa per le esperienze: ricerca pubblica (solo future) e CRUD lato host. I posti
 * disponibili in {@code EsperienzaView} sono sempre ricalcolati da {@link PrenotazioneEsperienzaRepository},
 * mai persistiti, per non rischiare di disallinearsi dalle prenotazioni reali.
 */
@Service
public class EsperienzaService {

    private final EsperienzaRepository esperienzaRepository;
    private final PrenotazioneEsperienzaRepository prenotazioneEsperienzaRepository;

    public EsperienzaService(EsperienzaRepository esperienzaRepository,
                             PrenotazioneEsperienzaRepository prenotazioneEsperienzaRepository) {
        this.esperienzaRepository = esperienzaRepository;
        this.prenotazioneEsperienzaRepository = prenotazioneEsperienzaRepository;
    }

    @Transactional(readOnly = true)
    public List<EsperienzaView> cerca(String citta, CategoriaEsperienza categoria) {
        return esperienzaRepository.cerca(citta, categoria, LocalDateTime.now()).stream()
                .map(this::toViewConPostiDisponibili).toList();
    }

    @Transactional(readOnly = true)
    public EsperienzaView get(Long id) {
        return toViewConPostiDisponibili(carica(id));
    }

    @Transactional(readOnly = true)
    public List<EsperienzaView> deiMieiEsperienze(Long hostId) {
        return esperienzaRepository.findByHostId(hostId).stream()
                .map(this::toViewConPostiDisponibili).toList();
    }

    @Transactional
    public EsperienzaView crea(EsperienzaRequest req, Utente host) {
        Esperienza e = Esperienza.builder()
                .host(host)
                .titolo(req.titolo()).descrizione(req.descrizione()).citta(req.citta())
                .categoria(req.categoria()).prezzoPersona(req.prezzoPersona())
                .dataOra(req.dataOra()).durataOre(req.durataOre()).postiMax(req.postiMax())
                .immagineUrl(req.immagineUrl())
                .build();
        e = esperienzaRepository.save(e);
        return Mappers.toView(e, req.postiMax());
    }

    @Transactional
    public EsperienzaView aggiorna(Long id, EsperienzaRequest req, Utente host) {
        Esperienza e = caricaProprio(id, host);
        e.setTitolo(req.titolo());
        e.setDescrizione(req.descrizione());
        e.setCitta(req.citta());
        e.setCategoria(req.categoria());
        e.setPrezzoPersona(req.prezzoPersona());
        e.setDataOra(req.dataOra());
        e.setDurataOre(req.durataOre());
        e.setPostiMax(req.postiMax());
        e.setImmagineUrl(req.immagineUrl());
        return toViewConPostiDisponibili(e);
    }

    @Transactional
    public void disattiva(Long id, Utente host) {
        caricaProprio(id, host).setAttivo(false);
    }

    private EsperienzaView toViewConPostiDisponibili(Esperienza e) {
        long occupati = prenotazioneEsperienzaRepository.sommaPartecipanti(e.getId());
        int disponibili = (int) Math.max(0, e.getPostiMax() - occupati);
        return Mappers.toView(e, disponibili);
    }

    private Esperienza carica(Long id) {
        return esperienzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esperienza non trovata: " + id));
    }

    private Esperienza caricaProprio(Long id, Utente host) {
        Esperienza e = carica(id);
        if (!e.getHost().getId().equals(host.getId())) {
            throw new BusinessException("Non sei l'organizzatore di questa esperienza");
        }
        return e;
    }
}
