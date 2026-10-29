package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.PrenotazioneEsperienzaDtos.*;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.PrenotazioneEsperienzaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint REST per le prenotazioni di esperienze. Tutti richiedono autenticazione. */
@RestController
@RequestMapping("/api/prenotazioni-esperienze")
public class PrenotazioneEsperienzaController {

    private final PrenotazioneEsperienzaService prenotazioneEsperienzaService;
    private final CurrentUser currentUser;

    public PrenotazioneEsperienzaController(PrenotazioneEsperienzaService prenotazioneEsperienzaService, CurrentUser currentUser) {
        this.prenotazioneEsperienzaService = prenotazioneEsperienzaService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrenotazioneEsperienzaView crea(@Valid @RequestBody PrenotazioneEsperienzaRequest req) {
        return prenotazioneEsperienzaService.crea(req, currentUser.get());
    }

    @GetMapping("/mie")
    public List<PrenotazioneEsperienzaView> mie() {
        return prenotazioneEsperienzaService.mie(currentUser.get());
    }

    @GetMapping("/ricevute")
    public List<PrenotazioneEsperienzaView> ricevute() {
        return prenotazioneEsperienzaService.ricevute(currentUser.get());
    }

    @PutMapping("/{id}/conferma")
    public PrenotazioneEsperienzaView conferma(@PathVariable Long id) {
        return prenotazioneEsperienzaService.conferma(id, currentUser.get());
    }

    @PutMapping("/{id}/rifiuta")
    public PrenotazioneEsperienzaView rifiuta(@PathVariable Long id) {
        return prenotazioneEsperienzaService.rifiuta(id, currentUser.get());
    }

    @PutMapping("/{id}/annulla")
    public PrenotazioneEsperienzaView annulla(@PathVariable Long id) {
        return prenotazioneEsperienzaService.annulla(id, currentUser.get());
    }
}
