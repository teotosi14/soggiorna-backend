package it.unife.soggiorna.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** DTO per i periodi di indisponibilita' (RF-06) che l'host puo' dichiarare su un proprio alloggio. */
public class DisponibilitaDtos {

    public record DisponibilitaRequest(
            @NotNull LocalDate dataInizio,
            @NotNull LocalDate dataFine
    ) {}

    public record DisponibilitaView(Long id, LocalDate dataInizio, LocalDate dataFine) {}
}
