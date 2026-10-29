package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.CambioVeicolo;
import it.unife.soggiorna.model.TipoVeicolo;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO di richiesta/risposta per il noleggio auto: disaccoppiano l'API REST dall'entità JPA {@code Veicolo}. */
public class VeicoloDtos {

    public record VeicoloRequest(
            @NotBlank String marca,
            @NotBlank String modello,
            @NotNull @Min(1990) Integer anno,
            @NotNull TipoVeicolo tipo,
            @NotNull CambioVeicolo cambio,
            @NotNull @Min(1) Integer posti,
            @NotNull @DecimalMin("0.0") BigDecimal prezzoGiorno,
            @NotBlank String citta,
            String immagineUrl
    ) {}

    public record VeicoloView(
            Long id,
            Long hostId,
            String hostNome,
            String marca,
            String modello,
            Integer anno,
            TipoVeicolo tipo,
            CambioVeicolo cambio,
            Integer posti,
            BigDecimal prezzoGiorno,
            String citta,
            String immagineUrl,
            boolean attivo
    ) {}
}
