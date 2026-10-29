package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.AlloggioDtos.PeriodoNonDisponibileView;
import it.unife.soggiorna.dto.VeicoloDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.CambioVeicolo;
import it.unife.soggiorna.model.StatoPrenotazione;
import it.unife.soggiorna.model.TipoVeicolo;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.model.Veicolo;
import it.unife.soggiorna.repository.PrenotazioneVeicoloRepository;
import it.unife.soggiorna.repository.VeicoloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Logica applicativa per il noleggio auto: ricerca pubblica e CRUD lato host. {@code caricaProprio}
 * centralizza il controllo "sei tu il proprietario?", stesso pattern di {@link AlloggioService}.
 */
@Service
public class VeicoloService {

    private final VeicoloRepository veicoloRepository;
    private final PrenotazioneVeicoloRepository prenotazioneVeicoloRepository;

    public VeicoloService(VeicoloRepository veicoloRepository, PrenotazioneVeicoloRepository prenotazioneVeicoloRepository) {
        this.veicoloRepository = veicoloRepository;
        this.prenotazioneVeicoloRepository = prenotazioneVeicoloRepository;
    }

    // Calendario pubblico delle date occupate, cosi' il guest le vede prima ancora di tentare
    // la prenotazione (mirror di AlloggioService.dateOccupate, senza i blocchi (entita' Blocco)
    // che per i veicoli non esistono, vedi piano-nuove-funzionalita.md).
    @Transactional(readOnly = true)
    public List<PeriodoNonDisponibileView> dateOccupate(Long veicoloId) {
        return prenotazioneVeicoloRepository
                .findByVeicoloIdAndStatoIn(veicoloId, List.of(StatoPrenotazione.CONFERMATA, StatoPrenotazione.IN_ATTESA))
                .stream()
                .map(pv -> new PeriodoNonDisponibileView(pv.getDataRitiro(), pv.getDataRiconsegna()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VeicoloView> cerca(String citta, TipoVeicolo tipo, CambioVeicolo cambio,
                                   LocalDate dataRitiro, LocalDate dataRiconsegna) {
        if (dataRitiro != null && dataRiconsegna != null && !dataRitiro.isBefore(dataRiconsegna)) {
            dataRitiro = null;
            dataRiconsegna = null;
        }
        return veicoloRepository.cerca(citta, tipo, cambio, dataRitiro, dataRiconsegna).stream()
                .map(Mappers::toView).toList();
    }

    @Transactional(readOnly = true)
    public VeicoloView get(Long id) {
        return Mappers.toView(carica(id));
    }

    @Transactional(readOnly = true)
    public List<VeicoloView> deiMieiVeicoli(Long hostId) {
        return veicoloRepository.findByHostId(hostId).stream().map(Mappers::toView).toList();
    }

    @Transactional
    public VeicoloView crea(VeicoloRequest req, Utente host) {
        Veicolo v = Veicolo.builder()
                .host(host)
                .marca(req.marca()).modello(req.modello()).anno(req.anno())
                .tipo(req.tipo()).cambio(req.cambio()).posti(req.posti())
                .prezzoGiorno(req.prezzoGiorno()).citta(req.citta()).immagineUrl(req.immagineUrl())
                .build();
        return Mappers.toView(veicoloRepository.save(v));
    }

    @Transactional
    public VeicoloView aggiorna(Long id, VeicoloRequest req, Utente host) {
        Veicolo v = caricaProprio(id, host);
        v.setMarca(req.marca());
        v.setModello(req.modello());
        v.setAnno(req.anno());
        v.setTipo(req.tipo());
        v.setCambio(req.cambio());
        v.setPosti(req.posti());
        v.setPrezzoGiorno(req.prezzoGiorno());
        v.setCitta(req.citta());
        v.setImmagineUrl(req.immagineUrl());
        return Mappers.toView(v);
    }

    @Transactional
    public void disattiva(Long id, Utente host) {
        caricaProprio(id, host).setAttivo(false);
    }

    private Veicolo carica(Long id) {
        return veicoloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veicolo non trovato: " + id));
    }

    private Veicolo caricaProprio(Long id, Utente host) {
        Veicolo v = carica(id);
        if (!v.getHost().getId().equals(host.getId())) {
            throw new BusinessException("Non sei il proprietario di questo veicolo");
        }
        return v;
    }
}
