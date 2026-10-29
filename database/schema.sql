-- =====================================================================
-- Soggiorna - Schema relazionale (MySQL 8.0)
-- Progetto congiunto Basi di Dati / Ingegneria dei Sistemi Web / IS
-- Schema in BCNF. Usabile per Basi di Dati con ddl-auto=validate lato Spring.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS soggiorna
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE soggiorna;

-- Eliminazione in ordine inverso alle dipendenze (per re-import puliti)
DROP TABLE IF EXISTS recensione;
DROP TABLE IF EXISTS pagamento;
DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS spesa;
DROP TABLE IF EXISTS prenotazione;
DROP TABLE IF EXISTS blocco;
DROP TABLE IF EXISTS foto;
DROP TABLE IF EXISTS alloggio_servizio;
DROP TABLE IF EXISTS alloggio;
DROP TABLE IF EXISTS servizio;
DROP TABLE IF EXISTS prenotazione_veicolo;
DROP TABLE IF EXISTS veicolo;
DROP TABLE IF EXISTS prenotazione_esperienza;
DROP TABLE IF EXISTS esperienza;
DROP TABLE IF EXISTS punto_interesse;
DROP TABLE IF EXISTS pacchetto_vacanza;
DROP TABLE IF EXISTS utente;

-- ---------------------------------------------------------------------
CREATE TABLE utente (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    email              VARCHAR(150) NOT NULL,
    password_hash      VARCHAR(255) NOT NULL,
    nome               VARCHAR(80)  NOT NULL,
    cognome            VARCHAR(80)  NOT NULL,
    telefono           VARCHAR(30),
    data_registrazione DATE         NOT NULL,
    ruolo              VARCHAR(20)  NOT NULL DEFAULT 'UTENTE',
    attivo             BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    UNIQUE KEY uq_utente_email (email),
    CONSTRAINT chk_utente_ruolo CHECK (ruolo IN ('UTENTE','ADMIN','PARTNER'))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
CREATE TABLE servizio (
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    nome VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_servizio_nome (nome)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
CREATE TABLE alloggio (
    id                 BIGINT         NOT NULL AUTO_INCREMENT,
    id_host            BIGINT         NOT NULL,
    titolo             VARCHAR(150)   NOT NULL,
    descrizione        TEXT,
    tipologia          VARCHAR(20)    NOT NULL,
    indirizzo          VARCHAR(200),
    citta              VARCHAR(100)   NOT NULL,
    paese              VARCHAR(100),
    cap                VARCHAR(10),
    prezzo_notte       DECIMAL(10,2)  NOT NULL,
    num_ospiti_max     INT            NOT NULL,
    num_camere         INT,
    num_bagni          INT,
    data_pubblicazione DATE           NOT NULL,
    attivo             BOOLEAN        NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_alloggio_host FOREIGN KEY (id_host) REFERENCES utente(id),
    CONSTRAINT chk_alloggio_tipologia
        CHECK (tipologia IN ('INTERO','STANZA_PRIVATA','STANZA_CONDIVISA')),
    CONSTRAINT chk_alloggio_prezzo  CHECK (prezzo_notte >= 0),
    CONSTRAINT chk_alloggio_ospiti  CHECK (num_ospiti_max >= 1)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabella ponte N:M alloggio <-> servizio
CREATE TABLE alloggio_servizio (
    id_alloggio BIGINT NOT NULL,
    id_servizio BIGINT NOT NULL,
    PRIMARY KEY (id_alloggio, id_servizio),
    CONSTRAINT fk_as_alloggio FOREIGN KEY (id_alloggio) REFERENCES alloggio(id) ON DELETE CASCADE,
    CONSTRAINT fk_as_servizio FOREIGN KEY (id_servizio) REFERENCES servizio(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
CREATE TABLE foto (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    id_alloggio BIGINT       NOT NULL,
    url         VARCHAR(500) NOT NULL,
    didascalia  VARCHAR(200),
    copertina   BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_foto_alloggio FOREIGN KEY (id_alloggio) REFERENCES alloggio(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
CREATE TABLE blocco (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    id_alloggio BIGINT NOT NULL,
    data_inizio DATE   NOT NULL,
    data_fine   DATE   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_blocco_alloggio FOREIGN KEY (id_alloggio) REFERENCES alloggio(id) ON DELETE CASCADE,
    CONSTRAINT chk_blocco_date CHECK (data_inizio < data_fine)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
CREATE TABLE prenotazione (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    id_alloggio    BIGINT        NOT NULL,
    id_guest       BIGINT        NOT NULL,
    data_checkin   DATE          NOT NULL,
    data_checkout  DATE          NOT NULL,
    num_ospiti     INT           NOT NULL,
    prezzo_totale  DECIMAL(10,2) NOT NULL,
    stato          VARCHAR(20)   NOT NULL,
    motivo_annullamento VARCHAR(255),
    data_creazione DATETIME      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pren_alloggio FOREIGN KEY (id_alloggio) REFERENCES alloggio(id),
    CONSTRAINT fk_pren_guest    FOREIGN KEY (id_guest)    REFERENCES utente(id),
    CONSTRAINT chk_pren_date    CHECK (data_checkin < data_checkout),   -- VR-01
    CONSTRAINT chk_pren_ospiti  CHECK (num_ospiti >= 1),
    CONSTRAINT chk_pren_stato
        CHECK (stato IN ('IN_ATTESA','CONFERMATA','ANNULLATA','COMPLETATA'))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
CREATE TABLE pagamento (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    id_prenotazione BIGINT        NOT NULL,
    importo         DECIMAL(10,2) NOT NULL,
    metodo          VARCHAR(20)   NOT NULL,
    stato           VARCHAR(20)   NOT NULL,
    data_pagamento  DATETIME      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_pagamento_prenotazione (id_prenotazione),            -- 1:0..1
    CONSTRAINT fk_pag_prenotazione FOREIGN KEY (id_prenotazione) REFERENCES prenotazione(id) ON DELETE CASCADE,
    CONSTRAINT chk_pag_metodo CHECK (metodo IN ('CARTA','PAYPAL','BONIFICO')),
    CONSTRAINT chk_pag_stato  CHECK (stato  IN ('IN_ATTESA','COMPLETATO','RIMBORSATO'))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
CREATE TABLE recensione (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    id_prenotazione BIGINT  NOT NULL,
    rating          INT     NOT NULL,
    commento        TEXT,
    data            DATE    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_recensione_prenotazione (id_prenotazione),           -- 1:0..1
    CONSTRAINT fk_rec_prenotazione FOREIGN KEY (id_prenotazione) REFERENCES prenotazione(id) ON DELETE CASCADE,
    CONSTRAINT chk_rec_rating CHECK (rating BETWEEN 1 AND 5)            -- VR-07
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabella ponte N:M utente <-> alloggio (preferiti)
CREATE TABLE wishlist (
    id_utente    BIGINT NOT NULL,
    id_alloggio  BIGINT NOT NULL,
    data_aggiunta DATE  NOT NULL,
    PRIMARY KEY (id_utente, id_alloggio),
    CONSTRAINT fk_wish_utente   FOREIGN KEY (id_utente)   REFERENCES utente(id)   ON DELETE CASCADE,
    CONSTRAINT fk_wish_alloggio FOREIGN KEY (id_alloggio) REFERENCES alloggio(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Registro spese di viaggio, legato a una prenotazione di alloggio (funzionalita' "Spese di viaggio")
CREATE TABLE spesa (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    id_prenotazione BIGINT        NOT NULL,
    categoria       VARCHAR(20)   NOT NULL,
    importo         DECIMAL(10,2) NOT NULL,
    descrizione     VARCHAR(200),
    data            DATE          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_spesa_prenotazione FOREIGN KEY (id_prenotazione) REFERENCES prenotazione(id) ON DELETE CASCADE,
    CONSTRAINT chk_spesa_categoria CHECK (categoria IN ('TRASPORTI','CIBO','ATTIVITA','SHOPPING','ALTRO')),
    CONSTRAINT chk_spesa_importo CHECK (importo >= 0)
) ENGINE=InnoDB;

-- =====================================================================
-- Noleggio auto (funzionalita' "Noleggio auto")
-- =====================================================================
CREATE TABLE veicolo (
    id                 BIGINT         NOT NULL AUTO_INCREMENT,
    id_host            BIGINT         NOT NULL,
    marca              VARCHAR(60)    NOT NULL,
    modello            VARCHAR(60)    NOT NULL,
    anno               INT            NOT NULL,
    tipo               VARCHAR(20)    NOT NULL,
    cambio             VARCHAR(20)    NOT NULL,
    posti              INT            NOT NULL,
    prezzo_giorno      DECIMAL(10,2)  NOT NULL,
    citta              VARCHAR(100)   NOT NULL,
    immagine_url       VARCHAR(500),
    data_pubblicazione DATE           NOT NULL,
    attivo             BOOLEAN        NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_veicolo_host FOREIGN KEY (id_host) REFERENCES utente(id),
    CONSTRAINT chk_veicolo_tipo CHECK (tipo IN ('UTILITARIA','BERLINA','SUV','MONOVOLUME','CABRIO','FURGONE')),
    CONSTRAINT chk_veicolo_cambio CHECK (cambio IN ('MANUALE','AUTOMATICO')),
    CONSTRAINT chk_veicolo_posti CHECK (posti >= 1),
    CONSTRAINT chk_veicolo_prezzo CHECK (prezzo_giorno >= 0)
) ENGINE=InnoDB;

CREATE TABLE prenotazione_veicolo (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    id_veicolo       BIGINT        NOT NULL,
    id_guest         BIGINT        NOT NULL,
    data_ritiro      DATE          NOT NULL,
    data_riconsegna  DATE          NOT NULL,
    prezzo_totale    DECIMAL(10,2) NOT NULL,
    stato            VARCHAR(20)   NOT NULL,
    data_creazione   DATETIME      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_prenv_veicolo FOREIGN KEY (id_veicolo) REFERENCES veicolo(id),
    CONSTRAINT fk_prenv_guest   FOREIGN KEY (id_guest)   REFERENCES utente(id),
    CONSTRAINT chk_prenv_date   CHECK (data_ritiro < data_riconsegna),   -- VR-16
    CONSTRAINT chk_prenv_stato
        CHECK (stato IN ('IN_ATTESA','CONFERMATA','ANNULLATA','COMPLETATA'))
) ENGINE=InnoDB;

-- =====================================================================
-- Esperienze (funzionalita' "Esplora esperienze")
-- =====================================================================
CREATE TABLE esperienza (
    id                 BIGINT         NOT NULL AUTO_INCREMENT,
    id_host            BIGINT         NOT NULL,
    titolo             VARCHAR(150)   NOT NULL,
    descrizione        TEXT,
    citta              VARCHAR(100)   NOT NULL,
    categoria          VARCHAR(20)    NOT NULL,
    prezzo_persona     DECIMAL(10,2)  NOT NULL,
    data_ora           DATETIME       NOT NULL,
    durata_ore         INT            NOT NULL,
    posti_max          INT            NOT NULL,
    immagine_url       VARCHAR(500),
    data_pubblicazione DATE           NOT NULL,
    attivo             BOOLEAN        NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_esperienza_host FOREIGN KEY (id_host) REFERENCES utente(id),
    CONSTRAINT chk_esp_categoria CHECK (categoria IN ('TOUR','CORSO','DEGUSTAZIONE','ESCURSIONE','SPORT','ALTRO')),
    CONSTRAINT chk_esp_prezzo CHECK (prezzo_persona >= 0),
    CONSTRAINT chk_esp_posti CHECK (posti_max >= 1)
) ENGINE=InnoDB;

CREATE TABLE prenotazione_esperienza (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    id_esperienza    BIGINT        NOT NULL,
    id_guest         BIGINT        NOT NULL,
    num_partecipanti INT           NOT NULL,
    prezzo_totale    DECIMAL(10,2) NOT NULL,
    stato            VARCHAR(20)   NOT NULL,
    data_creazione   DATETIME      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_prene_esperienza FOREIGN KEY (id_esperienza) REFERENCES esperienza(id),
    CONSTRAINT fk_prene_guest      FOREIGN KEY (id_guest)      REFERENCES utente(id),
    CONSTRAINT chk_prene_partecipanti CHECK (num_partecipanti >= 1),
    CONSTRAINT chk_prene_stato
        CHECK (stato IN ('IN_ATTESA','CONFERMATA','ANNULLATA','COMPLETATA'))
) ENGINE=InnoDB;

-- =====================================================================
-- Guida "Scopri la citta'" (contenuto curato dall'amministratore)
-- =====================================================================
CREATE TABLE punto_interesse (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    citta        VARCHAR(100) NOT NULL,
    nome         VARCHAR(150) NOT NULL,
    categoria    VARCHAR(20)  NOT NULL,
    descrizione  TEXT,
    indirizzo    VARCHAR(200),
    immagine_url VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT chk_pi_categoria CHECK (categoria IN ('MONUMENTO','RISTORANTE','EVENTO','NEGOZIO','NATURA','ALTRO'))
) ENGINE=InnoDB;

-- =====================================================================
-- Pagina "Suggeriti" (proposte di vacanza curate, contenuto editoriale come punto_interesse:
-- nessuna FK verso altre entita', sola lettura pubblica)
-- =====================================================================
CREATE TABLE pacchetto_vacanza (
    id                 BIGINT         NOT NULL AUTO_INCREMENT,
    titolo             VARCHAR(150)   NOT NULL,
    sottotitolo        VARCHAR(200)   NOT NULL,
    descrizione        TEXT,
    citta              VARCHAR(100)   NOT NULL,
    categoria          VARCHAR(20)    NOT NULL,
    durata_notti       INT            NOT NULL,
    prezzo_indicativo  DECIMAL(10,2)  NOT NULL,
    immagine_url       VARCHAR(500),
    attivo             BOOLEAN        NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT chk_pacchetto_categoria CHECK (categoria IN
        ('CITTA_ARTE','MARE','ROMANTICO','RELAX','AVVENTURA','ENOGASTRONOMIA','FAMIGLIA')),
    CONSTRAINT chk_pacchetto_notti  CHECK (durata_notti >= 1),
    CONSTRAINT chk_pacchetto_prezzo CHECK (prezzo_indicativo >= 0)
) ENGINE=InnoDB;

-- Indici utili alle query di ricerca/prenotazione
CREATE INDEX idx_alloggio_citta ON alloggio (citta);
CREATE INDEX idx_pren_alloggio_date ON prenotazione (id_alloggio, data_checkin, data_checkout);
CREATE INDEX idx_veicolo_citta ON veicolo (citta);
CREATE INDEX idx_prenv_veicolo_date ON prenotazione_veicolo (id_veicolo, data_ritiro, data_riconsegna);
CREATE INDEX idx_esperienza_citta ON esperienza (citta);
CREATE INDEX idx_pi_citta ON punto_interesse (citta);
