package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.WishlistDtos.WishlistView;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.*;
import it.unife.soggiorna.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Gestione della lista preferiti: aggiungi/rimuovi sono idempotenti (nessun errore se lo stato e' gia' quello desiderato). */
@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final AlloggioRepository alloggioRepository;

    public WishlistService(WishlistRepository wishlistRepository, AlloggioRepository alloggioRepository) {
        this.wishlistRepository = wishlistRepository;
        this.alloggioRepository = alloggioRepository;
    }

    @Transactional
    public void aggiungi(Long alloggioId, Utente utente) {
        Alloggio a = alloggioRepository.findById(alloggioId)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato"));
        WishlistId wid = new WishlistId(utente.getId(), a.getId());
        if (!wishlistRepository.existsById(wid)) {
            wishlistRepository.save(Wishlist.builder().id(wid).utente(utente).alloggio(a).build());
        }
    }

    @Transactional
    public void rimuovi(Long alloggioId, Utente utente) {
        WishlistId wid = new WishlistId(utente.getId(), alloggioId);
        if (wishlistRepository.existsById(wid)) {
            wishlistRepository.deleteById(wid);
        }
    }

    @Transactional(readOnly = true)
    public List<WishlistView> mia(Utente utente) {
        return wishlistRepository.findByUtenteId(utente.getId()).stream()
                .map(w -> new WishlistView(
                        w.getAlloggio().getId(),
                        w.getAlloggio().getTitolo(),
                        w.getAlloggio().getCitta(),
                        w.getDataAggiunta()))
                .toList();
    }
}
