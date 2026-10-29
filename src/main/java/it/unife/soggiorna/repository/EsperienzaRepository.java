package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.CategoriaEsperienza;
import it.unife.soggiorna.model.Esperienza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

/** Accesso dati per {@link Esperienza}: CRUD ereditato da JpaRepository + ricerca filtrata custom. */
public interface EsperienzaRepository extends JpaRepository<Esperienza, Long> {

    List<Esperienza> findByHostId(Long hostId);

    // Ricerca pubblica: solo esperienze future (non ha senso proporne una gia' passata).
    @Query("""
            SELECT e FROM Esperienza e
            WHERE e.attivo = true
              AND e.dataOra > :adesso
              AND (:citta IS NULL OR LOWER(e.citta) LIKE LOWER(CONCAT('%', :citta, '%')))
              AND (:categoria IS NULL OR e.categoria = :categoria)
            ORDER BY e.dataOra ASC
            """)
    List<Esperienza> cerca(@Param("citta") String citta,
                           @Param("categoria") CategoriaEsperienza categoria,
                           @Param("adesso") LocalDateTime adesso);
}
