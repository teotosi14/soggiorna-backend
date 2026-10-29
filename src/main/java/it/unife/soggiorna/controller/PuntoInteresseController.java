package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.PuntoInteresseDtos.CittaSommario;
import it.unife.soggiorna.dto.PuntoInteresseDtos.PuntoInteresseView;
import it.unife.soggiorna.service.PuntoInteresseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint pubblico per la guida "Scopri la città" (scrittura riservata all'admin, vedi AdminController). */
@RestController
@RequestMapping("/api/punti-interesse")
public class PuntoInteresseController {

    private final PuntoInteresseService puntoInteresseService;

    public PuntoInteresseController(PuntoInteresseService puntoInteresseService) {
        this.puntoInteresseService = puntoInteresseService;
    }

    @GetMapping
    public List<PuntoInteresseView> perCitta(@RequestParam String citta) {
        return puntoInteresseService.perCitta(citta);
    }

    @GetMapping("/citta")
    public List<CittaSommario> tutteLeCitta() {
        return puntoInteresseService.tutteLeCitta();
    }
}
