package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.PacchettoDtos.PacchettoView;
import it.unife.soggiorna.service.PacchettoVacanzaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint pubblico per le proposte di vacanza della pagina "Suggeriti". */
@RestController
@RequestMapping("/api/pacchetti")
public class PacchettoController {

    private final PacchettoVacanzaService pacchettoVacanzaService;

    public PacchettoController(PacchettoVacanzaService pacchettoVacanzaService) {
        this.pacchettoVacanzaService = pacchettoVacanzaService;
    }

    @GetMapping
    public List<PacchettoView> tutti() {
        return pacchettoVacanzaService.tutti();
    }

    @GetMapping("/{id}")
    public PacchettoView dettaglio(@PathVariable Long id) {
        return pacchettoVacanzaService.dettaglio(id);
    }
}
