package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Veicolo pubblicato da un host per il noleggio (funzionalità "Noleggio auto"). Dominio gemello di
 * {@link Alloggio}/{@link Prenotazione} ma più snello: una sola immagine, nessun periodo di
 * indisponibilità separato (la disponibilità è dedotta solo dalle {@link PrenotazioneVeicolo} attive).
 */
@Entity
@Table(name = "veicolo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Veicolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_host", nullable = false)
    private Utente host;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 60)
    private String modello;

    @Column(nullable = false)
    private Integer anno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVeicolo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CambioVeicolo cambio;

    @Column(nullable = false)
    private Integer posti;

    @Column(name = "prezzo_giorno", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoGiorno;

    @Column(nullable = false, length = 100)
    private String citta;

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
