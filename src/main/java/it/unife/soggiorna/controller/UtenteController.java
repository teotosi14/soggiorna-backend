package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.UtenteDtos.*;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Endpoint per consultare e modificare i propri dati (profilo e password); richiede sempre il JWT. */
@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService utenteService;
    private final CurrentUser currentUser;

    public UtenteController(UtenteService utenteService, CurrentUser currentUser) {
        this.utenteService = utenteService;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    public ProfiloResponse me() {
        return utenteService.profilo(currentUser.get());
    }

    @PutMapping("/me")
    public ProfiloResponse aggiorna(@Valid @RequestBody AggiornaProfiloRequest req) {
        return utenteService.aggiornaProfilo(currentUser.get(), req);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiaPassword(@Valid @RequestBody CambiaPasswordRequest req) {
        utenteService.cambiaPassword(currentUser.get(), req);
    }
}
