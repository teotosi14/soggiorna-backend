package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Accesso dati per {@link Foto}; usata per caricare la galleria di un alloggio separatamente dall'entita' principale. */
public interface FotoRepository extends JpaRepository<Foto, Long> {
    List<Foto> findByAlloggioId(Long alloggioId);

    // Caricamento in batch delle foto di piu' alloggi (es. risultati di ricerca): evita una
    // query per alloggio quando si mappano intere liste verso AlloggioView (vedi Mappers.toViews).
    List<Foto> findByAlloggioIdIn(List<Long> alloggioIds);
}
