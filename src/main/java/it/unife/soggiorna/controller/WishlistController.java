package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.WishlistDtos.WishlistView;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoint REST per i preferiti dell'utente autenticato: lista, aggiunta e rimozione. */
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final CurrentUser currentUser;

    public WishlistController(WishlistService wishlistService, CurrentUser currentUser) {
        this.wishlistService = wishlistService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<WishlistView> mia() {
        return wishlistService.mia(currentUser.get());
    }

    // RF-14
    @PostMapping("/{alloggioId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void aggiungi(@PathVariable Long alloggioId) {
        wishlistService.aggiungi(alloggioId, currentUser.get());
    }

    @DeleteMapping("/{alloggioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuovi(@PathVariable Long alloggioId) {
        wishlistService.rimuovi(alloggioId, currentUser.get());
    }
}
