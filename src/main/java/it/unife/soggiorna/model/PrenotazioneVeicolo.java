package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Richiesta di noleggio di un guest ({@link Utente}) su un {@link Veicolo} per un intervallo di date.
 * Stesso ciclo di vita di {@link Prenotazione} (IN_ATTESA → CONFERMATA/ANNULLATA → COMPLETATA), per
 * questo riusa {@link StatoPrenotazione} invece di duplicare l'enum.
 */
@Entity
@Table(name = "prenotazione_veicolo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrenotazioneVeicolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_veicolo", nullable = false)
    private Veicolo veicolo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_guest", nullable = false)
    private Utente guest;

    @Column(name = "data_ritiro", nullable = false)
    private LocalDate dataRitiro;

    @Column(name = "data_riconsegna", nullable = false)
    private LocalDate dataRiconsegna;

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
