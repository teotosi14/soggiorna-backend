package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.UtenteDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consultazione e modifica dei dati anagrafici e della password dell'utente autenticato. */
@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public UtenteService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private ProfiloResponse toResponse(Utente u) {
        return new ProfiloResponse(u.getId(), u.getEmail(), u.getNome(), u.getCognome(),
                u.getTelefono(), u.getDataRegistrazione(), u.getRuolo());
    }

    @Transactional(readOnly = true)
    public ProfiloResponse profilo(Utente utente) {
        return toResponse(utente);
    }

    @Transactional
    public ProfiloResponse aggiornaProfilo(Utente utente, AggiornaProfiloRequest req) {
        utente.setNome(req.nome());
        utente.setCognome(req.cognome());
        utente.setTelefono(req.telefono());
        return toResponse(utenteRepository.save(utente));
    }

    @Transactional
    public void cambiaPassword(Utente utente, CambiaPasswordRequest req) {
        if (!passwordEncoder.matches(req.passwordAttuale(), utente.getPasswordHash())) {
            throw new BusinessException("Password attuale non corretta");
        }
        utente.setPasswordHash(passwordEncoder.encode(req.nuovaPassword()));
        utenteRepository.save(utente);
    }
}
