package it.unife.soggiorna.dto;

import it.unife.soggiorna.model.CategoriaPacchetto;
import java.math.BigDecimal;

/** DTO di risposta per le proposte di vacanza della pagina "Suggeriti". */
public class PacchettoDtos {

    public record PacchettoView(
            Long id,
            String titolo,
            String sottotitolo,
            String descrizione,
            String citta,
            CategoriaPacchetto categoria,
            Integer durataNotti,
            BigDecimal prezzoIndicativo,
            String immagineUrl
    ) {}
}
