package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Prenotazione;
import it.unife.soggiorna.model.StatoPrenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Accesso dati per {@link Prenotazione}: liste per guest/host + query di controllo sovrapposizioni date. */
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    List<Prenotazione> findByGuestId(Long guestId);

    List<Prenotazione> findByAlloggioHostId(Long hostId);

    // Calendario pubblico (RF-09/RF-10): date occupate da prenotazioni attive di un alloggio,
    // senza esporre alcun dato del guest.
    List<Prenotazione> findByAlloggioIdAndStatoIn(Long alloggioId, List<StatoPrenotazione> stati);

    // VR-02: conta prenotazioni confermate/in attesa che si sovrappongono all'intervallo richiesto.
    // Stessa condizione (dataCheckin < checkout AND dataCheckout > checkin) ripetuta in
    // AlloggioRepository.cerca per filtrare la ricerca per data: se cambia va aggiornata anche li'.
    @Query("""
            SELECT COUNT(p) FROM Prenotazione p
            WHERE p.alloggio.id = :alloggioId
              AND p.stato IN (it.unife.soggiorna.model.StatoPrenotazione.CONFERMATA,
                              it.unife.soggiorna.model.StatoPrenotazione.IN_ATTESA)
              AND p.dataCheckin < :checkout
              AND p.dataCheckout > :checkin
            """)
    long contaSovrapposizioni(@Param("alloggioId") Long alloggioId,
                              @Param("checkin") LocalDate checkin,
                              @Param("checkout") LocalDate checkout);
}
