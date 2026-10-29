package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.SpesaDtos.*;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.SpesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Endpoint REST per il registro spese di viaggio, annidato sotto la prenotazione a cui appartiene. */
@RestController
@RequestMapping("/api/prenotazioni/{prenotazioneId}/spese")
public class SpesaController {

    private final SpesaService spesaService;
    private final CurrentUser currentUser;

    public SpesaController(SpesaService spesaService, CurrentUser currentUser) {
        this.spesaService = spesaService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public RiepilogoSpeseView elenco(@PathVariable Long prenotazioneId) {
        return spesaService.elenco(prenotazioneId, currentUser.get());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpesaView aggiungi(@PathVariable Long prenotazioneId, @Valid @RequestBody SpesaRequest req) {
        return spesaService.aggiungi(prenotazioneId, req, currentUser.get());
    }

    @DeleteMapping("/{spesaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void elimina(@PathVariable Long prenotazioneId, @PathVariable Long spesaId) {
        spesaService.elimina(prenotazioneId, spesaId, currentUser.get());
    }
}
