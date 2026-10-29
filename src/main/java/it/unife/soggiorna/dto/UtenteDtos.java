package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.RuoloUtente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** DTO per la consultazione/modifica dei dati del profilo dell'utente autenticato. */
public class UtenteDtos {

    public record ProfiloResponse(
            Long id,
            String email,
            String nome,
            String cognome,
            String telefono,
            LocalDate dataRegistrazione,
            RuoloUtente ruolo
    ) {}

    public record AggiornaProfiloRequest(
            @NotBlank String nome,
            @NotBlank String cognome,
            String telefono
    ) {}

    public record CambiaPasswordRequest(
            @NotBlank String passwordAttuale,
            @NotBlank @Size(min = 6, max = 100) String nuovaPassword
    ) {}
}
