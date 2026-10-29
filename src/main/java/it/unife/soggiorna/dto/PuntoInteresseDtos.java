package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.CategoriaPuntoInteresse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** DTO di richiesta/risposta per la guida "Scopri la città" (gestita dall'amministratore). */
public class PuntoInteresseDtos {

    public record PuntoInteresseRequest(
            @NotBlank String citta,
            @NotBlank String nome,
            @NotNull CategoriaPuntoInteresse categoria,
            String descrizione,
            String indirizzo,
            String immagineUrl
    ) {}

    public record PuntoInteresseView(
            Long id,
            String citta,
            String nome,
            CategoriaPuntoInteresse categoria,
            String descrizione,
            String indirizzo,
            String immagineUrl
    ) {}

    /** Riepilogo di una città per il selettore della pagina "Scopri la città": foto di copertina
     *  (la prima disponibile) e numero di punti di interesse, senza dover caricare l'elenco completo. */
    public record CittaSommario(
            String citta,
            int numeroPunti,
            String immagineUrl
    ) {}
}
