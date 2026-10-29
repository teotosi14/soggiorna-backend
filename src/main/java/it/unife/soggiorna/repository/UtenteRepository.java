package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Accesso dati per {@link Utente}; la ricerca per email e' la base del login e del filtro JWT. */
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByEmail(String email);
    boolean existsByEmail(String email);
}
