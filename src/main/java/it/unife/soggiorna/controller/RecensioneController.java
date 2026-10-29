package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.RecensioneDtos.*;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.RecensioneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint REST per le recensioni: creazione richiede autenticazione, lettura per alloggio e' pubblica. */
@RestController
@RequestMapping("/api/recensioni")
public class RecensioneController {

    private final RecensioneService recensioneService;
    private final CurrentUser currentUser;

    public RecensioneController(RecensioneService recensioneService, CurrentUser currentUser) {
        this.recensioneService = recensioneService;
        this.currentUser = currentUser;
    }

    // RF-16
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecensioneView crea(@Valid @RequestBody RecensioneRequest req) {
        return recensioneService.crea(req, currentUser.get());
    }

    // pubblico: recensioni di un alloggio
    @GetMapping("/alloggio/{alloggioId}")
    public List<RecensioneView> perAlloggio(@PathVariable Long alloggioId) {
        return recensioneService.perAlloggio(alloggioId);
    }
}
