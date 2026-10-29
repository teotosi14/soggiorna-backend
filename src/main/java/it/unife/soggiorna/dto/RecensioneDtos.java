package it.unife.soggiorna.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** DTO di richiesta/risposta per le recensioni; il rating e' vincolato tra 1 e 5 (VR-07) gia' a livello di validazione. */
public class RecensioneDtos {

    public record RecensioneRequest(
            @NotNull Long prenotazioneId,
            @NotNull @Min(1) @Max(5) Integer rating,
            String commento
    ) {}

    public record RecensioneView(
            Long id,
            Long prenotazioneId,
            Long alloggioId,
            String autoreNome,
            Integer rating,
            String commento,
            LocalDate data
    ) {}
}
