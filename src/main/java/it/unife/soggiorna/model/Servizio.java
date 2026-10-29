package it.unife.soggiorna.model;

import jakarta.persistence.*;
import lombok.*;

/** Comfort/dotazione selezionabile per un alloggio (es. WiFi, Cucina, Piscina); catalogo condiviso tra tutti gli alloggi. */
@Entity
@Table(name = "servizio",
       uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Servizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;
}
