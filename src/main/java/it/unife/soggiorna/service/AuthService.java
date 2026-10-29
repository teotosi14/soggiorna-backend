package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.AuthDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.UtenteRepository;
import it.unife.soggiorna.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registrazione e login: verifica/crea l'{@link Utente}, controlla la password con BCrypt e rilascia il JWT di sessione. */
@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (utenteRepository.existsByEmail(req.email())) {
            throw new BusinessException("Email gia' registrata");
        }
        Utente u = Utente.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .nome(req.nome())
                .cognome(req.cognome())
                .telefono(req.telefono())
                .build();
        u = utenteRepository.save(u);
        String token = jwtService.generateToken(u.getId(), u.getEmail());
        return new AuthResponse(token, u.getId(), u.getEmail(), u.getNome(), u.getRuolo());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Utente u = utenteRepository.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException("Credenziali non valide"));
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new BusinessException("Credenziali non valide");
        }
        if (!u.getAttivo()) {
            throw new BusinessException("Account sospeso: contatta l'amministrazione");
        }
        String token = jwtService.generateToken(u.getId(), u.getEmail());
        return new AuthResponse(token, u.getId(), u.getEmail(), u.getNome(), u.getRuolo());
    }
}
