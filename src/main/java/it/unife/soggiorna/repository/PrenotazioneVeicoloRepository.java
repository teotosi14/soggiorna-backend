package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.PrenotazioneVeicolo;
import it.unife.soggiorna.model.StatoPrenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Accesso dati per {@link PrenotazioneVeicolo}: liste per guest/host + controllo sovrapposizioni date. */
public interface PrenotazioneVeicoloRepository extends JpaRepository<PrenotazioneVeicolo, Long> {

    List<PrenotazioneVeicolo> findByGuestId(Long guestId);

    List<PrenotazioneVeicolo> findByVeicoloHostId(Long hostId);

    // Calendario pubblico (mirror di PrenotazioneRepository.findByAlloggioIdAndStatoIn): date
    // occupate da noleggi attivi, cosi' il guest le vede prima ancora di tentare la prenotazione.
    List<PrenotazioneVeicolo> findByVeicoloIdAndStatoIn(Long veicoloId, List<StatoPrenotazione> stati);

    // VR-17: conta prenotazioni confermate/in attesa che si sovrappongono all'intervallo richiesto.
    // Stessa condizione ripetuta in VeicoloRepository.cerca (mirror di PrenotazioneRepository.contaSovrapposizioni).
    @Query("""
            SELECT COUNT(pv) FROM PrenotazioneVeicolo pv
            WHERE pv.veicolo.id = :veicoloId
              AND pv.stato IN (it.unife.soggiorna.model.StatoPrenotazione.CONFERMATA,
                               it.unife.soggiorna.model.StatoPrenotazione.IN_ATTESA)
              AND pv.dataRitiro < :dataRiconsegna
              AND pv.dataRiconsegna > :dataRitiro
            """)
    long contaSovrapposizioni(@Param("veicoloId") Long veicoloId,
                              @Param("dataRitiro") LocalDate dataRitiro,
                              @Param("dataRiconsegna") LocalDate dataRiconsegna);
}
