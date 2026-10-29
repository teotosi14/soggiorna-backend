package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Voce della guida "Scopri la città": contenuto curato (dall'amministratore) su cosa fare/vedere in
 * una città, sostituisce l'elenco statico che prima viveva solo lato frontend in {@code attivita.js}.
 */
@Entity
@Table(name = "punto_interesse")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PuntoInteresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String citta;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaPuntoInteresse categoria;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(length = 200)
    private String indirizzo;

    @Column(name = "immagine_url", length = 500)
    private String immagineUrl;
}
