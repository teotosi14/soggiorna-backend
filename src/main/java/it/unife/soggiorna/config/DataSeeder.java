package it.unife.soggiorna.config;

import it.unife.soggiorna.model.*;
import it.unife.soggiorna.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Popola il database con un catalogo demo ricco e diversificato su tutta Italia
 * (Nord/Centro/Sud/Isole): host e alloggi vari, foto, prenotazioni passate/future
 * e recensioni. Gira una sola volta, al primo avvio a database vuoto.
 */
@Configuration
public class DataSeeder {

    // Foto d'interni generiche (Unsplash) verificate, usate come seconda/terza foto
    // per completare la copertina specifica del luogo.
    private static final String INTERNO_ACCOGLIENTE = "1522708323590-d24dbb6b0267";
    private static final String INTERNO_BOHO = "1502672260266-1c1ef2d93688";
    private static final String INTERNO_LUMINOSO = "1560448204-e02f11c3d0e2";
    private static final String INTERNO_NORDICO = "1493809842364-78817add7ffb";
    private static final String ESTERNO_PISCINA = "1512917774080-9991f1c4c750";

    @Bean
    CommandLineRunner seed(UtenteRepository utenti,
                           ServizioRepository serviziRepo,
                           AlloggioRepository alloggiRepo,
                           FotoRepository fotoRepo,
                           PrenotazioneRepository prenotazioniRepo,
                           RecensioneRepository recensioniRepo,
                           VeicoloRepository veicoliRepo,
                           PrenotazioneVeicoloRepository prenotazioniVeicoloRepo,
                           EsperienzaRepository esperienzeRepo,
                           PrenotazioneEsperienzaRepository prenotazioniEsperienzaRepo,
                           PuntoInteresseRepository puntiInteresseRepo,
                           PacchettoVacanzaRepository pacchettiRepo,
                           PasswordEncoder encoder) {
        return args -> {
            if (utenti.count() > 0) return; // gia' popolato

            // ---- Amministratore ----
            utenti.save(Utente.builder()
                .email("admin@example.com").passwordHash(encoder.encode("password"))
                .nome("Admin").cognome("SpaceB&B").ruolo(RuoloUtente.ADMIN).build());

            // ---- Servizi ----
            Servizio wifi = serviziRepo.save(Servizio.builder().nome("WiFi").build());
            Servizio parcheggio = serviziRepo.save(Servizio.builder().nome("Parcheggio").build());
            Servizio cucina = serviziRepo.save(Servizio.builder().nome("Cucina").build());
            Servizio ac = serviziRepo.save(Servizio.builder().nome("Aria condizionata").build());
            Servizio lavatrice = serviziRepo.save(Servizio.builder().nome("Lavatrice").build());
            Servizio piscina = serviziRepo.save(Servizio.builder().nome("Piscina").build());
            Servizio vistaMare = serviziRepo.save(Servizio.builder().nome("Vista mare").build());
            Servizio animali = serviziRepo.save(Servizio.builder().nome("Animali ammessi").build());
            Servizio balcone = serviziRepo.save(Servizio.builder().nome("Balcone").build());
            Servizio ascensore = serviziRepo.save(Servizio.builder().nome("Ascensore").build());
            Servizio riscaldamento = serviziRepo.save(Servizio.builder().nome("Riscaldamento").build());

            // ---- Host (ognuno con uno o piu' alloggi) ----
            Utente marco = utenti.save(persona(encoder, "host@example.com", "Marco", "Rossi", "3331112233"));
            Utente giulia = utenti.save(persona(encoder, "giulia.ferrari@example.com", "Giulia", "Ferrari", "3335556677"));
            Utente luca = utenti.save(persona(encoder, "luca.bianchi@example.com", "Luca", "Bianchi", "3336667788"));
            Utente sofia = utenti.save(persona(encoder, "sofia.conti@example.com", "Sofia", "Conti", "3337778899"));
            Utente antonio = utenti.save(persona(encoder, "antonio.esposito@example.com", "Antonio", "Esposito", "3338889900"));
            Utente chiara = utenti.save(persona(encoder, "chiara.russo@example.com", "Chiara", "Russo", "3339990011"));
            Utente davide = utenti.save(persona(encoder, "davide.greco@example.com", "Davide", "Greco", "3340001122"));
            Utente elena = utenti.save(persona(encoder, "elena.marino@example.com", "Elena", "Marino", "3341112233"));
            Utente francesca = utenti.save(persona(encoder, "francesca.villa@example.com", "Francesca", "Villa", "3342223344"));
            Utente matteo = utenti.save(persona(encoder, "matteo.deluca@example.com", "Matteo", "De Luca", "3343334455"));
            Utente alessia = utenti.save(persona(encoder, "alessia.conte@example.com", "Alessia", "Conte", "3344445566"));
            Utente paolo = utenti.save(persona(encoder, "paolo.marini@example.com", "Paolo", "Marini", "3345556677"));
            Utente giorgio = utenti.save(persona(encoder, "giorgio.ferraro@example.com", "Giorgio", "Ferraro", "3346661122"));
            Utente beatrice = utenti.save(persona(encoder, "beatrice.neri@example.com", "Beatrice", "Neri", "3347772233"));
            Utente salvatore = utenti.save(persona(encoder, "salvatore.lombardo@example.com", "Salvatore", "Lombardo", "3348883344"));
            Utente camilla = utenti.save(persona(encoder, "camilla.bruno@example.com", "Camilla", "Bruno", "3349994455"));
            Utente vito = utenti.save(persona(encoder, "vito.colombo@example.com", "Vito", "Colombo", "3340005566"));
            Utente roberto = utenti.save(persona(encoder, "roberto.galli@example.com", "Roberto", "Galli", "3351112233"));
            Utente elisa = utenti.save(persona(encoder, "elisa.fabbri@example.com", "Elisa", "Fabbri", "3352223344"));
            Utente nicola = utenti.save(persona(encoder, "nicola.rizzo@example.com", "Nicola", "Rizzo", "3353334455"));
            Utente teresa = utenti.save(persona(encoder, "teresa.amato@example.com", "Teresa", "Amato", "3354445566"));
            Utente vincenzo = utenti.save(persona(encoder, "vincenzo.cuomo@example.com", "Vincenzo", "Cuomo", "3355556677"));
            Utente carla = utenti.save(persona(encoder, "carla.sabbatini@example.com", "Carla", "Sabbatini", "3356667788"));

            // ---- Guest ----
            Utente lucia = utenti.save(persona(encoder, "guest@example.com", "Lucia", "Bianchi", "3334445566"));
            Utente andrea = utenti.save(persona(encoder, "andrea.moretti@example.com", "Andrea", "Moretti", "3346667788"));
            Utente valentina = utenti.save(persona(encoder, "valentina.ricci@example.com", "Valentina", "Ricci", "3347778899"));
            Utente simone = utenti.save(persona(encoder, "simone.costa@example.com", "Simone", "Costa", "3348889900"));
            Utente martina = utenti.save(persona(encoder, "martina.gallo@example.com", "Martina", "Gallo", "3349990011"));
            Utente riccardo = utenti.save(persona(encoder, "riccardo.fontana@example.com", "Riccardo", "Fontana", "3350001122"));

            // ---- Alloggi (Nord, Centro, Sud, Isole) ----
            Alloggio a1 = alloggiRepo.save(Alloggio.builder()
                .host(marco).titolo("Bilocale nel centro storico")
                .descrizione("Accogliente bilocale a due passi dal Castello Estense.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Mazzini 10").citta("Ferrara").paese("Italia").cap("44121")
                .prezzoNotte(new BigDecimal("75.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina, riscaldamento)).build());
            attaccaFoto(fotoRepo, a1, "1648918648791-4ee1efba686f", INTERNO_ACCOGLIENTE);

            Alloggio a2 = alloggiRepo.save(Alloggio.builder()
                .host(marco).titolo("Stanza luminosa vicino all'universita'")
                .descrizione("Camera privata ideale per studenti e trasfertisti.")
                .tipologia(TipologiaAlloggio.STANZA_PRIVATA)
                .indirizzo("Corso Ercole I d'Este 32").citta("Ferrara").paese("Italia").cap("44121")
                .prezzoNotte(new BigDecimal("40.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, parcheggio)).build());
            attaccaFoto(fotoRepo, a2, "1745701867069-58c133bcac81", INTERNO_NORDICO);

            Alloggio a3 = alloggiRepo.save(Alloggio.builder()
                .host(giulia).titolo("Loft con vista sull'Arno")
                .descrizione("Loft luminoso all'ultimo piano con vista sul fiume, a pochi minuti da Ponte Vecchio.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Lungarno delle Grazie 4").citta("Firenze").paese("Italia").cap("50122")
                .prezzoNotte(new BigDecimal("120.00")).numOspitiMax(3).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, cucina, ac, ascensore)).build());
            attaccaFoto(fotoRepo, a3, "1543173947-4465841bd5e2", "1557509074-407e14f8166a", INTERNO_LUMINOSO);

            Alloggio a4 = alloggiRepo.save(Alloggio.builder()
                .host(giulia).titolo("Camera in casale nel Chianti")
                .descrizione("Camera privata in un casale di campagna circondato da vigne e uliveti.")
                .tipologia(TipologiaAlloggio.STANZA_PRIVATA)
                .indirizzo("Via del Chianti 22").citta("Greve in Chianti").paese("Italia").cap("50022")
                .prezzoNotte(new BigDecimal("55.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, parcheggio, cucina)).build());
            attaccaFoto(fotoRepo, a4, "1608476037397-7b53ace4c871", "1744909229894-883bb19b552f", INTERNO_ACCOGLIENTE);

            Alloggio a5 = alloggiRepo.save(Alloggio.builder()
                .host(luca).titolo("Attico vicino al Colosseo")
                .descrizione("Attico con terrazza panoramica a due passi dal Colosseo e dai Fori Imperiali.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Labicana 50").citta("Roma").paese("Italia").cap("00184")
                .prezzoNotte(new BigDecimal("150.00")).numOspitiMax(4).numCamere(2).numBagni(2)
                .servizi(Set.of(wifi, ac, ascensore, lavatrice)).build());
            attaccaFoto(fotoRepo, a5, "1552832230-c0197dd311b5", "1555992828-ca4dbe41d294", INTERNO_LUMINOSO);

            Alloggio a6 = alloggiRepo.save(Alloggio.builder()
                .host(luca).titolo("Monolocale nel cuore di Trastevere")
                .descrizione("Monolocale accogliente tra i vicoli caratteristici di Trastevere, vita notturna a due passi.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via della Scala 15").citta("Roma").paese("Italia").cap("00153")
                .prezzoNotte(new BigDecimal("90.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, cucina, ac)).build());
            attaccaFoto(fotoRepo, a6, "1526928008744-c86c7051aab6", "1514897083766-6d22cf909486", INTERNO_BOHO);

            Alloggio a7 = alloggiRepo.save(Alloggio.builder()
                .host(sofia).titolo("Appartamento affacciato sul canale")
                .descrizione("Appartamento autentico veneziano con vista su un canale tranquillo, lontano dalla folla turistica.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Fondamenta San Giacomo 120").citta("Venezia").paese("Italia").cap("30123")
                .prezzoNotte(new BigDecimal("135.00")).numOspitiMax(3).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, cucina, riscaldamento)).build());
            attaccaFoto(fotoRepo, a7, "1523906834658-6e24ef2386f9", "1558271736-cd043ef2e855", INTERNO_ACCOGLIENTE);

            Alloggio a8 = alloggiRepo.save(Alloggio.builder()
                .host(antonio).titolo("Casa nei Quartieri Spagnoli")
                .descrizione("Casa vivace nel cuore dei Quartieri Spagnoli, tra street art e la vera Napoli.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Toledo 200").citta("Napoli").paese("Italia").cap("80132")
                .prezzoNotte(new BigDecimal("70.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina, ac)).build());
            attaccaFoto(fotoRepo, a8, "1527208043690-e3db1cc93456", "1655480259894-cdcbb11b900f", INTERNO_BOHO);

            Alloggio a9 = alloggiRepo.save(Alloggio.builder()
                .host(antonio).titolo("Villetta con terrazza sulla Costiera")
                .descrizione("Villetta con terrazza panoramica sulla Costiera Amalfitana, tramonti mozzafiato garantiti.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Cristoforo Colombo 8").citta("Positano").paese("Italia").cap("84017")
                .prezzoNotte(new BigDecimal("210.00")).numOspitiMax(5).numCamere(2).numBagni(2)
                .servizi(Set.of(wifi, cucina, vistaMare, parcheggio)).build());
            attaccaFoto(fotoRepo, a9, "1583844056361-4418a8f2a985", "1561956021-947f09ae0101", ESTERNO_PISCINA);

            Alloggio a10 = alloggiRepo.save(Alloggio.builder()
                .host(chiara).titolo("Appartamento design ai Navigli")
                .descrizione("Appartamento di design a due passi dai Navigli, perfetto per la movida milanese.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Ripa di Porta Ticinese 45").citta("Milano").paese("Italia").cap("20143")
                .prezzoNotte(new BigDecimal("130.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, ac, ascensore, lavatrice)).build());
            attaccaFoto(fotoRepo, a10, "1718260872589-189e127a99b1", "1556983852-43bf21186b2a", INTERNO_NORDICO);

            Alloggio a11 = alloggiRepo.save(Alloggio.builder()
                .host(davide).titolo("Casa nel cuore della Kalsa")
                .descrizione("Casa caratteristica nel quartiere della Kalsa, tra mercati storici e street food palermitano.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Alloro 60").citta("Palermo").paese("Italia").cap("90133")
                .prezzoNotte(new BigDecimal("65.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina, ac)).build());
            attaccaFoto(fotoRepo, a11, "1533483268851-8975f8352671", "1683978345304-e12d6531b24f", INTERNO_ACCOGLIENTE);

            Alloggio a12 = alloggiRepo.save(Alloggio.builder()
                .host(elena).titolo("Bilocale a due passi dal Poetto")
                .descrizione("Bilocale luminoso a pochi minuti a piedi dalla spiaggia del Poetto.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Viale Poetto 88").citta("Cagliari").paese("Italia").cap("09126")
                .prezzoNotte(new BigDecimal("95.00")).numOspitiMax(3).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, ac, vistaMare, balcone)).build());
            attaccaFoto(fotoRepo, a12, "1558508006-b47365cf0e61", "1712060775370-a549fdd58ac4", INTERNO_BOHO);

            Alloggio a13 = alloggiRepo.save(Alloggio.builder()
                .host(francesca).titolo("Mansarda elegante in centro")
                .descrizione("Mansarda raffinata a pochi passi da Piazza Castello e dai musei del centro.")
                .tipologia(TipologiaAlloggio.STANZA_PRIVATA)
                .indirizzo("Via Po 30").citta("Torino").paese("Italia").cap("10124")
                .prezzoNotte(new BigDecimal("50.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, riscaldamento)).build());
            attaccaFoto(fotoRepo, a13, "1569355849432-0ed9d0a55bf5", "1611223155177-f4640a5e165b", INTERNO_LUMINOSO);

            Alloggio a14 = alloggiRepo.save(Alloggio.builder()
                .host(matteo).titolo("Casetta colorata sul mare delle Cinque Terre")
                .descrizione("Casetta tipica ligure a picco sul mare, punto di partenza ideale per le escursioni tra i borghi.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via San Giacomo 5").citta("Riomaggiore").paese("Italia").cap("19017")
                .prezzoNotte(new BigDecimal("160.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina, vistaMare)).build());
            attaccaFoto(fotoRepo, a14, "1530735606451-8f5f13955328", "1592396355679-1e2a094e8bf1", INTERNO_ACCOGLIENTE);

            Alloggio a15 = alloggiRepo.save(Alloggio.builder()
                .host(alessia).titolo("Masseria tra gli ulivi nel Salento")
                .descrizione("Masseria ristrutturata immersa negli ulivi secolari del Salento, a pochi km dal mare.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Contrada Ulivi 3").citta("Lecce").paese("Italia").cap("73100")
                .prezzoNotte(new BigDecimal("85.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, parcheggio, piscina, animali)).build());
            attaccaFoto(fotoRepo, a15, "1774281114964-2e3a969d500b", "1767622947473-3e119f12c534", ESTERNO_PISCINA);

            Alloggio a16 = alloggiRepo.save(Alloggio.builder()
                .host(paolo).titolo("Stanza sotto i portici")
                .descrizione("Camera in appartamento condiviso sotto i celebri portici bolognesi.")
                .tipologia(TipologiaAlloggio.STANZA_CONDIVISA)
                .indirizzo("Via Zamboni 15").citta("Bologna").paese("Italia").cap("40126")
                .prezzoNotte(new BigDecimal("30.00")).numOspitiMax(3).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina, lavatrice)).build());
            attaccaFoto(fotoRepo, a16, "1574615637286-a8005ec1fd94", "1730145612903-d5fe98515c47", INTERNO_BOHO);

            Alloggio a17 = alloggiRepo.save(Alloggio.builder()
                .host(giorgio).titolo("Attico con vista sull'Arena")
                .descrizione("Attico luminoso a due passi dall'Arena di Verona, ideale per l'opera e le sere d'estate.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Pallone 8").citta("Verona").paese("Italia").cap("37121")
                .prezzoNotte(new BigDecimal("140.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, ac, ascensore)).build());
            attaccaFoto(fotoRepo, a17, "1609858170613-7ba6b01a57d8", "1544537041-a514f5918bd5", INTERNO_LUMINOSO);

            Alloggio a18 = alloggiRepo.save(Alloggio.builder()
                .host(beatrice).titolo("Camera in torre medievale su Piazza del Campo")
                .descrizione("Camera raffinata in un palazzo storico a pochi passi dalla piazza piu' bella d'Italia.")
                .tipologia(TipologiaAlloggio.STANZA_PRIVATA)
                .indirizzo("Via di Citta' 45").citta("Siena").paese("Italia").cap("53100")
                .prezzoNotte(new BigDecimal("60.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, riscaldamento)).build());
            attaccaFoto(fotoRepo, a18, "1626315334749-7abfa602fe5f", "1694600439901-6989d7cd70aa", INTERNO_ACCOGLIENTE);

            Alloggio a19 = alloggiRepo.save(Alloggio.builder()
                .host(salvatore).titolo("Villetta con vista sullo Ionio")
                .descrizione("Villetta con piscina e terrazza panoramica sul mare di Taormina, tra agrumeti e vista Etna.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Bagnoli Croce 12").citta("Taormina").paese("Italia").cap("98039")
                .prezzoNotte(new BigDecimal("175.00")).numOspitiMax(4).numCamere(2).numBagni(2)
                .servizi(Set.of(wifi, vistaMare, piscina, parcheggio)).build());
            attaccaFoto(fotoRepo, a19, "1523365154888-8a758819b722", "1506186773396-2a6370e52c1f", ESTERNO_PISCINA);

            Alloggio a20 = alloggiRepo.save(Alloggio.builder()
                .host(camilla).titolo("Loft nel Porto Antico")
                .descrizione("Loft moderno a due passi dall'Acquario e dal Porto Antico, cuore della Genova marinara.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Calata Cattaneo 3").citta("Genova").paese("Italia").cap("16126")
                .prezzoNotte(new BigDecimal("100.00")).numOspitiMax(3).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, cucina, ascensore)).build());
            attaccaFoto(fotoRepo, a20, "1709282905838-ddff269be8c4", "1622660591594-b18bfa7abe1b", INTERNO_NORDICO);

            Alloggio a21 = alloggiRepo.save(Alloggio.builder()
                .host(vito).titolo("Casa scavata nei Sassi")
                .descrizione("Casa suggestiva scavata nella roccia nel cuore dei Sassi di Matera, patrimonio UNESCO.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Fiorentini 20").citta("Matera").paese("Italia").cap("75100")
                .prezzoNotte(new BigDecimal("115.00")).numOspitiMax(3).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, riscaldamento, cucina)).build());
            attaccaFoto(fotoRepo, a21, "1630505392721-b7b545929b61", "1692372159006-3f0c0273b268", INTERNO_ACCOGLIENTE);

            // ---- Alloggi aggiuntivi per le destinazioni piu' popolari (Roma, Firenze, Venezia, Napoli, Positano, Matera) ----
            Alloggio a22 = alloggiRepo.save(Alloggio.builder()
                .host(roberto).titolo("Bilocale accogliente a Prati, vicino San Pietro")
                .descrizione("Bilocale ristrutturato nel quartiere Prati, a due passi da Piazza Risorgimento e dai Musei Vaticani.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Ottaviano 40").citta("Roma").paese("Italia").cap("00192")
                .prezzoNotte(new BigDecimal("105.00")).numOspitiMax(3).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, cucina, ac, ascensore)).build());
            attaccaFoto(fotoRepo, a22, "1603668279554-631d92eaef9b", "1696407868324-460a69a81c11", INTERNO_LUMINOSO);

            Alloggio a23 = alloggiRepo.save(Alloggio.builder()
                .host(giulia).titolo("Appartamento con vista sul Duomo")
                .descrizione("Appartamento al terzo piano con vista mozzafiato sulla cupola del Brunelleschi, nel cuore di Firenze.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via dei Servi 22").citta("Firenze").paese("Italia").cap("50122")
                .prezzoNotte(new BigDecimal("145.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina, ac, ascensore)).build());
            attaccaFoto(fotoRepo, a23, "1569867037413-e0c43dbd6e78", "1606230762486-988508c970dd", INTERNO_LUMINOSO);

            Alloggio a24 = alloggiRepo.save(Alloggio.builder()
                .host(elisa).titolo("Stanza in appartamento vicino a Santa Croce")
                .descrizione("Camera privata in appartamento condiviso, a pochi minuti a piedi dalla Basilica di Santa Croce e dall'Arno.")
                .tipologia(TipologiaAlloggio.STANZA_PRIVATA)
                .indirizzo("Borgo Santa Croce 18").citta("Firenze").paese("Italia").cap("50122")
                .prezzoNotte(new BigDecimal("48.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, riscaldamento)).build());
            attaccaFoto(fotoRepo, a24, "1622659637086-da4236ed2519", INTERNO_ACCOGLIENTE);

            Alloggio a25 = alloggiRepo.save(Alloggio.builder()
                .host(sofia).titolo("Casa con affaccio su Rio San Barnaba")
                .descrizione("Casa veneziana autentica affacciata su un rio tranquillo, tra Campo Santa Margherita e le Gallerie dell'Accademia.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Calle Lunga San Barnaba 8").citta("Venezia").paese("Italia").cap("30123")
                .prezzoNotte(new BigDecimal("155.00")).numOspitiMax(4).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina, riscaldamento)).build());
            attaccaFoto(fotoRepo, a25, "1498307833015-e7b400441eb8", "1519730870542-6fcb06050e78", INTERNO_ACCOGLIENTE);

            Alloggio a26 = alloggiRepo.save(Alloggio.builder()
                .host(nicola).titolo("Monolocale vicino a Piazza San Marco")
                .descrizione("Monolocale intimo a pochi passi da Piazza San Marco, ideale per una coppia in visita alla Serenissima.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Calle dei Fabbri 12").citta("Venezia").paese("Italia").cap("30124")
                .prezzoNotte(new BigDecimal("130.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, ac)).build());
            attaccaFoto(fotoRepo, a26, "1480548004877-593316be2bd5", INTERNO_NORDICO);

            Alloggio a27 = alloggiRepo.save(Alloggio.builder()
                .host(antonio).titolo("Appartamento nei vicoli del Rione Sanita'")
                .descrizione("Appartamento tipico partenopeo nel Rione Sanita', tra murales, botteghe storiche e la vera anima di Napoli.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Sanita' 55").citta("Napoli").paese("Italia").cap("80137")
                .prezzoNotte(new BigDecimal("60.00")).numOspitiMax(3).numCamere(2).numBagni(1)
                .servizi(Set.of(wifi, cucina)).build());
            attaccaFoto(fotoRepo, a27, "1776842234685-9f3696f1c5c6", "1761138414056-99c8e870d3a8", INTERNO_BOHO);

            Alloggio a28 = alloggiRepo.save(Alloggio.builder()
                .host(teresa).titolo("Bilocale con vista sul Golfo e Vesuvio")
                .descrizione("Bilocale luminoso al Vomero con terrazzino panoramico sul Golfo di Napoli e il Vesuvio.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Scarlatti 90").citta("Napoli").paese("Italia").cap("80129")
                .prezzoNotte(new BigDecimal("88.00")).numOspitiMax(3).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, ac, vistaMare, balcone)).build());
            attaccaFoto(fotoRepo, a28, "1610641819167-e9f4f393a7fe", INTERNO_LUMINOSO);

            Alloggio a29 = alloggiRepo.save(Alloggio.builder()
                .host(antonio).titolo("Casa a schiera con vista su Positano")
                .descrizione("Casa a schiera tipica della Costiera, con vista sulle terrazze colorate di Positano e il mare sottostante.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Pasitea 65").citta("Positano").paese("Italia").cap("84017")
                .prezzoNotte(new BigDecimal("190.00")).numOspitiMax(4).numCamere(2).numBagni(2)
                .servizi(Set.of(wifi, cucina, vistaMare)).build());
            attaccaFoto(fotoRepo, a29, "1689474629451-b02bf4261f9a", "1568282167464-cb0d811b05c2", ESTERNO_PISCINA);

            Alloggio a30 = alloggiRepo.save(Alloggio.builder()
                .host(vincenzo).titolo("Monolocale romantico vicino alla spiaggia")
                .descrizione("Monolocale accogliente a due passi dalla Spiaggia Grande di Positano, perfetto per una fuga romantica.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via del Brigantino 4").citta("Positano").paese("Italia").cap("84017")
                .prezzoNotte(new BigDecimal("165.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, ac, vistaMare)).build());
            attaccaFoto(fotoRepo, a30, "1732316480143-718fb6400c8a", INTERNO_ACCOGLIENTE);

            Alloggio a31 = alloggiRepo.save(Alloggio.builder()
                .host(vito).titolo("Grotta ristrutturata nei Sassi di Barisano")
                .descrizione("Antica grotta scavata nel tufo, ristrutturata con comfort moderni, nel suggestivo rione Sassi Barisano.")
                .tipologia(TipologiaAlloggio.INTERO)
                .indirizzo("Via Bruno Buozzi 12").citta("Matera").paese("Italia").cap("75100")
                .prezzoNotte(new BigDecimal("98.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, riscaldamento, ac)).build());
            attaccaFoto(fotoRepo, a31, "1660320392567-c46423ebf681", "1556978448-c6e09a11039a", INTERNO_ACCOGLIENTE);

            Alloggio a32 = alloggiRepo.save(Alloggio.builder()
                .host(carla).titolo("Camera con vista sui Sassi di Caveoso")
                .descrizione("Camera privata in palazzo storico con terrazza affacciata sul rione Sassi Caveoso, patrimonio UNESCO.")
                .tipologia(TipologiaAlloggio.STANZA_PRIVATA)
                .indirizzo("Via Madonna delle Virtu' 8").citta("Matera").paese("Italia").cap("75100")
                .prezzoNotte(new BigDecimal("65.00")).numOspitiMax(2).numCamere(1).numBagni(1)
                .servizi(Set.of(wifi, riscaldamento)).build());
            attaccaFoto(fotoRepo, a32, "1681741027825-14283f37d024", INTERNO_LUMINOSO);

            // ---- Prenotazioni completate + recensioni (storico) ----
            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a1, lucia,
                LocalDate.of(2025, 8, 10), LocalDate.of(2025, 8, 14), 2, 5,
                "Soggiorno fantastico, Marco e' stato super disponibile e l'appartamento e' proprio come nelle foto, a due passi dal Castello Estense.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a3, andrea,
                LocalDate.of(2025, 9, 5), LocalDate.of(2025, 9, 9), 2, 5,
                "Vista sull'Arno spettacolare, posizione perfetta per visitare Firenze a piedi. Consigliatissimo!");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a5, valentina,
                LocalDate.of(2025, 6, 20), LocalDate.of(2025, 6, 25), 3, 4,
                "Attico bellissimo e vicinissimo al Colosseo, unico neo un po' di rumore la sera ma nel complesso esperienza top.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a9, simone,
                LocalDate.of(2025, 7, 14), LocalDate.of(2025, 7, 19), 4, 5,
                "La terrazza con vista sulla costiera e' da sogno, Antonio ci ha consigliato i posti migliori dove mangiare. Torneremo sicuramente.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a7, martina,
                LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 4), 2, 4,
                "Svegliarsi con vista sul canale e' un'esperienza unica, un po' rumoroso di notte per i vaporetti ma lo rifarei.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a14, riccardo,
                LocalDate.of(2025, 8, 22), LocalDate.of(2025, 8, 26), 3, 5,
                "Le Cinque Terre sono magiche e questa casetta colorata e' il posto perfetto da cui partire per le escursioni.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a11, lucia,
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 13), 2, 4,
                "Casa caratteristica nel cuore della Kalsa, cibo di strada a due passi. Davide molto accogliente.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a12, andrea,
                LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 5), 2, 5,
                "Mare stupendo a due passi, appartamento pulitissimo. Vacanza perfetta.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a15, valentina,
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 6), 4, 5,
                "Tra gli ulivi secolari, silenzio assoluto e colazione con prodotti locali offerta da Alessia. Esperienza autentica.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a2, simone,
                LocalDate.of(2025, 3, 15), LocalDate.of(2025, 3, 17), 1, 3,
                "Stanza semplice ma pulita, buona per una notte di passaggio durante gli esami.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a22, andrea,
                LocalDate.of(2025, 9, 20), LocalDate.of(2025, 9, 24), 2, 5,
                "Zona di Prati tranquilla e ben collegata, dieci minuti a piedi da San Pietro. Roberto molto preciso con le indicazioni.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a25, valentina,
                LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 13), 3, 5,
                "Svegliarsi sul rio, lontano dalla folla di San Marco, e' stata la parte migliore del viaggio. Casa curata nei minimi dettagli.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a27, simone,
                LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 8), 2, 4,
                "Quartiere autentico e vivace, un po' rumoroso di giorno ma esperienza vera. Antonio disponibilissimo.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a29, riccardo,
                LocalDate.of(2025, 8, 15), LocalDate.of(2025, 8, 20), 4, 5,
                "Vista sulle terrazze di Positano da cartolina, scalinate ripide ma ne vale assolutamente la pena. Torneremo.");

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a31, martina,
                LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 4), 2, 5,
                "Dormire in una grotta scavata nel tufo e' un'esperienza incredibile, silenzio totale e fresco naturale. Vito super ospitale.");

            // Completata senza recensione (realismo: non tutti gli ospiti recensiscono)
            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a23, lucia,
                LocalDate.of(2025, 11, 5), LocalDate.of(2025, 11, 8), 2, null, null);

            // Completate senza recensione (realismo: non tutti gli ospiti recensiscono)
            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a10, martina,
                LocalDate.of(2025, 11, 10), LocalDate.of(2025, 11, 12), 2, null, null);
            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a6, riccardo,
                LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 3), 2, null, null);

            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a17, andrea,
                LocalDate.of(2025, 6, 12), LocalDate.of(2025, 6, 15), 2, 5,
                "Vista sull'Arena spettacolare, abbiamo assistito all'opera e poi siamo tornati a piedi in due minuti. Giorgio impeccabile.");
            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a19, valentina,
                LocalDate.of(2025, 7, 20), LocalDate.of(2025, 7, 25), 4, 5,
                "Piscina e vista mare da sogno, Taormina e' magica al tramonto. Salvatore ci ha consigliato le spiagge migliori.");
            prenotazioneCompletata(prenotazioniRepo, recensioniRepo, a21, martina,
                LocalDate.of(2025, 9, 8), LocalDate.of(2025, 9, 11), 2, null, null);

            // ---- Prenotazioni future confermate ----
            prenotazione(prenotazioniRepo, a4, lucia,
                LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 8), 2, StatoPrenotazione.CONFERMATA);
            prenotazione(prenotazioniRepo, a16, andrea,
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12), 1, StatoPrenotazione.CONFERMATA);
            prenotazione(prenotazioniRepo, a18, riccardo,
                LocalDate.of(2026, 8, 14), LocalDate.of(2026, 8, 16), 2, StatoPrenotazione.CONFERMATA);
            prenotazione(prenotazioniRepo, a26, simone,
                LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 8), 2, StatoPrenotazione.CONFERMATA);
            prenotazione(prenotazioniRepo, a32, andrea,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 3), 1, StatoPrenotazione.CONFERMATA);

            // ---- Prenotazioni future in attesa di conferma host ----
            prenotazione(prenotazioniRepo, a8, valentina,
                LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 23), 3, StatoPrenotazione.IN_ATTESA);
            prenotazione(prenotazioniRepo, a13, simone,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 3), 1, StatoPrenotazione.IN_ATTESA);
            prenotazione(prenotazioniRepo, a30, martina,
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 12), 2, StatoPrenotazione.IN_ATTESA);

            // ---- Una prenotazione annullata (realismo) ----
            prenotazione(prenotazioniRepo, a9, martina,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), 3, StatoPrenotazione.ANNULLATA);

            // ==================== Noleggio auto ====================
            // Tutta la flotta appartiene a un'unica agenzia partner (coerente con il testo della
            // pagina Noleggio). Le foto vengono da Wikimedia Commons e hanno nel nome del file la
            // marca+modello esatti, così l'immagine corrisponde sempre al titolo dell'auto.
            Utente agenzia = utenti.save(partner(encoder,
                "partner@italiarent.example.com", "ItaliaRent", "Autonoleggi", "0532900900"));

            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Fiat").modello("Panda").anno(2022)
                .tipo(TipoVeicolo.UTILITARIA).cambio(CambioVeicolo.MANUALE).posti(4)
                .prezzoGiorno(new BigDecimal("35.00")).citta("Ferrara")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Fiat_Panda_Type_319_Minimal_Grey_%281%29.jpg/960px-Fiat_Panda_Type_319_Minimal_Grey_%281%29.jpg")
                .build());
            Veicolo v2 = veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Volkswagen").modello("Golf").anno(2023)
                .tipo(TipoVeicolo.BERLINA).cambio(CambioVeicolo.AUTOMATICO).posti(5)
                .prezzoGiorno(new BigDecimal("55.00")).citta("Roma")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Volkswagen_Golf_VII_Shishi_02_2022-09-06.jpg/960px-Volkswagen_Golf_VII_Shishi_02_2022-09-06.jpg")
                .build());
            Veicolo v3 = veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Jeep").modello("Renegade").anno(2021)
                .tipo(TipoVeicolo.SUV).cambio(CambioVeicolo.AUTOMATICO).posti(5)
                .prezzoGiorno(new BigDecimal("70.00")).citta("Firenze")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Jeep_Renegade%2C_GIMS_2019%2C_Le_Grand-Saconnex_%28GIMS0538%29.jpg/960px-Jeep_Renegade%2C_GIMS_2019%2C_Le_Grand-Saconnex_%28GIMS0538%29.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Fiat").modello("500 Cabrio").anno(2023)
                .tipo(TipoVeicolo.CABRIO).cambio(CambioVeicolo.MANUALE).posti(4)
                .prezzoGiorno(new BigDecimal("60.00")).citta("Positano")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/2018_Fiat_500C_Anniversario_DSC_7090.jpg/960px-2018_Fiat_500C_Anniversario_DSC_7090.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Fiat").modello("Doblo").anno(2020)
                .tipo(TipoVeicolo.FURGONE).cambio(CambioVeicolo.MANUALE).posti(7)
                .prezzoGiorno(new BigDecimal("50.00")).citta("Milano")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/Fiat_Doblo%2C_BAS_24%2C_Brussels_%28P1170399-RR%29.jpg/960px-Fiat_Doblo%2C_BAS_24%2C_Brussels_%28P1170399-RR%29.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Citroen").modello("Grand C4 Picasso").anno(2021)
                .tipo(TipoVeicolo.MONOVOLUME).cambio(CambioVeicolo.AUTOMATICO).posti(7)
                .prezzoGiorno(new BigDecimal("58.00")).citta("Taormina")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/CITROEN_GRAND_C4_PICASSO_China_%282%29.jpg/960px-CITROEN_GRAND_C4_PICASSO_China_%282%29.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Toyota").modello("Yaris").anno(2022)
                .tipo(TipoVeicolo.UTILITARIA).cambio(CambioVeicolo.AUTOMATICO).posti(5)
                .prezzoGiorno(new BigDecimal("42.00")).citta("Bologna")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Toyota_Yaris_Hybrid_%28XP210%29_1X7A0353.jpg/960px-Toyota_Yaris_Hybrid_%28XP210%29_1X7A0353.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Volkswagen").modello("Polo").anno(2021)
                .tipo(TipoVeicolo.UTILITARIA).cambio(CambioVeicolo.MANUALE).posti(5)
                .prezzoGiorno(new BigDecimal("40.00")).citta("Verona")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Volkswagen_Polo_VI_%282021%29_IMG_5322.jpg/960px-Volkswagen_Polo_VI_%282021%29_IMG_5322.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Audi").modello("A4 Avant").anno(2020)
                .tipo(TipoVeicolo.BERLINA).cambio(CambioVeicolo.AUTOMATICO).posti(5)
                .prezzoGiorno(new BigDecimal("78.00")).citta("Torino")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Audi_A4_Avant_%28B9%29.jpg/960px-Audi_A4_Avant_%28B9%29.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("BMW").modello("Serie 3").anno(2021)
                .tipo(TipoVeicolo.BERLINA).cambio(CambioVeicolo.AUTOMATICO).posti(5)
                .prezzoGiorno(new BigDecimal("82.00")).citta("Roma")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/BMW_3_SERIES_SEDAN_%28G20%29_China.jpg/960px-BMW_3_SERIES_SEDAN_%28G20%29_China.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Volvo").modello("XC60").anno(2022)
                .tipo(TipoVeicolo.SUV).cambio(CambioVeicolo.AUTOMATICO).posti(5)
                .prezzoGiorno(new BigDecimal("88.00")).citta("Milano")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Volvo_XC60_%28II%29_Washington_DC_Metro_Area%2C_USA.jpg/960px-Volvo_XC60_%28II%29_Washington_DC_Metro_Area%2C_USA.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Nissan").modello("Qashqai").anno(2019)
                .tipo(TipoVeicolo.SUV).cambio(CambioVeicolo.MANUALE).posti(5)
                .prezzoGiorno(new BigDecimal("63.00")).citta("Genova")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/NISSAN_QASHQAI_%28J11%29_China.jpg/960px-NISSAN_QASHQAI_%28J11%29_China.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Mini").modello("Cooper Cabrio").anno(2020)
                .tipo(TipoVeicolo.CABRIO).cambio(CambioVeicolo.MANUALE).posti(4)
                .prezzoGiorno(new BigDecimal("68.00")).citta("Rimini")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/c/cc/2020_Mini_Cabrio_%28F57%29_Sidewalk_1X7A6810.jpg/960px-2020_Mini_Cabrio_%28F57%29_Sidewalk_1X7A6810.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Renault").modello("Trafic").anno(2019)
                .tipo(TipoVeicolo.FURGONE).cambio(CambioVeicolo.MANUALE).posti(9)
                .prezzoGiorno(new BigDecimal("65.00")).citta("Napoli")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/Kleintransporter_Renault_Trafic_%28III%29_dCi_115_in_Winterthur_%282017%29.jpg/960px-Kleintransporter_Renault_Trafic_%28III%29_dCi_115_in_Winterthur_%282017%29.jpg")
                .build());
            veicoliRepo.save(Veicolo.builder()
                .host(agenzia).marca("Ford").modello("Focus").anno(2021)
                .tipo(TipoVeicolo.BERLINA).cambio(CambioVeicolo.MANUALE).posti(5)
                .prezzoGiorno(new BigDecimal("48.00")).citta("Bari")
                .immagineUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Ford_Focus_MK4_sedan_001.jpg/960px-Ford_Focus_MK4_sedan_001.jpg")
                .build());

            prenotazioneVeicolo(prenotazioniVeicoloRepo, v2, lucia,
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 6), StatoPrenotazione.CONFERMATA);
            prenotazioneVeicolo(prenotazioniVeicoloRepo, v3, andrea,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 4), StatoPrenotazione.IN_ATTESA);

            // ==================== Esplora esperienze ====================
            // Le esperienze non sono pubblicate dagli utenti: appartengono a un organizzatore
            // partner (come la flotta auto), così l'utente le può solo prenotare, non creare.
            Utente organizzatore = utenti.save(partner(encoder,
                "partner@italiaexperience.example.com", "Italia Experience", "Tour & Attività", "0532900901"));

            Esperienza e1 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Giro guidato in bici sulle Mura di Ferrara")
                .descrizione("Un giro guidato di due ore sulle mura rinascimentali, con soste nei punti panoramici.")
                .citta("Ferrara").categoria(CategoriaEsperienza.TOUR)
                .prezzoPersona(new BigDecimal("18.00")).dataOra(fraGiorni(7, 10, 0))
                .durataOre(2).postiMax(10)
                .immagineUrl("https://images.unsplash.com/photo-1517649763962-0c623066013b?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e2 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Corso di cucina toscana con pranzo")
                .descrizione("Impariamo a fare la pasta fresca e la bistecca alla fiorentina, poi si mangia insieme.")
                .citta("Firenze").categoria(CategoriaEsperienza.CORSO)
                .prezzoPersona(new BigDecimal("65.00")).dataOra(fraGiorni(12, 11, 0))
                .durataOre(3).postiMax(8)
                .immagineUrl("https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e3 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Degustazione di vini nel Salento")
                .descrizione("Degustazione guidata di 5 vini locali con taglieri di prodotti tipici.")
                .citta("Lecce").categoria(CategoriaEsperienza.DEGUSTAZIONE)
                .prezzoPersona(new BigDecimal("30.00")).dataOra(fraGiorni(25, 18, 0))
                .durataOre(2).postiMax(12)
                .immagineUrl("https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e4 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Escursione tra i borghi delle Cinque Terre")
                .descrizione("Trekking guidato sul Sentiero Azzurro con soste fotografiche panoramiche.")
                .citta("Riomaggiore").categoria(CategoriaEsperienza.ESCURSIONE)
                .prezzoPersona(new BigDecimal("25.00")).dataOra(fraGiorni(20, 9, 0))
                .durataOre(4).postiMax(15)
                .immagineUrl("https://images.unsplash.com/photo-1533587851505-d119e13fa0d7?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e5 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Giro in barca sulla laguna di Venezia")
                .descrizione("Un giro in barca al tramonto lontano dalle rotte turistiche, tra le isole minori della laguna.")
                .citta("Venezia").categoria(CategoriaEsperienza.TOUR)
                .prezzoPersona(new BigDecimal("40.00")).dataOra(fraGiorni(3, 18, 30))
                .durataOre(2).postiMax(6)
                .immagineUrl("https://images.unsplash.com/photo-1523906834658-6e24ef2386f9?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e6 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Corso di pallavolo in spiaggia")
                .descrizione("Sessione sportiva di beach volley per tutti i livelli, attrezzatura inclusa.")
                .citta("Bologna").categoria(CategoriaEsperienza.SPORT)
                .prezzoPersona(new BigDecimal("15.00")).dataOra(fraGiorni(16, 17, 0))
                .durataOre(2).postiMax(10)
                .immagineUrl("https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e7 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Tour serale del Colosseo e dei Fori Imperiali")
                .descrizione("Un tour serale del Colosseo illuminato e dei Fori Imperiali, con una guida esperta che racconta la Roma antica lontano dalla folla del giorno.")
                .citta("Roma").categoria(CategoriaEsperienza.TOUR)
                .prezzoPersona(new BigDecimal("35.00")).dataOra(fraGiorni(9, 20, 0))
                .durataOre(2).postiMax(12)
                .immagineUrl("https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e8 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Degustazione di street food nei Quartieri Spagnoli")
                .descrizione("Un giro a piedi tra le friggitorie e i banchi storici dei Quartieri Spagnoli, assaggiando cuoppo, pizza fritta e sfogliatelle.")
                .citta("Napoli").categoria(CategoriaEsperienza.DEGUSTAZIONE)
                .prezzoPersona(new BigDecimal("28.00")).dataOra(fraGiorni(14, 18, 30))
                .durataOre(3).postiMax(10)
                .immagineUrl("https://images.unsplash.com/photo-1527208043690-e3db1cc93456?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e9 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Escursione in barca lungo la Costiera Amalfitana")
                .descrizione("Una gita in barca lungo la Costiera Amalfitana, con soste per un bagno nelle calette raggiungibili solo dal mare.")
                .citta("Positano").categoria(CategoriaEsperienza.ESCURSIONE)
                .prezzoPersona(new BigDecimal("55.00")).dataOra(fraGiorni(18, 9, 30))
                .durataOre(4).postiMax(8)
                .immagineUrl("https://images.unsplash.com/photo-1583844056361-4418a8f2a985?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e10 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Corso di cioccolateria con maestro cioccolatiere")
                .descrizione("Un laboratorio guidato da un maestro cioccolatiere per scoprire i segreti del gianduiotto, con degustazione finale.")
                .citta("Torino").categoria(CategoriaEsperienza.CORSO)
                .prezzoPersona(new BigDecimal("32.00")).dataOra(fraGiorni(11, 16, 0))
                .durataOre(2).postiMax(8)
                .immagineUrl("https://images.unsplash.com/photo-1569355849432-0ed9d0a55bf5?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e11 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Immersione subacquea a Isola Bella")
                .descrizione("Un'immersione guidata tra i fondali di Isola Bella, adatta anche a chi non ha mai fatto sub prima.")
                .citta("Taormina").categoria(CategoriaEsperienza.SPORT)
                .prezzoPersona(new BigDecimal("70.00")).dataOra(fraGiorni(22, 10, 0))
                .durataOre(3).postiMax(6)
                .immagineUrl("https://images.unsplash.com/photo-1523365154888-8a758819b722?w=700&q=65&auto=format&fit=crop")
                .build());
            Esperienza e12 = esperienzeRepo.save(Esperienza.builder()
                .host(organizzatore).titolo("Tour fotografico serale dei Sassi")
                .descrizione("Un tour fotografico al tramonto tra i vicoli dei Sassi, con i punti di vista migliori per catturare la luce dorata sulla roccia.")
                .citta("Matera").categoria(CategoriaEsperienza.ALTRO)
                .prezzoPersona(new BigDecimal("20.00")).dataOra(fraGiorni(6, 19, 0))
                .durataOre(2).postiMax(14)
                .immagineUrl("https://images.unsplash.com/photo-1630505392721-b7b545929b61?w=700&q=65&auto=format&fit=crop")
                .build());

            prenotazioneEsperienza(prenotazioniEsperienzaRepo, e2, simone, 2, StatoPrenotazione.CONFERMATA);
            prenotazioneEsperienza(prenotazioniEsperienzaRepo, e4, riccardo, 3, StatoPrenotazione.IN_ATTESA);
            // Il guest demo (guest@example.com) ha una prenotazione visibile in "Le mie prenotazioni".
            prenotazioneEsperienza(prenotazioniEsperienzaRepo, e1, lucia, 2, StatoPrenotazione.CONFERMATA);

            // ==================== Guida "Scopri la citta'" ====================
            // Migrazione dei dati prima statici in frontend/js/attivita.js: ora persistiti e gestibili
            // dall'amministratore invece di essere hardcoded lato client.
            puntoInteresse(puntiInteresseRepo, "Ferrara", "Castello Estense e le sue torri", CategoriaPuntoInteresse.MONUMENTO,
                "Fortezza medievale nel cuore della città, con fossato, ponti levatoi e torri da cui si domina tutta Ferrara: la tappa obbligata di ogni visita.",
                "1648918648791-4ee1efba686f");
            puntoInteresse(puntiInteresseRepo, "Ferrara", "Giro in bici sulle Mura rinascimentali", CategoriaPuntoInteresse.NATURA,
                "Nove chilometri di mura intatte, tra le meglio conservate d'Europa: si percorrono in bici in un'oretta, tra prati e scorci sulla città vecchia.",
                "1745701867069-58c133bcac81");
            puntoInteresse(puntiInteresseRepo, "Ferrara", "Cattedrale di San Giorgio e centro medievale", CategoriaPuntoInteresse.MONUMENTO,
                "La facciata romanico-gotica della Cattedrale si affaccia sulla piazza principale, punto di partenza ideale per perdersi tra vicoli e portici del centro storico.",
                "1517649763962-0c623066013b");
            puntoInteresse(puntiInteresseRepo, "Firenze", "Ponte Vecchio e Uffizi", CategoriaPuntoInteresse.MONUMENTO,
                "Il ponte più fotografato di Firenze, con le sue botteghe orafe sospese sull'Arno, a due passi dalla Galleria degli Uffizi e dai suoi capolavori rinascimentali.",
                "1543173947-4465841bd5e2");
            puntoInteresse(puntiInteresseRepo, "Firenze", "Duomo, Battistero e Campanile di Giotto", CategoriaPuntoInteresse.MONUMENTO,
                "La cupola del Brunelleschi domina lo skyline della città: si può salire fino in cima per una vista che vale la fila.",
                "1557509074-407e14f8166a");
            puntoInteresse(puntiInteresseRepo, "Firenze", "Giardino di Boboli al tramonto", CategoriaPuntoInteresse.NATURA,
                "Il grande giardino all'italiana dietro Palazzo Pitti, tra statue, fontane e vialetti alberati: perfetto per una passeggiata al tramonto lontano dalla folla.",
                "1569867037413-e0c43dbd6e78");
            puntoInteresse(puntiInteresseRepo, "Greve in Chianti", "Degustazione di vino in una cantina locale", CategoriaPuntoInteresse.RISTORANTE,
                "Le colline del Chianti sono punteggiate di piccole cantine a conduzione familiare che aprono le porte per degustazioni guidate di Sangiovese.",
                "1608476037397-7b53ace4c871");
            puntoInteresse(puntiInteresseRepo, "Greve in Chianti", "Piazza del Mercatale e le botteghe del borgo", CategoriaPuntoInteresse.NEGOZIO,
                "La piazza a forma di imbuto nel cuore del borgo, circondata da portici e botteghe di prodotti tipici: il posto giusto per un aperitivo toscano.",
                "1744909229894-883bb19b552f");
            puntoInteresse(puntiInteresseRepo, "Greve in Chianti", "Trekking tra vigne e uliveti del Chianti", CategoriaPuntoInteresse.NATURA,
                "Sentieri segnalati che attraversano vigneti e uliveti secolari, con soste panoramiche sulle colline che hanno reso famoso il Chianti nel mondo.",
                "1608476037397-7b53ace4c871");
            puntoInteresse(puntiInteresseRepo, "Roma", "Colosseo e Fori Imperiali", CategoriaPuntoInteresse.MONUMENTO,
                "L'anfiteatro simbolo di Roma antica e l'area archeologica dei Fori, dove si cammina letteralmente sopra duemila anni di storia.",
                "1552832230-c0197dd311b5");
            puntoInteresse(puntiInteresseRepo, "Roma", "Fontana di Trevi e Pantheon", CategoriaPuntoInteresse.MONUMENTO,
                "La fontana barocca più famosa al mondo (con l'immancabile monetina di rito) e il Pantheon, il tempio romano meglio conservato, a pochi minuti a piedi l'uno dall'altra.",
                "1555992828-ca4dbe41d294");
            puntoInteresse(puntiInteresseRepo, "Roma", "Musei Vaticani e Cappella Sistina", CategoriaPuntoInteresse.MONUMENTO,
                "Una delle collezioni d'arte più grandi al mondo, che culmina nella volta della Cappella Sistina affrescata da Michelangelo: conviene prenotare in anticipo.",
                "1526928008744-c86c7051aab6");
            puntoInteresse(puntiInteresseRepo, "Roma", "Passeggiata serale a Trastevere", CategoriaPuntoInteresse.EVENTO,
                "Vicoli acciottolati, trattorie storiche e vita notturna: Trastevere di sera è uno dei quartieri più autentici e vivaci della capitale.",
                "1514897083766-6d22cf909486");
            puntoInteresse(puntiInteresseRepo, "Venezia", "Piazza San Marco e il Palazzo Ducale", CategoriaPuntoInteresse.MONUMENTO,
                "Il salotto di Venezia, con la Basilica dorata, il Campanile e il Palazzo Ducale affacciati sulla laguna: il cuore simbolico della città.",
                "1523906834658-6e24ef2386f9");
            puntoInteresse(puntiInteresseRepo, "Venezia", "Giro in gondola lungo i canali", CategoriaPuntoInteresse.EVENTO,
                "Il modo più classico per scoprire Venezia dall'acqua, scivolando sotto ponti antichi e tra le facciate colorate dei palazzi affacciati sui canali.",
                "1558271736-cd043ef2e855");
            puntoInteresse(puntiInteresseRepo, "Venezia", "Isole di Murano e Burano", CategoriaPuntoInteresse.NATURA,
                "Murano per l'arte millenaria del vetro soffiato, Burano per le case coloratissime dei pescatori: due gite in barca facili da combinare in una giornata.",
                "1498307833015-e7b400441eb8");
            puntoInteresse(puntiInteresseRepo, "Napoli", "Centro storico e Spaccanapoli", CategoriaPuntoInteresse.MONUMENTO,
                "Il decumano che taglia in due il centro storico, patrimonio UNESCO: chiese barocche, presepi artigianali e la vita di strada napoletana in ogni vicolo.",
                "1527208043690-e3db1cc93456");
            puntoInteresse(puntiInteresseRepo, "Napoli", "Una vera pizza napoletana in pizzeria storica", CategoriaPuntoInteresse.RISTORANTE,
                "Le pizzerie storiche del centro tramandano la ricetta originale della Margherita da generazioni: una tappa obbligata per chi visita Napoli.",
                "1655480259894-cdcbb11b900f");
            puntoInteresse(puntiInteresseRepo, "Napoli", "Museo Archeologico Nazionale", CategoriaPuntoInteresse.MONUMENTO,
                "Una delle collezioni archeologiche più importanti al mondo, con i reperti recuperati da Pompei ed Ercolano: mosaici, affreschi e sculture uniche.",
                "1776842234685-9f3696f1c5c6");
            puntoInteresse(puntiInteresseRepo, "Positano", "Sentiero degli Dei", CategoriaPuntoInteresse.NATURA,
                "Il trekking panoramico più celebre della Costiera, sospeso tra mare e monti con vista su Positano e Capri: da percorrere preferibilmente al mattino presto.",
                "1583844056361-4418a8f2a985");
            puntoInteresse(puntiInteresseRepo, "Positano", "Spiaggia Grande e il lungomare", CategoriaPuntoInteresse.NATURA,
                "La spiaggia principale di Positano, con le case colorate arrampicate sulla collina come sfondo: il punto di partenza per ogni gita in barca lungo la costa.",
                "1561956021-947f09ae0101");
            puntoInteresse(puntiInteresseRepo, "Positano", "Gita in barca verso Capri", CategoriaPuntoInteresse.EVENTO,
                "Da Positano partono escursioni giornaliere in barca verso Capri e la Grotta Azzurra, con soste per un bagno nelle calette raggiungibili solo dal mare.",
                "1689474629451-b02bf4261f9a");
            puntoInteresse(puntiInteresseRepo, "Milano", "Duomo di Milano e le terrazze panoramiche", CategoriaPuntoInteresse.MONUMENTO,
                "La cattedrale gotica simbolo della città, con oltre 3000 statue: si può salire sulle terrazze per una vista ravvicinata sulle guglie e, nelle giornate limpide, sulle Alpi.",
                "1718260872589-189e127a99b1");
            puntoInteresse(puntiInteresseRepo, "Milano", "Aperitivo sui Navigli", CategoriaPuntoInteresse.RISTORANTE,
                "La zona dei Navigli si anima al tramonto tra bar, locali e l'aperitivo milanese per eccellenza, lungo i canali storici della città.",
                "1556983852-43bf21186b2a");
            puntoInteresse(puntiInteresseRepo, "Milano", "Pinacoteca di Brera", CategoriaPuntoInteresse.MONUMENTO,
                "Una delle gallerie d'arte più importanti d'Italia, nel cuore del quartiere artistico di Brera, con opere di Caravaggio, Raffaello e Mantegna.",
                "1718260872589-189e127a99b1");
            puntoInteresse(puntiInteresseRepo, "Palermo", "Mercato del Capo e street food locale", CategoriaPuntoInteresse.NEGOZIO,
                "Uno dei mercati storici di Palermo, tra i banchi urlati dei venditori (l'abbanniata) e lo street food siciliano: panelle, arancine e sfincione.",
                "1533483268851-8975f8352671");
            puntoInteresse(puntiInteresseRepo, "Palermo", "Cattedrale di Monreale", CategoriaPuntoInteresse.MONUMENTO,
                "Poco fuori Palermo, il Duomo di Monreale custodisce uno dei cicli di mosaici bizantini più imponenti al mondo, patrimonio UNESCO.",
                "1683978345304-e12d6531b24f");
            puntoInteresse(puntiInteresseRepo, "Palermo", "Passeggiata nel centro storico della Kalsa", CategoriaPuntoInteresse.MONUMENTO,
                "Il quartiere più antico di Palermo, tra chiese barocche, palazzi nobiliari e il lungomare del Foro Italico affacciato sul golfo.",
                "1533483268851-8975f8352671");
            puntoInteresse(puntiInteresseRepo, "Cagliari", "Spiaggia del Poetto", CategoriaPuntoInteresse.NATURA,
                "I quasi otto chilometri di spiaggia sabbiosa a due passi dal centro di Cagliari, con l'acqua trasparente tipica della Sardegna.",
                "1558508006-b47365cf0e61");
            puntoInteresse(puntiInteresseRepo, "Cagliari", "Quartiere storico di Castello", CategoriaPuntoInteresse.MONUMENTO,
                "Il quartiere fortificato in cima alla collina, con bastioni, torri pisane e una vista che spazia su tutto il golfo di Cagliari.",
                "1712060775370-a549fdd58ac4");
            puntoInteresse(puntiInteresseRepo, "Cagliari", "Vista panoramica dalla Sella del Diavolo", CategoriaPuntoInteresse.NATURA,
                "Il promontorio che chiude il golfo verso sud: un sentiero facile porta a una delle viste più belle sulla città e sul mare.",
                "1558508006-b47365cf0e61");
            puntoInteresse(puntiInteresseRepo, "Torino", "Mole Antonelliana e Museo del Cinema", CategoriaPuntoInteresse.MONUMENTO,
                "Il simbolo di Torino ospita il Museo Nazionale del Cinema; l'ascensore panoramico porta fino in cima per una vista sulla città e sulle Alpi.",
                "1569355849432-0ed9d0a55bf5");
            puntoInteresse(puntiInteresseRepo, "Torino", "Piazza Castello e i portici del centro", CategoriaPuntoInteresse.MONUMENTO,
                "Il cuore barocco di Torino, circondato da 18 km di portici che collegano piazze, teatri e palazzi reali senza mai bagnarsi.",
                "1611223155177-f4640a5e165b");
            puntoInteresse(puntiInteresseRepo, "Torino", "Cioccolato e caffe' nei locali storici", CategoriaPuntoInteresse.RISTORANTE,
                "Torino è la patria del gianduiotto e del bicerin: i caffè storici del centro conservano ancora gli arredi ottocenteschi originali.",
                "1569355849432-0ed9d0a55bf5");
            puntoInteresse(puntiInteresseRepo, "Riomaggiore", "Sentiero Azzurro tra i borghi delle Cinque Terre", CategoriaPuntoInteresse.NATURA,
                "Il cammino panoramico che collega i cinque borghi lungo la costa, tra vigneti terrazzati e vista sul mare a ogni tornante.",
                "1530735606451-8f5f13955328");
            puntoInteresse(puntiInteresseRepo, "Riomaggiore", "Giro in barca lungo la costa", CategoriaPuntoInteresse.EVENTO,
                "Il modo migliore per vedere le Cinque Terre da un'altra prospettiva: le facciate colorate dei borghi viste dal mare, raggiungibili solo così o a piedi.",
                "1592396355679-1e2a094e8bf1");
            puntoInteresse(puntiInteresseRepo, "Riomaggiore", "Tramonto sul porticciolo", CategoriaPuntoInteresse.NATURA,
                "Il piccolo porto di Riomaggiore, tra le barche colorate tirate in secca, è il punto di ritrovo ideale per ammirare il tramonto.",
                "1533587851505-d119e13fa0d7");
            puntoInteresse(puntiInteresseRepo, "Lecce", "Basilica di Santa Croce e il barocco leccese", CategoriaPuntoInteresse.MONUMENTO,
                "La facciata più scenografica del barocco leccese, scolpita nella pietra dorata locale: il simbolo architettonico della città.",
                "1774281114964-2e3a969d500b");
            puntoInteresse(puntiInteresseRepo, "Lecce", "Anfiteatro romano", CategoriaPuntoInteresse.MONUMENTO,
                "Nel cuore della piazza principale, i resti dell'anfiteatro romano riemergono tra i palazzi barocchi: una convivenza di epoche unica nel suo genere.",
                "1767622947473-3e119f12c534");
            puntoInteresse(puntiInteresseRepo, "Lecce", "Giro tra le masserie e gli ulivi del Salento", CategoriaPuntoInteresse.NATURA,
                "La campagna intorno a Lecce è punteggiata di masserie storiche circondate da ulivi secolari, spesso trasformate in agriturismi e cantine.",
                "1510812431401-41d2bd2722f3");
            puntoInteresse(puntiInteresseRepo, "Bologna", "Le Due Torri", CategoriaPuntoInteresse.MONUMENTO,
                "Gli iconici simboli di Bologna, Garisenda e degli Asinelli: si può salire sulla torre maggiore per una vista a 360° sui tetti rossi della città.",
                "1574615637286-a8005ec1fd94");
            puntoInteresse(puntiInteresseRepo, "Bologna", "Passeggiata sotto i portici (patrimonio UNESCO)", CategoriaPuntoInteresse.MONUMENTO,
                "Oltre 60 km di portici, patrimonio UNESCO, che permettono di attraversare la città riparati dalla pioggia o dal sole in ogni stagione.",
                "1730145612903-d5fe98515c47");
            puntoInteresse(puntiInteresseRepo, "Bologna", "Mercato delle Erbe e Quadrilatero gastronomico", CategoriaPuntoInteresse.NEGOZIO,
                "Il cuore gastronomico di Bologna: botteghe storiche, salumerie e il Mercato delle Erbe per assaggiare i prodotti tipici emiliani.",
                "1612872087720-bb876e2e67d1");
            puntoInteresse(puntiInteresseRepo, "Verona", "Arena di Verona e i suoi spettacoli lirici", CategoriaPuntoInteresse.EVENTO,
                "L'anfiteatro romano meglio conservato al mondo per gli spettacoli, oggi sede della celebre stagione lirica estiva: un'esperienza da vivere almeno una volta.",
                "1609858170613-7ba6b01a57d8");
            puntoInteresse(puntiInteresseRepo, "Verona", "Casa di Giulietta", CategoriaPuntoInteresse.MONUMENTO,
                "Il celebre balcone shakespeariano, meta di pellegrinaggio per gli innamorati di tutto il mondo, nel cuore del centro storico.",
                "1544537041-a514f5918bd5");
            puntoInteresse(puntiInteresseRepo, "Verona", "Passeggiata su Ponte Pietra e Castel San Pietro", CategoriaPuntoInteresse.MONUMENTO,
                "Il ponte romano più antico della città porta verso Castel San Pietro, da cui si gode una delle viste più belle su Verona e l'Adige.",
                "1609858170613-7ba6b01a57d8");
            puntoInteresse(puntiInteresseRepo, "Siena", "Piazza del Campo e Torre del Mangia", CategoriaPuntoInteresse.MONUMENTO,
                "La piazza a conchiglia più famosa d'Italia, sede del Palio: dalla Torre del Mangia si domina tutta la città e le colline circostanti.",
                "1626315334749-7abfa602fe5f");
            puntoInteresse(puntiInteresseRepo, "Siena", "Duomo di Siena", CategoriaPuntoInteresse.MONUMENTO,
                "Una delle cattedrali gotiche più spettacolari d'Italia, con la facciata in marmi policromi e un pavimento istoriato unico nel suo genere.",
                "1694600439901-6989d7cd70aa");
            puntoInteresse(puntiInteresseRepo, "Siena", "Palio di Siena (se il periodo coincide)", CategoriaPuntoInteresse.EVENTO,
                "Due volte l'anno, in luglio e agosto, la piazza si trasforma nella pista di una corsa di cavalli che dura 90 secondi ma coinvolge la città per giorni.",
                "1626315334749-7abfa602fe5f");
            puntoInteresse(puntiInteresseRepo, "Taormina", "Teatro Antico con vista sull'Etna", CategoriaPuntoInteresse.MONUMENTO,
                "Il teatro greco-romano meglio posizionato al mondo: le rappresentazioni si tengono con il Mar Ionio e l'Etna fumante come sfondo naturale.",
                "1523365154888-8a758819b722");
            puntoInteresse(puntiInteresseRepo, "Taormina", "Isola Bella e le sue calette", CategoriaPuntoInteresse.NATURA,
                "La piccola isola collegata alla terraferma da una lingua di sabbia, riserva naturale con calette di acqua cristallina ai piedi di Taormina.",
                "1506186773396-2a6370e52c1f");
            puntoInteresse(puntiInteresseRepo, "Taormina", "Passeggiata su Corso Umberto", CategoriaPuntoInteresse.NEGOZIO,
                "La via pedonale principale di Taormina, tra boutique, terrazze panoramiche e palazzi storici, da percorrere con calma al tramonto.",
                "1523365154888-8a758819b722");
            puntoInteresse(puntiInteresseRepo, "Genova", "Porto Antico e l'Acquario", CategoriaPuntoInteresse.MONUMENTO,
                "L'area riqualificata del vecchio porto ospita l'Acquario più grande d'Italia e offre passeggiate lungo il mare nel cuore della città.",
                "1709282905838-ddff269be8c4");
            puntoInteresse(puntiInteresseRepo, "Genova", "Caruggi del centro storico", CategoriaPuntoInteresse.MONUMENTO,
                "Il dedalo di vicoli stretti del centro storico medievale, uno dei più grandi d'Europa, tra botteghe, focaccerie e palazzi nobiliari nascosti.",
                "1622660591594-b18bfa7abe1b");
            puntoInteresse(puntiInteresseRepo, "Genova", "Boccadasse e il lungomare", CategoriaPuntoInteresse.NATURA,
                "L'antico borgo di pescatori inglobato nella città, con le barche colorate tirate sulla spiaggetta: la meta classica per una passeggiata o un gelato.",
                "1709282905838-ddff269be8c4");
            puntoInteresse(puntiInteresseRepo, "Matera", "Sassi di Matera (patrimonio UNESCO)", CategoriaPuntoInteresse.MONUMENTO,
                "I rioni scavati nella roccia calcarea, abitati ininterrottamente da millenni: uno dei paesaggi urbani più sorprendenti d'Italia, patrimonio UNESCO dal 1993.",
                "1630505392721-b7b545929b61");
            puntoInteresse(puntiInteresseRepo, "Matera", "Cattedrale e i belvedere panoramici", CategoriaPuntoInteresse.MONUMENTO,
                "Dalla Cattedrale, in cima al colle della Civita, si aprono i punti panoramici migliori per ammirare l'intero anfiteatro dei Sassi.",
                "1692372159006-3f0c0273b268");
            puntoInteresse(puntiInteresseRepo, "Matera", "Chiese rupestri scavate nella roccia", CategoriaPuntoInteresse.MONUMENTO,
                "Oltre 150 chiese scavate nella roccia tra Sassi e gravina, molte con affreschi bizantini ancora visibili nonostante i secoli.",
                "1660320392567-c46423ebf681");

            // ==================== Pagina "Suggeriti": proposte di vacanza curate ====================
            // Idee di soggiorno con durata/prezzo indicativi (non prenotabili direttamente): la
            // copertina riusa la foto principale di un alloggio reale della stessa citta', gia'
            // verificata, cosi' l'immagine e' sempre coerente col luogo.
            pacchetto(pacchettiRepo, "Weekend tra le mura di Ferrara",
                "Bici, Castello Estense e cucina emiliana in un weekend fuori porta",
                "Due giorni nella citta' rinascimentale patrimonio UNESCO: pedalata sulle Mura, visita al Castello Estense e cena a base di cappellacci di zucca.",
                "Ferrara", CategoriaPacchetto.CITTA_ARTE, 2, new BigDecimal("219.00"), "1648918648791-4ee1efba686f");
            pacchetto(pacchettiRepo, "Firenze: Rinascimento e vista sull'Arno",
                "Uffizi, Ponte Vecchio e tramonti dai lungarni",
                "Tre notti nel cuore di Firenze tra musei, botteghe artigiane e passeggiate serali lungo l'Arno, con una gita al Giardino di Boboli.",
                "Firenze", CategoriaPacchetto.CITTA_ARTE, 3, new BigDecimal("420.00"), "1543173947-4465841bd5e2");
            pacchetto(pacchettiRepo, "Venezia romantica in laguna",
                "Canali, gondole e le isole di Murano e Burano",
                "Un soggiorno intimo lontano dalla folla: passeggiate tra le calli, un giro in barca al tramonto e le isole colorate della laguna.",
                "Venezia", CategoriaPacchetto.ROMANTICO, 2, new BigDecimal("320.00"), "1523906834658-6e24ef2386f9");
            pacchetto(pacchettiRepo, "Roma imperiale: Colosseo e Fori",
                "Tre giorni tra storia antica e vita da Trastevere",
                "Colosseo, Fori Imperiali e Musei Vaticani di giorno, cene e passeggiate serali a Trastevere: il meglio della Capitale in un weekend lungo.",
                "Roma", CategoriaPacchetto.CITTA_ARTE, 3, new BigDecimal("480.00"), "1552832230-c0197dd311b5");
            pacchetto(pacchettiRepo, "Costiera Amalfitana da sogno",
                "Terrazze panoramiche, mare cristallino e gita a Capri",
                "Quattro notti a Positano con vista sulla Costiera, tra spiagge, il Sentiero degli Dei e un'escursione in barca verso Capri.",
                "Positano", CategoriaPacchetto.MARE, 4, new BigDecimal("890.00"), "1583844056361-4418a8f2a985");
            pacchetto(pacchettiRepo, "Sole e mare al Poetto di Cagliari",
                "Spiaggia, Sella del Diavolo e centro storico di Castello",
                "Tre giorni di relax in Sardegna: la spiaggia del Poetto a pochi passi, il quartiere storico di Castello e viste mozzafiato sul golfo.",
                "Cagliari", CategoriaPacchetto.MARE, 3, new BigDecimal("310.00"), "1558508006-b47365cf0e61");
            pacchetto(pacchettiRepo, "Verona, la citta' di Giulietta",
                "Arena, Casa di Giulietta e un weekend romantico",
                "Due notti tra i vicoli di Verona, con visita alla Casa di Giulietta, passeggiata su Ponte Pietra e, se il calendario lo permette, una sera all'Arena.",
                "Verona", CategoriaPacchetto.ROMANTICO, 2, new BigDecimal("310.00"), "1609858170613-7ba6b01a57d8");
            pacchetto(pacchettiRepo, "Siena e le colline toscane",
                "Piazza del Campo, Duomo e degustazioni nel Chianti",
                "Tre notti in un palazzo storico su Piazza del Campo, con una giornata dedicata alle cantine e ai borghi del Chianti poco distanti.",
                "Siena", CategoriaPacchetto.ENOGASTRONOMIA, 3, new BigDecimal("289.00"), "1626315334749-7abfa602fe5f");
            pacchetto(pacchettiRepo, "Taormina: mare, Etna e dolce vita",
                "Piscina, calette e vista sull'Etna dal Teatro Antico",
                "Quattro notti in Sicilia tra la piscina panoramica della villetta, le calette di Isola Bella e il Teatro Antico con vista sull'Etna.",
                "Taormina", CategoriaPacchetto.MARE, 4, new BigDecimal("750.00"), "1523365154888-8a758819b722");
            pacchetto(pacchettiRepo, "Matera, i Sassi patrimonio UNESCO",
                "Dormire in una casa scavata nella roccia",
                "Tre notti in un'autentica casa nei Sassi, tra chiese rupestri, belvedere panoramici e le atmosfere uniche della citta' della roccia.",
                "Matera", CategoriaPacchetto.AVVENTURA, 3, new BigDecimal("360.00"), "1630505392721-b7b545929b61");
            pacchetto(pacchettiRepo, "Genova tra caruggi e Porto Antico",
                "Acquario, centro storico e il mare della Liguria",
                "Tre notti a due passi dal Porto Antico, tra i caruggi del centro storico, l'Acquario e una tappa a Boccadasse per un gelato sul mare.",
                "Genova", CategoriaPacchetto.CITTA_ARTE, 3, new BigDecimal("310.00"), "1709282905838-ddff269be8c4");
            pacchetto(pacchettiRepo, "Napoli autentica tra storia e pizza",
                "Spaccanapoli, Museo Archeologico e la vera pizza napoletana",
                "Tre notti nel cuore pulsante di Napoli, tra il centro storico di Spaccanapoli, i tesori del Museo Archeologico e le pizzerie storiche.",
                "Napoli", CategoriaPacchetto.ENOGASTRONOMIA, 3, new BigDecimal("249.00"), "1527208043690-e3db1cc93456");

            System.out.println(">>> Dati demo caricati: " + alloggiRepo.count() + " alloggi in " + 19
                + " citta', " + utenti.count() + " utenti, " + prenotazioniRepo.count() + " prenotazioni, "
                + recensioniRepo.count() + " recensioni, " + veicoliRepo.count() + " veicoli, "
                + esperienzeRepo.count() + " esperienze, " + puntiInteresseRepo.count() + " punti di interesse, "
                + pacchettiRepo.count() + " pacchetti vacanza.");
            System.out.println(">>> Prova con host@example.com (host, alloggi a Ferrara), guest@example.com (guest) o admin@example.com (amministratore) - password: password");
        };
    }

    private Utente persona(PasswordEncoder encoder, String email, String nome, String cognome, String telefono) {
        return Utente.builder()
            .email(email).passwordHash(encoder.encode("password"))
            .nome(nome).cognome(cognome).telefono(telefono).build();
    }

    /** Agenzia partner (noleggio/esperienze): ruolo PARTNER, gestisce solo le prenotazioni ricevute. */
    private Utente partner(PasswordEncoder encoder, String email, String nome, String cognome, String telefono) {
        return Utente.builder()
            .email(email).passwordHash(encoder.encode("password"))
            .nome(nome).cognome(cognome).telefono(telefono).ruolo(RuoloUtente.PARTNER).build();
    }

    /** Collega le foto indicate (id Unsplash) all'alloggio; la prima e' la copertina. */
    private void attaccaFoto(FotoRepository fotoRepo, Alloggio alloggio, String... fotoIds) {
        for (int i = 0; i < fotoIds.length; i++) {
            fotoRepo.save(Foto.builder()
                .alloggio(alloggio)
                .url("https://images.unsplash.com/photo-" + fotoIds[i] + "?w=900&q=65&auto=format&fit=crop")
                .copertina(i == 0)
                .build());
        }
    }

    /** Prenotazione passata gia' completata; se {@code rating} non e' null, aggiunge anche la recensione collegata. */
    private void prenotazioneCompletata(PrenotazioneRepository prenotazioniRepo, RecensioneRepository recensioniRepo,
                                         Alloggio alloggio, Utente guest, LocalDate checkin, LocalDate checkout,
                                         int ospiti, Integer rating, String commento) {
        Prenotazione p = prenotazioniRepo.save(Prenotazione.builder()
            .alloggio(alloggio).guest(guest)
            .dataCheckin(checkin).dataCheckout(checkout).numOspiti(ospiti)
            .prezzoTotale(totale(alloggio, checkin, checkout))
            .stato(StatoPrenotazione.COMPLETATA)
            .build());
        if (rating != null) {
            recensioniRepo.save(Recensione.builder()
                .prenotazione(p).rating(rating).commento(commento).data(checkout.plusDays(1))
                .build());
        }
    }

    /** Prenotazione futura/annullata, senza recensione (non ancora possibile finche' non e' COMPLETATA). */
    private void prenotazione(PrenotazioneRepository prenotazioniRepo, Alloggio alloggio, Utente guest,
                               LocalDate checkin, LocalDate checkout, int ospiti, StatoPrenotazione stato) {
        prenotazioniRepo.save(Prenotazione.builder()
            .alloggio(alloggio).guest(guest)
            .dataCheckin(checkin).dataCheckout(checkout).numOspiti(ospiti)
            .prezzoTotale(totale(alloggio, checkin, checkout))
            .stato(stato)
            .build());
    }

    private BigDecimal totale(Alloggio alloggio, LocalDate checkin, LocalDate checkout) {
        long notti = ChronoUnit.DAYS.between(checkin, checkout);
        return alloggio.getPrezzoNotte().multiply(BigDecimal.valueOf(notti));
    }

    private void prenotazioneVeicolo(PrenotazioneVeicoloRepository repo, Veicolo v, Utente guest,
                                      LocalDate ritiro, LocalDate riconsegna, StatoPrenotazione stato) {
        long giorni = ChronoUnit.DAYS.between(ritiro, riconsegna);
        repo.save(PrenotazioneVeicolo.builder()
            .veicolo(v).guest(guest).dataRitiro(ritiro).dataRiconsegna(riconsegna)
            .prezzoTotale(v.getPrezzoGiorno().multiply(BigDecimal.valueOf(giorni)))
            .stato(stato).build());
    }

    private void prenotazioneEsperienza(PrenotazioneEsperienzaRepository repo, Esperienza e, Utente guest,
                                         int partecipanti, StatoPrenotazione stato) {
        repo.save(PrenotazioneEsperienza.builder()
            .esperienza(e).guest(guest).numPartecipanti(partecipanti)
            .prezzoTotale(e.getPrezzoPersona().multiply(BigDecimal.valueOf(partecipanti)))
            .stato(stato).build());
    }

    /** {@code fotoId} e' l'id Unsplash di una foto reale della stessa citta' gia' verificata altrove nel
     *  seed (alloggio/esperienza): evita di introdurre nuove URL non verificate. */
    private void puntoInteresse(PuntoInteresseRepository repo, String citta, String nome, CategoriaPuntoInteresse categoria,
                                 String descrizione, String fotoId) {
        repo.save(PuntoInteresse.builder().citta(citta).nome(nome).categoria(categoria).descrizione(descrizione)
            .immagineUrl(fotoId == null ? null : "https://images.unsplash.com/photo-" + fotoId + "?w=900&q=65&auto=format&fit=crop")
            .build());
    }

    /** Proposta di vacanza per la pagina "Suggeriti"; {@code fotoId} e' l'id Unsplash della copertina
     *  di un alloggio reale della stessa citta', gia' verificato, cosi' l'immagine e' sempre coerente. */
    private void pacchetto(PacchettoVacanzaRepository repo, String titolo, String sottotitolo, String descrizione,
                            String citta, CategoriaPacchetto categoria, int durataNotti, BigDecimal prezzoIndicativo,
                            String fotoId) {
        repo.save(PacchettoVacanza.builder()
            .titolo(titolo).sottotitolo(sottotitolo).descrizione(descrizione)
            .citta(citta).categoria(categoria).durataNotti(durataNotti).prezzoIndicativo(prezzoIndicativo)
            .immagineUrl("https://images.unsplash.com/photo-" + fotoId + "?w=900&q=65&auto=format&fit=crop")
            .build());
    }

    /**
     * Data/ora futura relativa al momento del seeding: le esperienze demo restano sempre nel
     * futuro (la ricerca pubblica mostra solo esperienze con dataOra > adesso), così i dati
     * non "scadono" col passare del tempo come accadeva con le date fisse.
     */
    private static LocalDateTime fraGiorni(int giorni, int ora, int minuti) {
        return LocalDateTime.now().plusDays(giorni).withHour(ora).withMinute(minuti).withSecond(0).withNano(0);
    }
}
