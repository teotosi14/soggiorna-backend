package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Servizio;
import org.springframework.data.jpa.repository.JpaRepository;

/** Accesso dati per il catalogo {@link Servizio}: CRUD standard + verifica di unicita' del nome. */
public interface ServizioRepository extends JpaRepository<Servizio, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
