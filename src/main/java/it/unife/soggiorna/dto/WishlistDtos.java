package it.unife.soggiorna.dto;

import java.time.LocalDate;

/** DTO di risposta per la lista preferiti: solo i campi utili a mostrare la card dell'alloggio salvato. */
public class WishlistDtos {
    public record WishlistView(Long alloggioId, String titolo, String citta, LocalDate dataAggiunta) {}
}
