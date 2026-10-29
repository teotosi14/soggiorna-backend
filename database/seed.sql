-- Dati di esempio (eseguire dopo schema.sql).
-- Le password sono hash BCrypt di "password".
USE soggiorna;

INSERT INTO servizio (nome) VALUES
 ('WiFi'), ('Parcheggio'), ('Cucina'), ('Aria condizionata'), ('Lavatrice');

INSERT INTO utente (email, password_hash, nome, cognome, telefono, data_registrazione) VALUES
 ('host@example.com',  '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5tFxJ0eQy7Yp6mYwM0Z3WqU5b7Iu', 'Marco', 'Rossi',   '3331112233', CURDATE()),
 ('guest@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5tFxJ0eQy7Yp6mYwM0Z3WqU5b7Iu', 'Lucia', 'Bianchi', '3334445566', CURDATE());

INSERT INTO alloggio (id_host, titolo, descrizione, tipologia, indirizzo, citta, paese, cap, prezzo_notte, num_ospiti_max, num_camere, num_bagni, data_pubblicazione, attivo) VALUES
 (1, 'Bilocale nel centro storico', 'Accogliente bilocale a due passi dal Castello Estense.', 'INTERO', 'Via Mazzini 10', 'Ferrara', 'Italia', '44121', 75.00, 4, 2, 1, CURDATE(), TRUE),
 (1, 'Stanza luminosa vicino al campus', 'Camera privata ideale per studenti.', 'STANZA_PRIVATA', 'Corso Ercole I d''Este 32', 'Ferrara', 'Italia', '44121', 40.00, 2, 1, 1, CURDATE(), TRUE);

INSERT INTO alloggio_servizio (id_alloggio, id_servizio) VALUES
 (1, 1), (1, 3), (2, 1), (2, 2);
