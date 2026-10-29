package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Prenotazione di un guest ({@link Utente}) per una {@link Esperienza}, per un certo numero di
 * partecipanti. Stesso ciclo di vita di {@link Prenotazione}, riusa {@link StatoPrenotazione}.
 */
@Entity
@Table(name = "prenotazione_esperienza")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrenotazioneEsperienza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_esperienza", nullable = false)
    private Esperienza esperienza;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_guest", nullable = false)
    private Utente guest;

    @Column(name = "num_partecipanti", nullable = false)
    private Integer numPartecipanti;

    @Column(name = "prezzo_totale", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoTotale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoPrenotazione stato;

    @Column(name = "data_creazione", nullable = false)
    private LocalDateTime dataCreazione;

    @PrePersist
    void prePersist() {
        if (dataCreazione == null) dataCreazione = LocalDateTime.now();
        if (stato == null) stato = StatoPrenotazione.IN_ATTESA;
    }
}
