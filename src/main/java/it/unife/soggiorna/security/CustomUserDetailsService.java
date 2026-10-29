package it.unife.soggiorna.security;

import it.unife.soggiorna.model.RuoloUtente;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.UtenteRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Ponte tra Spring Security e il dominio: carica un {@link Utente} per email e lo espone come
 * UserDetails. Tutti gli utenti hanno ROLE_USER; chi ha {@code ruolo == ADMIN} ottiene anche
 * ROLE_ADMIN. Un utente sospeso ({@code attivo == false}) e' marcato disabled: il login lo
 * blocca esplicitamente (vedi AuthService) e {@link JwtAuthenticationFilter} rifiuta di
 * autenticare le richieste con un token gia' emesso, cosi' la sospensione ha effetto immediato
 * anche su sessioni gia' aperte.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    public CustomUserDetailsService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utente u = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));
        List<SimpleGrantedAuthority> autorita = u.getRuolo() == RuoloUtente.ADMIN
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(u.getEmail(), u.getPasswordHash(), u.getAttivo(), true, true, true, autorita);
    }
}
