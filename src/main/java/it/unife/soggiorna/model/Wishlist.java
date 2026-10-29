package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/** Associazione N:M tra {@link Utente} e {@link Alloggio} per la lista dei preferiti; chiave primaria composta ({@link WishlistId}). */
@Entity
@Table(name = "wishlist")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wishlist {

    @EmbeddedId
    private WishlistId id;

    @MapsId("idUtente")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utente")
    private Utente utente;

    @MapsId("idAlloggio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alloggio")
    private Alloggio alloggio;

    @Column(name = "data_aggiunta", nullable = false)
    private LocalDate dataAggiunta;

    @PrePersist
    void prePersist() {
        if (dataAggiunta == null) dataAggiunta = LocalDate.now();
    }
}
