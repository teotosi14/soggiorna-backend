package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.TipologiaAlloggio;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/** DTO di richiesta/risposta per alloggi, servizi e foto: disaccoppiano l'API REST dalle entita' JPA. */
public class AlloggioDtos {

    public record AlloggioRequest(
            @NotBlank String titolo,
            String descrizione,
            @NotNull TipologiaAlloggio tipologia,
            String indirizzo,
            @NotBlank String citta,
            String paese,
            String cap,
            @NotNull @DecimalMin("0.0") BigDecimal prezzoNotte,
            @NotNull @Min(1) Integer numOspitiMax,
            Integer numCamere,
            Integer numBagni,
            Set<Long> serviziIds
    ) {}

    public record ServizioView(Long id, String nome) {}

    public record FotoView(Long id, String url, String didascalia, boolean copertina) {}

    public record AlloggioView(
            Long id,
            Long hostId,
            String hostNome,
            String titolo,
            String descrizione,
            TipologiaAlloggio tipologia,
            String indirizzo,
            String citta,
            String paese,
            String cap,
            BigDecimal prezzoNotte,
            Integer numOspitiMax,
            Integer numCamere,
            Integer numBagni,
            boolean attivo,
            List<ServizioView> servizi,
            List<FotoView> foto
    ) {}

    public record FotoRequest(
            @NotBlank String url,
            String didascalia,
            boolean copertina
    ) {}

    // RF-09/RF-10: intervallo [dataInizio, dataFine) non disponibile per una nuova prenotazione,
    // che sia dovuto a una prenotazione attiva altrui o a un blocco impostato dall'host (Blocco).
    public record PeriodoNonDisponibileView(LocalDate dataInizio, LocalDate dataFine) {}
}
