package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Wishlist;
import it.unife.soggiorna.model.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Accesso dati per {@link Wishlist} (preferiti), chiave primaria composta {@link WishlistId}. */
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
    List<Wishlist> findByUtenteId(Long utenteId);
}
