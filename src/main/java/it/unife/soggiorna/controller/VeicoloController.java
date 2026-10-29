package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.AlloggioDtos.PeriodoNonDisponibileView;
import it.unife.soggiorna.dto.VeicoloDtos.*;
import it.unife.soggiorna.model.CambioVeicolo;
import it.unife.soggiorna.model.TipoVeicolo;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.VeicoloService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Endpoint REST per il noleggio auto: ricerca e dettaglio pubblici, pubblicazione/modifica riservate all'host autenticato. */
@RestController
@RequestMapping("/api/veicoli")
public class VeicoloController {

    private final VeicoloService veicoloService;
    private final CurrentUser currentUser;

    public VeicoloController(VeicoloService veicoloService, CurrentUser currentUser) {
        this.veicoloService = veicoloService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<VeicoloView> cerca(@RequestParam(required = false) String citta,
                                   @RequestParam(required = false) TipoVeicolo tipo,
                                   @RequestParam(required = false) CambioVeicolo cambio,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRitiro,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRiconsegna) {
        return veicoloService.cerca(citta, tipo, cambio, dataRitiro, dataRiconsegna);
    }

    @GetMapping("/{id}")
    public VeicoloView dettaglio(@PathVariable Long id) {
        return veicoloService.get(id);
    }

    @GetMapping("/miei")
    public List<VeicoloView> miei() {
        return veicoloService.deiMieiVeicoli(currentUser.get().getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeicoloView crea(@Valid @RequestBody VeicoloRequest req) {
        return veicoloService.crea(req, currentUser.get());
    }

    @PutMapping("/{id}")
    public VeicoloView aggiorna(@PathVariable Long id, @Valid @RequestBody VeicoloRequest req) {
        return veicoloService.aggiorna(id, req, currentUser.get());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disattiva(@PathVariable Long id) {
        veicoloService.disattiva(id, currentUser.get());
    }

    @GetMapping("/{id}/date-occupate")
    public List<PeriodoNonDisponibileView> dateOccupate(@PathVariable Long id) {
        return veicoloService.dateOccupate(id);
    }
}
