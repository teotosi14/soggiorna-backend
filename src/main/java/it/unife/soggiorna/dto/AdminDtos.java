package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.RuoloUtente;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/** DTO ad uso esclusivo del pannello Amministratore: gestione servizi, utenti e moderazione contenuti. */
public class AdminDtos {

    public record ServizioRequest(@NotBlank String nome) {}

    public record UtenteAdminView(
            Long id,
            String email,
            String nome,
            String cognome,
            RuoloUtente ruolo,
            boolean attivo,
            LocalDate dataRegistrazione
    ) {}
}
