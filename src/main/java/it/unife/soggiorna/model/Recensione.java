package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/** Voto (1-5) e commento lasciati dal guest su una {@link Prenotazione} completata; relazione 1:0..1 con la prenotazione. */
@Entity
@Table(name = "recensione")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prenotazione", nullable = false, unique = true)
    private Prenotazione prenotazione;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String commento;

    @Column(nullable = false)
    private LocalDate data;

    @PrePersist
    void prePersist() {
        if (data == null) data = LocalDate.now();
    }
}
