package it.unife.soggiorna.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;

/** Chiave primaria composta (id utente + id alloggio) della tabella ponte {@link Wishlist}. */
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class WishlistId implements Serializable {

    @Column(name = "id_utente")
    private Long idUtente;

    @Column(name = "id_alloggio")
    private Long idAlloggio;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WishlistId that)) return false;
        return Objects.equals(idUtente, that.idUtente)
                && Objects.equals(idAlloggio, that.idAlloggio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUtente, idAlloggio);
    }
}
