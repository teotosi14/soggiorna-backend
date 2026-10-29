package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Intervallo di date in cui un host blocca manualmente un alloggio, oltre al blocco
 * automatico dato dalle prenotazioni. Esposta da {@code AlloggioController} sotto
 * l'endpoint "/disponibilita" (RF-06).
 */
@Entity
@Table(name = "blocco")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Blocco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alloggio", nullable = false)
    private Alloggio alloggio;

    @Column(name = "data_inizio", nullable = false)
    private LocalDate dataInizio;

    @Column(name = "data_fine", nullable = false)
    private LocalDate dataFine;
}
