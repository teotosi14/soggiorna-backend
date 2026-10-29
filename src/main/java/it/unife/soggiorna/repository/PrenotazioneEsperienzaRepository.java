package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.PrenotazioneEsperienza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/** Accesso dati per {@link PrenotazioneEsperienza}: liste per guest/host + somma partecipanti per la capienza (VR-20). */
public interface PrenotazioneEsperienzaRepository extends JpaRepository<PrenotazioneEsperienza, Long> {

    List<PrenotazioneEsperienza> findByGuestId(Long guestId);

    List<PrenotazioneEsperienza> findByEsperienzaHostId(Long hostId);

    // VR-20: somma dei partecipanti gia' prenotati (confermati/in attesa) su un'esperienza, per
    // calcolare quanti posti restano disponibili prima di accettare una nuova prenotazione.
    @Query("""
            SELECT COALESCE(SUM(pe.numPartecipanti), 0) FROM PrenotazioneEsperienza pe
            WHERE pe.esperienza.id = :esperienzaId
              AND pe.stato IN (it.unife.soggiorna.model.StatoPrenotazione.CONFERMATA,
                               it.unife.soggiorna.model.StatoPrenotazione.IN_ATTESA)
            """)
    long sommaPartecipanti(@Param("esperienzaId") Long esperienzaId);
}
