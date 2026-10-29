package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.PacchettoVacanza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Accesso dati per {@link PacchettoVacanza}: consultazione pubblica delle proposte attive. */
public interface PacchettoVacanzaRepository extends JpaRepository<PacchettoVacanza, Long> {

    List<PacchettoVacanza> findByAttivoTrueOrderByIdAsc();
}
