package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.RuoloUtente;
import jakarta.validation.constraints.*;

/** DTO per registrazione/login; {@code @Email}/{@code @NotBlank} ecc. delegano la validazione a Bean Validation. */
public class AuthDtos {

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 6, max = 100) String password,
            @NotBlank String nome,
            @NotBlank String cognome,
            String telefono
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            Long userId,
            String email,
            String nome,
            RuoloUtente ruolo
    ) {}
}
