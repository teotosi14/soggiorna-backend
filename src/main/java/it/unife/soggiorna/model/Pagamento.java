package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Traccia il pagamento associato a una {@link Prenotazione} (relazione 1:0..1). Creato
 * automaticamente da {@code PrenotazioneService.conferma(...)} quando l'host conferma:
 * in questa demo non c'e' un gateway reale, il pagamento nasce gia' COMPLETATO.
 */
@Entity
@Table(name = "pagamento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prenotazione", nullable = false, unique = true)
    private Prenotazione prenotazione;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoPagamento stato;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @PrePersist
    void prePersist() {
        if (dataPagamento == null) dataPagamento = LocalDateTime.now();
        if (stato == null) stato = StatoPagamento.IN_ATTESA;
    }
}
