package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Persona registrata sulla piattaforma. Non c'e' distinzione strutturale tra "host" e
 * "guest": ogni utente puo' pubblicare alloggi (diventando host di quegli annunci) e
 * allo stesso tempo prenotare alloggi altrui (come guest).
 */
@Entity
@Table(name = "utente",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 80)
    private String cognome;

    @Column(length = 30)
    private String telefono;

    @Column(name = "data_registrazione", nullable = false)
    private LocalDate dataRegistrazione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuoloUtente ruolo;

    // Consente all'amministratore di sospendere un account (es. contenuti/comportamento scorretti)
    // senza doverlo eliminare: un utente sospeso non puo' piu' autenticarsi.
    @Column(nullable = false)
    private Boolean attivo;

    @PrePersist
    void prePersist() {
        if (dataRegistrazione == null) dataRegistrazione = LocalDate.now();
        if (ruolo == null) ruolo = RuoloUtente.UTENTE;
        if (attivo == null) attivo = true;
    }
}
