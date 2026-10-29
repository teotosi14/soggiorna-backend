package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Voce di spesa personale registrata da un guest durante il soggiorno legato a una
 * {@link Prenotazione} (funzionalità "Spese di viaggio"). Non incide sul prezzo della prenotazione
 * (VR-05): è solo un tracciamento informativo del budget del viaggio.
 */
@Entity
@Table(name = "spesa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Spesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prenotazione", nullable = false)
    private Prenotazione prenotazione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaSpesa categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importo;

    @Column(length = 200)
    private String descrizione;

    @Column(nullable = false)
    private LocalDate data;

    @PrePersist
    void prePersist() {
        if (data == null) data = LocalDate.now();
    }
}
