package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.CambioVeicolo;
import it.unife.soggiorna.model.TipoVeicolo;
import it.unife.soggiorna.model.Veicolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Accesso dati per {@link Veicolo}: CRUD ereditato da JpaRepository + ricerca filtrata custom. */
public interface VeicoloRepository extends JpaRepository<Veicolo, Long> {

    List<Veicolo> findByHostId(Long hostId);

    // Ricerca con filtri opzionali (mirror di AlloggioRepository.cerca): se dataRitiro/dataRiconsegna
    // sono entrambe presenti, esclude i veicoli con prenotazioni attive che si sovrappongono.
    @Query("""
            SELECT v FROM Veicolo v
            WHERE v.attivo = true
              AND (:citta IS NULL OR LOWER(v.citta) LIKE LOWER(CONCAT('%', :citta, '%')))
              AND (:tipo IS NULL OR v.tipo = :tipo)
              AND (:cambio IS NULL OR v.cambio = :cambio)
              AND (:dataRitiro IS NULL OR :dataRiconsegna IS NULL OR NOT EXISTS (
                    SELECT pv FROM PrenotazioneVeicolo pv
                    WHERE pv.veicolo = v
                      AND pv.stato IN (it.unife.soggiorna.model.StatoPrenotazione.CONFERMATA,
                                       it.unife.soggiorna.model.StatoPrenotazione.IN_ATTESA)
                      AND pv.dataRitiro < :dataRiconsegna AND pv.dataRiconsegna > :dataRitiro
              ))
            ORDER BY v.dataPubblicazione DESC
            """)
    List<Veicolo> cerca(@Param("citta") String citta,
                        @Param("tipo") TipoVeicolo tipo,
                        @Param("cambio") CambioVeicolo cambio,
                        @Param("dataRitiro") LocalDate dataRitiro,
                        @Param("dataRiconsegna") LocalDate dataRiconsegna);
}
