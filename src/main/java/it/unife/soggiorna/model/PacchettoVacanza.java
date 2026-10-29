package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Proposta di vacanza curata (dall'amministratore) mostrata nella pagina "Suggeriti": un'idea di
 * soggiorno con durata e prezzo indicativi, non collegata a una prenotazione vera e propria.
 */
@Entity
@Table(name = "pacchetto_vacanza")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PacchettoVacanza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titolo;

    @Column(nullable = false, length = 200)
    private String sottotitolo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false, length = 100)
    private String citta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaPacchetto categoria;

    @Column(name = "durata_notti", nullable = false)
    private Integer durataNotti;

    @Column(name = "prezzo_indicativo", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoIndicativo;

    @Column(name = "immagine_url", length = 500)
    private String immagineUrl;

    @Column(nullable = false)
    private Boolean attivo;

    @PrePersist
    void prePersist() {
        if (attivo == null) attivo = true;
    }
}
