# SpaceB&B - Backend

Backend REST di **SpaceB&B**, una piattaforma di affitti brevi tra privati (sul modello di Airbnb) con noleggio auto, esperienze locali e guida alle città.
Progetto congiunto per gli esami di Ingegneria del Software, Basi di Dati e Ingegneria dei Sistemi Web (Università degli Studi di Ferrara).

Autori: Matteo Tosi (195958), Tommaso Pizzolato (196187)

Il frontend è in un repository separato: (https://github.com/teotosi14/soggiorna-frontend)

## Tecnologie

- Java 17, Spring Boot 3.2.5
- Spring Data JPA (Hibernate), Spring Security + JWT
- MySQL 8.0
- Maven

## Come avviarlo

1. **Database.** Con Docker:
   ```bash
   docker compose up -d
   ```
   Oppure, con un MySQL già installato:
   ```bash
   mysql -u root -p < database/schema.sql
   ```
2. **Backend** (serve JDK 17 e Maven):
   ```bash
   mvn spring-boot:run
   ```
   Le API sono su `http://localhost:8080`. Le credenziali del database sono in `src/main/resources/application.properties` (di default utente `root`, password `password`).
3. Al primo avvio vengono caricati dati di esempio.

## Utenti di prova

Password di tutti gli utenti di esempio: `password`

| Email | Uso |
|---|---|
| host@example.com | ha già alloggi pubblicati |
| guest@example.com | per ricerca e prenotazioni |

## Struttura del codice

```
src/main/java/it/unife/soggiorna/
├── controller/   API REST (un controller per risorsa)
├── service/      logica e regole di business
├── repository/   accesso al database (Spring Data JPA)
├── model/        entità JPA ed enum
├── dto/          oggetti di richiesta e risposta
├── security/     JWT e Spring Security
└── exception/    gestione uniforme degli errori
```

L'architettura è a strati (controller, service, repository): il controller riceve la richiesta, il service applica le regole di business, il repository legge e scrive sul database. Le entità non vengono mai esposte direttamente: l'API usa DTO.

## Autenticazione

Il login restituisce un token JWT (valido 24 ore). Le richieste protette usano l'header `Authorization: Bearer <token>`. Le API sono stateless: nessuna sessione sul server.

## API principali

| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | registrazione e login |
| GET | `/api/alloggi` | ricerca con filtri (città, date, ospiti, prezzo) |
| GET | `/api/alloggi/{id}` | dettaglio di un alloggio |
| POST | `/api/prenotazioni` | crea una prenotazione |
| PUT | `/api/prenotazioni/{id}/conferma`, `/rifiuta`, `/annulla` | gestione dello stato |
| POST | `/api/recensioni` | recensione (solo a soggiorno completato) |
| GET/POST/DELETE | `/api/wishlist` | preferiti |

Ci sono inoltre le API per veicoli, esperienze, spese di viaggio, punti di interesse, proposte di vacanza e il pannello admin (`/api/admin/**`, solo per l'amministratore).

## Test

```bash
mvn test
```

I test verificano le regole di business del service delle prenotazioni (JUnit 5 + Mockito, senza database).

## Documentazione

Nella cartella `relazione_esame` ci sono la relazione, il video della demo, i link (`LINKS.txt`) e il dump del database.
