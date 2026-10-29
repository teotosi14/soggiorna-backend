package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.PrenotazioneDtos.*;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.PrenotazioneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint REST per le prenotazioni: creazione e liste (proprie/ricevute), conferma (host) e annullamento (guest). Tutti richiedono autenticazione. */
@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;
    private final CurrentUser currentUser;

    public PrenotazioneController(PrenotazioneService prenotazioneService, CurrentUser currentUser) {
        this.prenotazioneService = prenotazioneService;
        this.currentUser = currentUser;
    }

    // RF-11
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrenotazioneView crea(@Valid @RequestBody PrenotazioneRequest req) {
        return prenotazioneService.crea(req, currentUser.get());
    }

    // RF-13: le mie prenotazioni (come guest)
    @GetMapping("/mie")
    public List<PrenotazioneView> mie() {
        return prenotazioneService.mie(currentUser.get());
    }

    // RF-08: prenotazioni ricevute (come host)
    @GetMapping("/ricevute")
    public List<PrenotazioneView> ricevute() {
        return prenotazioneService.ricevute(currentUser.get());
    }

    @PutMapping("/{id}/conferma")
    public PrenotazioneView conferma(@PathVariable Long id) {
        return prenotazioneService.conferma(id, currentUser.get());
    }

    // RF-08: l'host rifiuta una prenotazione in attesa
    @PutMapping("/{id}/rifiuta")
    public PrenotazioneView rifiuta(@PathVariable Long id) {
        return prenotazioneService.rifiuta(id, currentUser.get());
    }

    @PutMapping("/{id}/annulla")
    public PrenotazioneView annulla(@PathVariable Long id) {
        return prenotazioneService.annulla(id, currentUser.get());
    }
}
