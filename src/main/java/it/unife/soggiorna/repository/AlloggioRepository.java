package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Alloggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Accesso dati per {@link Alloggio}: CRUD ereditato da JpaRepository + ricerca filtrata custom. */
public interface AlloggioRepository extends JpaRepository<Alloggio, Long> {

    List<Alloggio> findByHostId(Long hostId);

    // Ricerca con filtri opzionali (RF-09): i parametri null vengono ignorati. Se sia checkin che
    // checkout sono presenti, esclude gli alloggi con prenotazioni attive o periodi di indisponibilita'
    // (Blocco, RF-06) che si sovrappongono all'intervallo richiesto.
    // Le due condizioni NOT EXISTS ripetono, rispettivamente, la stessa condizione di sovrapposizione
    // di PrenotazioneRepository.contaSovrapposizioni (VR-02) e BloccoRepository.contaSovrapposizioni
    // (VR-11): JPQL non permette di richiamare un'altra @Query da qui, quindi se quelle regole
    // cambiano vanno aggiornate anche qui.
    @Query("""
            SELECT a FROM Alloggio a
            WHERE a.attivo = true
              AND (:citta IS NULL OR LOWER(a.citta) LIKE LOWER(CONCAT('%', :citta, '%')))
              AND (:ospiti IS NULL OR a.numOspitiMax >= :ospiti)
              AND (:prezzoMax IS NULL OR a.prezzoNotte <= :prezzoMax)
              AND (:checkin IS NULL OR :checkout IS NULL OR NOT EXISTS (
                    SELECT p FROM Prenotazione p
                    WHERE p.alloggio = a
                      AND p.stato IN (it.unife.soggiorna.model.StatoPrenotazione.CONFERMATA,
                                      it.unife.soggiorna.model.StatoPrenotazione.IN_ATTESA)
                      AND p.dataCheckin < :checkout AND p.dataCheckout > :checkin
              ))
              AND (:checkin IS NULL OR :checkout IS NULL OR NOT EXISTS (
                    SELECT b FROM Blocco b
                    WHERE b.alloggio = a
                      AND b.dataInizio < :checkout AND b.dataFine > :checkin
              ))
            ORDER BY a.dataPubblicazione DESC
            """)
    List<Alloggio> cerca(@Param("citta") String citta,
                         @Param("ospiti") Integer ospiti,
                         @Param("prezzoMax") BigDecimal prezzoMax,
                         @Param("checkin") LocalDate checkin,
                         @Param("checkout") LocalDate checkout);

    // Pannello Amministratore: quanti alloggi usano ancora un dato servizio (blocca l'eliminazione se > 0).
    @Query("SELECT COUNT(a) FROM Alloggio a JOIN a.servizi s WHERE s.id = :servizioId")
    long contaAlloggiConServizio(@Param("servizioId") Long servizioId);
}
