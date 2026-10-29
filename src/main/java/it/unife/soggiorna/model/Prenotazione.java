package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Richiesta di soggiorno di un guest ({@link Utente}) su un {@link Alloggio} per un
 * intervallo di date. Il ciclo di vita segue {@link StatoPrenotazione}: nasce IN_ATTESA,
 * l'host la porta a CONFERMATA (generando un {@link Pagamento}), puo' essere ANNULLATA
 * dal guest, e diventa COMPLETATA a soggiorno concluso (condizione per lasciare una {@link Recensione}).
 */
@Entity
@Table(name = "prenotazione")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alloggio", nullable = false)
    private Alloggio alloggio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_guest", nullable = false)
    private Utente guest;

    @Column(name = "data_checkin", nullable = false)
    private LocalDate dataCheckin;

    @Column(name = "data_checkout", nullable = false)
    private LocalDate dataCheckout;

    @Column(name = "num_ospiti", nullable = false)
    private Integer numOspiti;

    @Column(name = "prezzo_totale", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoTotale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoPrenotazione stato;

    // Valorizzato solo per annullamenti non richiesti dal guest (es. l'host disattiva l'alloggio),
    // cosi' il guest capisce perche' una prenotazione confermata e' sparita senza sua azione.
    @Column(name = "motivo_annullamento", length = 255)
    private String motivoAnnullamento;

    @Column(name = "data_creazione", nullable = false)
    private LocalDateTime dataCreazione;

    @PrePersist
    void prePersist() {
        if (dataCreazione == null) dataCreazione = LocalDateTime.now();
        if (stato == null) stato = StatoPrenotazione.IN_ATTESA;
    }
}
