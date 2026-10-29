package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.StatoPrenotazione;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** DTO di richiesta/risposta per le prenotazioni di esperienze. */
public class PrenotazioneEsperienzaDtos {

    public record PrenotazioneEsperienzaRequest(
            @NotNull Long esperienzaId,
            @NotNull @Min(1) Integer numPartecipanti
    ) {}

    public record PrenotazioneEsperienzaView(
            Long id,
            Long esperienzaId,
            String esperienzaTitolo,
            LocalDateTime esperienzaDataOra,
            Long guestId,
            Integer numPartecipanti,
            BigDecimal prezzoTotale,
            StatoPrenotazione stato
    ) {}
}
