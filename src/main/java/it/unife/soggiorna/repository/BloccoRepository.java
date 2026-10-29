package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Blocco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Accesso dati per {@link Blocco} (CRUD standard + controllo sovrapposizioni). */
public interface BloccoRepository extends JpaRepository<Blocco, Long> {
    List<Blocco> findByAlloggioId(Long alloggioId);

    // VR-11: conta i periodi di indisponibilita' che si sovrappongono all'intervallo richiesto.
    // Stessa condizione ripetuta in AlloggioRepository.cerca per il filtro data della ricerca:
    // se cambia va aggiornata anche li'.
    @Query("""
            SELECT COUNT(b) FROM Blocco b
            WHERE b.alloggio.id = :alloggioId
              AND b.dataInizio < :checkout
              AND b.dataFine > :checkin
            """)
    long contaSovrapposizioni(@Param("alloggioId") Long alloggioId,
                              @Param("checkin") LocalDate checkin,
                              @Param("checkout") LocalDate checkout);
}
