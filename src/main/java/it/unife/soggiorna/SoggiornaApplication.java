package it.unife.soggiorna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto di ingresso dell'applicazione Spring Boot "SpaceB&amp;B": avvia il contesto,
 * che a sua volta configura il server web incorporato (porta 8080), la connessione
 * al database MySQL, la sicurezza JWT e — al primo avvio a schema vuoto — il
 * {@link it.unife.soggiorna.config.DataSeeder} popola i dati demo.
 */
@SpringBootApplication
public class SoggiornaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoggiornaApplication.class, args);
    }
}
