package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.StatoPrenotazione;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO di richiesta/risposta per le prenotazioni di veicoli a noleggio. */
public class PrenotazioneVeicoloDtos {

    public record PrenotazioneVeicoloRequest(
            @NotNull Long veicoloId,
            @NotNull LocalDate dataRitiro,
            @NotNull LocalDate dataRiconsegna
    ) {}

    public record PrenotazioneVeicoloView(
            Long id,
            Long veicoloId,
            String veicoloMarca,
            String veicoloModello,
            Long guestId,
            LocalDate dataRitiro,
            LocalDate dataRiconsegna,
            BigDecimal prezzoTotale,
            StatoPrenotazione stato
    ) {}
}
