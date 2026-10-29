package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entita' centrale del dominio: un annuncio pubblicato da un host (appartamento,
 * stanza privata o condivisa). Ha un proprietario ({@link Utente} host), un insieme
 * di {@link Servizio} offerti (N:M) e una galleria di {@link Foto}.
 */
@Entity
@Table(name = "alloggio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alloggio {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipologiaAlloggio tipologia;

    @Column(length = 200)
    private String indirizzo;

    @Column(nullable = false, length = 100)
    private String citta;

    @Column(length = 100)
    private String paese;

    @Column(length = 10)
    private String cap;

    @Column(name = "prezzo_notte", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoNotte;

    @Column(name = "num_ospiti_max", nullable = false)
    private Integer numOspitiMax;

    @Column(name = "num_camere")
    private Integer numCamere;

    @Column(name = "num_bagni")
    private Integer numBagni;

    @Column(name = "data_pubblicazione", nullable = false)
    private LocalDate dataPubblicazione;

    @Column(nullable = false)
    private Boolean attivo;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "alloggio_servizio",
            joinColumns = @JoinColumn(name = "id_alloggio"),
            inverseJoinColumns = @JoinColumn(name = "id_servizio"))
    private Set<Servizio> servizi = new HashSet<>();

    @PrePersist
    void prePersist() {
        if (dataPubblicazione == null) dataPubblicazione = LocalDate.now();
        if (attivo == null) attivo = true;
    }
}
