package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.CategoriaSpesa;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** DTO di richiesta/risposta per il registro spese di viaggio legato a una prenotazione. */
public class SpesaDtos {

    public record SpesaRequest(
            @NotNull CategoriaSpesa categoria,
            @NotNull @DecimalMin("0.0") BigDecimal importo,
            String descrizione,
            LocalDate data
    ) {}

    public record SpesaView(
            Long id,
            CategoriaSpesa categoria,
            BigDecimal importo,
            String descrizione,
            LocalDate data
    ) {}

    // Il totale e' calcolato dal server (non e' solo una somma banale: e' la fonte di verita' per il
    // frontend, coerente con l'approccio gia' usato per il prezzo di una prenotazione, VR-05).
    public record RiepilogoSpeseView(List<SpesaView> spese, BigDecimal totale) {}
}
