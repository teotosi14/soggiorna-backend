package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;

/** Una fotografia (URL esterno) collegata a un {@link Alloggio}; al massimo una per alloggio funge da copertina. */
@Entity
@Table(name = "foto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alloggio", nullable = false)
    private Alloggio alloggio;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 200)
    private String didascalia;

    @Column(nullable = false)
    private Boolean copertina;

    @PrePersist
    void prePersist() {
        if (copertina == null) copertina = false;
    }
}
