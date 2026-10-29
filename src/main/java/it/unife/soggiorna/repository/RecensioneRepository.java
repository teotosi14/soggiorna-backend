package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/** Accesso dati per {@link Recensione}: verifica esistenza per prenotazione + elenco recensioni di un alloggio. */
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    boolean existsByPrenotazioneId(Long prenotazioneId);

    @Query("SELECT r FROM Recensione r WHERE r.prenotazione.alloggio.id = :alloggioId ORDER BY r.data DESC")
    List<Recensione> findByAlloggioId(@Param("alloggioId") Long alloggioId);
}
