package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attività/tour organizzato da un host in una data e ora fissa, con un numero massimo di partecipanti
 * (funzionalità "Esplora esperienze"). A differenza di {@link Alloggio} non c'è un intervallo di date da
 * bloccare: la capienza residua si calcola sommando i partecipanti delle {@link PrenotazioneEsperienza} attive.
 */
@Entity
@Table(name = "esperienza")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Esperienza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_host", nullable = false)
    private Utente host;

    @Column(nullable = false, length = 150)
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false, length = 100)
    private String citta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaEsperienza categoria;

    @Column(name = "prezzo_persona", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoPersona;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra;

    @Column(name = "durata_ore", nullable = false)
    private Integer durataOre;

    @Column(name = "posti_max", nullable = false)
    private Integer postiMax;

    @Column(name = "immagine_url", length = 500)
    private String immagineUrl;

    @Column(name = "data_pubblicazione", nullable = false)
    private LocalDate dataPubblicazione;

    @Column(nullable = false)
    private Boolean attivo;

    @PrePersist
    void prePersist() {
        if (dataPubblicazione == null) dataPubblicazione = LocalDate.now();
        if (attivo == null) attivo = true;
    }
}
