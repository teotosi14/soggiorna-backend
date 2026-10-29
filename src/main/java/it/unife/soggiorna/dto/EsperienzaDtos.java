package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.CategoriaEsperienza;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** DTO di richiesta/risposta per le esperienze: disaccoppiano l'API REST dall'entità JPA {@code Esperienza}. */
public class EsperienzaDtos {

    public record EsperienzaRequest(
            @NotBlank String titolo,
            String descrizione,
            @NotBlank String citta,
            @NotNull CategoriaEsperienza categoria,
            @NotNull @DecimalMin("0.0") BigDecimal prezzoPersona,
            @NotNull @Future LocalDateTime dataOra,
            @NotNull @Min(1) Integer durataOre,
            @NotNull @Min(1) Integer postiMax,
            String immagineUrl
    ) {}

    // postiDisponibili e' calcolato lato server (postiMax - partecipanti gia' prenotati, VR-20),
    // cosi' il frontend non deve ricostruire la logica di capienza residua.
    public record EsperienzaView(
            Long id,
            Long hostId,
            String hostNome,
            String titolo,
            String descrizione,
            String citta,
            CategoriaEsperienza categoria,
            BigDecimal prezzoPersona,
            LocalDateTime dataOra,
            Integer durataOre,
            Integer postiMax,
            Integer postiDisponibili,
            String immagineUrl,
            boolean attivo
    ) {}
}
