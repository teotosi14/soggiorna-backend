package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.StatoPrenotazione;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO di richiesta/risposta per le prenotazioni; {@code recensito} nella view evita una chiamata separata dal frontend. */
public class PrenotazioneDtos {

    public record PrenotazioneRequest(
            @NotNull Long alloggioId,
            @NotNull LocalDate dataCheckin,
            @NotNull LocalDate dataCheckout,
            @NotNull @Min(1) Integer numOspiti
    ) {}

    public record PrenotazioneView(
            Long id,
            Long alloggioId,
            String alloggioTitolo,
            Long guestId,
            LocalDate dataCheckin,
            LocalDate dataCheckout,
            Integer numOspiti,
            BigDecimal prezzoTotale,
            StatoPrenotazione stato,
            boolean recensito,
            String motivoAnnullamento
    ) {}
}
