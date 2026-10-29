package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.PrenotazioneVeicoloDtos.*;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.PrenotazioneVeicoloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint REST per le prenotazioni di veicoli a noleggio. Tutti richiedono autenticazione. */
@RestController
@RequestMapping("/api/prenotazioni-veicoli")
public class PrenotazioneVeicoloController {

    private final PrenotazioneVeicoloService prenotazioneVeicoloService;
    private final CurrentUser currentUser;

    public PrenotazioneVeicoloController(PrenotazioneVeicoloService prenotazioneVeicoloService, CurrentUser currentUser) {
        this.prenotazioneVeicoloService = prenotazioneVeicoloService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrenotazioneVeicoloView crea(@Valid @RequestBody PrenotazioneVeicoloRequest req) {
        return prenotazioneVeicoloService.crea(req, currentUser.get());
    }

    @GetMapping("/mie")
    public List<PrenotazioneVeicoloView> mie() {
        return prenotazioneVeicoloService.mie(currentUser.get());
    }

    @GetMapping("/ricevute")
    public List<PrenotazioneVeicoloView> ricevute() {
        return prenotazioneVeicoloService.ricevute(currentUser.get());
    }

    @PutMapping("/{id}/conferma")
    public PrenotazioneVeicoloView conferma(@PathVariable Long id) {
        return prenotazioneVeicoloService.conferma(id, currentUser.get());
    }

    @PutMapping("/{id}/rifiuta")
    public PrenotazioneVeicoloView rifiuta(@PathVariable Long id) {
        return prenotazioneVeicoloService.rifiuta(id, currentUser.get());
    }

    @PutMapping("/{id}/annulla")
    public PrenotazioneVeicoloView annulla(@PathVariable Long id) {
        return prenotazioneVeicoloService.annulla(id, currentUser.get());
    }
}
