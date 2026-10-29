package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Spesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Accesso dati per {@link Spesa}: tutte le voci di spesa di una prenotazione. */
public interface SpesaRepository extends JpaRepository<Spesa, Long> {

    List<Spesa> findByPrenotazioneId(Long prenotazioneId);
}
