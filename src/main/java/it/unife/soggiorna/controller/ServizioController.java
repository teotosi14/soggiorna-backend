package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.AlloggioDtos.ServizioView;
import it.unife.soggiorna.repository.ServizioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint pubblico che espone il catalogo servizi, usato dal frontend per popolare i filtri e il form di pubblicazione. */
@RestController
@RequestMapping("/api/servizi")
public class ServizioController {

    private final ServizioRepository servizioRepository;

    public ServizioController(ServizioRepository servizioRepository) {
        this.servizioRepository = servizioRepository;
    }

    @GetMapping
    public List<ServizioView> tutti() {
        return servizioRepository.findAll().stream()
                .map(s -> new ServizioView(s.getId(), s.getNome()))
                .toList();
    }
}
