package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.EsperienzaDtos.*;
import it.unife.soggiorna.model.CategoriaEsperienza;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.EsperienzaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint REST per le esperienze: ricerca e dettaglio pubblici, pubblicazione/modifica riservate all'host autenticato. */
@RestController
@RequestMapping("/api/esperienze")
public class EsperienzaController {

    private final EsperienzaService esperienzaService;
    private final CurrentUser currentUser;

    public EsperienzaController(EsperienzaService esperienzaService, CurrentUser currentUser) {
        this.esperienzaService = esperienzaService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<EsperienzaView> cerca(@RequestParam(required = false) String citta,
                                      @RequestParam(required = false) CategoriaEsperienza categoria) {
        return esperienzaService.cerca(citta, categoria);
    }

    @GetMapping("/{id}")
    public EsperienzaView dettaglio(@PathVariable Long id) {
        return esperienzaService.get(id);
    }

    @GetMapping("/mie")
    public List<EsperienzaView> mie() {
        return esperienzaService.deiMieiEsperienze(currentUser.get().getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EsperienzaView crea(@Valid @RequestBody EsperienzaRequest req) {
        return esperienzaService.crea(req, currentUser.get());
    }

    @PutMapping("/{id}")
    public EsperienzaView aggiorna(@PathVariable Long id, @Valid @RequestBody EsperienzaRequest req) {
        return esperienzaService.aggiorna(id, req, currentUser.get());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disattiva(@PathVariable Long id) {
        esperienzaService.disattiva(id, currentUser.get());
    }
}
