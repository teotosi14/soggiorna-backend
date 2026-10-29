package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.PacchettoDtos.PacchettoView;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.PacchettoVacanza;
import it.unife.soggiorna.repository.PacchettoVacanzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lettura pubblica delle proposte di vacanza in evidenza (pagina "Suggeriti"). */
@Service
public class PacchettoVacanzaService {

    private final PacchettoVacanzaRepository pacchettoVacanzaRepository;

    public PacchettoVacanzaService(PacchettoVacanzaRepository pacchettoVacanzaRepository) {
        this.pacchettoVacanzaRepository = pacchettoVacanzaRepository;
    }

    @Transactional(readOnly = true)
    public List<PacchettoView> tutti() {
        return pacchettoVacanzaRepository.findByAttivoTrueOrderByIdAsc().stream()
                .map(Mappers::toView).toList();
    }

    /** Dettaglio di una singola proposta, per la pagina del pacchetto (RF-29). Un pacchetto
     *  disattivato non e' raggiungibile nemmeno per id, coerentemente con {@link #tutti()}. */
    @Transactional(readOnly = true)
    public PacchettoView dettaglio(Long id) {
        return pacchettoVacanzaRepository.findById(id)
                .filter(PacchettoVacanza::getAttivo)
                .map(Mappers::toView)
                .orElseThrow(() -> new ResourceNotFoundException("Pacchetto non trovato: " + id));
    }
}
