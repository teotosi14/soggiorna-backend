package it.unife.soggiorna.security;

import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.UtenteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/** Recupera l'entita' Utente associata al token JWT della richiesta corrente. */
@Component
public class CurrentUser {

    private final UtenteRepository utenteRepository;

    public CurrentUser(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public Utente get() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = (principal instanceof UserDetails ud) ? ud.getUsername() : principal.toString();
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente autenticato non trovato"));
    }
}
