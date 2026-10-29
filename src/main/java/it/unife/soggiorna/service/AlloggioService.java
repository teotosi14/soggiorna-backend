package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.AlloggioDtos.*;
import it.unife.soggiorna.dto.DisponibilitaDtos.*;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.exception.ResourceNotFoundException;
import it.unife.soggiorna.model.*;
import it.unife.soggiorna.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Logica applicativa per gli annunci: ricerca pubblica, CRUD lato host, gestione foto e
 * periodi di indisponibilita' (RF-06). {@code caricaProprio} centralizza il controllo
 * "sei tu il proprietario?" usato da aggiorna/disattiva/aggiungiFoto/disponibilita per
 * impedire che un host modifichi annunci altrui.
 */
@Service
public class AlloggioService {

    private final AlloggioRepository alloggioRepository;
    private final ServizioRepository servizioRepository;
    private final FotoRepository fotoRepository;
    private final BloccoRepository bloccoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final String uploadDir;
    private final String publicBaseUrl;

    public AlloggioService(AlloggioRepository alloggioRepository,
                           ServizioRepository servizioRepository,
                           FotoRepository fotoRepository,
                           BloccoRepository bloccoRepository,
                           PrenotazioneRepository prenotazioneRepository,
                           @Value("${soggiorna.upload.dir}") String uploadDir,
                           @Value("${soggiorna.upload.public-base-url}") String publicBaseUrl) {
        this.alloggioRepository = alloggioRepository;
        this.servizioRepository = servizioRepository;
        this.fotoRepository = fotoRepository;
        this.bloccoRepository = bloccoRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.uploadDir = uploadDir;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Transactional(readOnly = true)
    public List<AlloggioView> cerca(String citta, Integer ospiti, BigDecimal prezzoMax,
                                    LocalDate checkin, LocalDate checkout) {
        // Bugfix: un intervallo checkin/checkout non valido (checkin >= checkout) veniva accettato
        // dalla ricerca — che quindi poteva mostrare un alloggio come "disponibile" per quelle date
        // — mentre la creazione della prenotazione lo avrebbe sempre rifiutato (VR-01). Coerente con
        // gli altri filtri opzionali malformati: qui viene semplicemente ignorato, non genera errore.
        if (checkin != null && checkout != null && !checkin.isBefore(checkout)) {
            checkin = null;
            checkout = null;
        }
        return Mappers.toViews(alloggioRepository.cerca(citta, ospiti, prezzoMax, checkin, checkout), fotoRepository);
    }

    @Transactional(readOnly = true)
    public AlloggioView get(Long id) {
        Alloggio a = alloggioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato: " + id));
        return Mappers.toView(a, fotoRepository.findByAlloggioId(id));
    }

    @Transactional(readOnly = true)
    public List<AlloggioView> deiMieiAlloggi(Long hostId) {
        return Mappers.toViews(alloggioRepository.findByHostId(hostId), fotoRepository);
    }

    @Transactional
    public AlloggioView crea(AlloggioRequest req, Utente host) {
        Alloggio a = Alloggio.builder()
                .host(host)
                .titolo(req.titolo())
                .descrizione(req.descrizione())
                .tipologia(req.tipologia())
                .indirizzo(req.indirizzo())
                .citta(req.citta())
                .paese(req.paese())
                .cap(req.cap())
                .prezzoNotte(req.prezzoNotte())
                .numOspitiMax(req.numOspitiMax())
                .numCamere(req.numCamere())
                .numBagni(req.numBagni())
                .servizi(risolviServizi(req.serviziIds()))
                .build();
        a = alloggioRepository.save(a);
        return Mappers.toView(a, List.of());
    }

    @Transactional
    public AlloggioView aggiorna(Long id, AlloggioRequest req, Utente host) {
        Alloggio a = caricaProprio(id, host);
        a.setTitolo(req.titolo());
        a.setDescrizione(req.descrizione());
        a.setTipologia(req.tipologia());
        a.setIndirizzo(req.indirizzo());
        a.setCitta(req.citta());
        a.setPaese(req.paese());
        a.setCap(req.cap());
        a.setPrezzoNotte(req.prezzoNotte());
        a.setNumOspitiMax(req.numOspitiMax());
        a.setNumCamere(req.numCamere());
        a.setNumBagni(req.numBagni());
        a.setServizi(risolviServizi(req.serviziIds()));
        return Mappers.toView(a, fotoRepository.findByAlloggioId(id));
    }

    private static final String MOTIVO_ALLOGGIO_DISATTIVATO = "L'host ha disattivato l'alloggio";

    @Transactional
    public void disattiva(Long id, Utente host) {
        Alloggio a = caricaProprio(id, host);
        a.setAttivo(false);
        // Le prenotazioni ancora attive non possono piu' essere onorate se l'annuncio sparisce:
        // vengono annullate con un motivo, cosi' il guest capisce perche' (non e' stato lui a farlo).
        prenotazioneRepository.findByAlloggioIdAndStatoIn(id, List.of(StatoPrenotazione.IN_ATTESA, StatoPrenotazione.CONFERMATA))
                .forEach(p -> {
                    p.setStato(StatoPrenotazione.ANNULLATA);
                    p.setMotivoAnnullamento(MOTIVO_ALLOGGIO_DISATTIVATO);
                });
    }

    @Transactional
    public void riattiva(Long id, Utente host) {
        Alloggio a = caricaProprio(id, host);
        a.setAttivo(true);
    }

    @Transactional
    public FotoView aggiungiFoto(Long alloggioId, FotoRequest req, Utente host) {
        Alloggio a = caricaProprio(alloggioId, host);
        Foto f = Foto.builder()
                .alloggio(a)
                .url(req.url())
                .didascalia(req.didascalia())
                .copertina(req.copertina())
                .build();
        f = fotoRepository.save(f);
        return new FotoView(f.getId(), f.getUrl(), f.getDidascalia(), f.getCopertina());
    }

    private static final Set<String> CONTENT_TYPE_AMMESSI = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final String UPLOAD_URL_PREFIX = "/uploads/alloggi/";

    // RF-05: upload reale di un file immagine (in alternativa all'inserimento di un URL esterno).
    @Transactional
    public FotoView caricaFoto(Long alloggioId, MultipartFile file, String didascalia, boolean copertina, Utente host) {
        Alloggio a = caricaProprio(alloggioId, host);
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nessun file caricato");
        }
        String contentType = file.getContentType();
        if (contentType == null || !CONTENT_TYPE_AMMESSI.contains(contentType)) {
            throw new BusinessException("Formato immagine non supportato (usa JPEG, PNG, WEBP o GIF)");
        }
        byte[] contenuto;
        try {
            contenuto = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile leggere il file caricato", e);
        }
        // Bugfix: il Content-Type multipart e' dichiarato dal client e non garantisce che il file
        // sia davvero un'immagine di quel formato (un client malevolo puo' dichiarare "image/jpeg"
        // e inviare qualunque byte). Si verifica anche la firma binaria dei primi byte, non solo
        // l'header. Controllo manuale invece di ImageIO.read: il JDK non include un decoder WEBP,
        // e aggiungere una libreria solo per questa verifica sarebbe sproporzionato.
        if (!firmaCorrisponde(contenuto, contentType)) {
            throw new BusinessException("Il file non e' un'immagine valida del formato dichiarato");
        }
        String estensione = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".gif";
        };
        String nomeFile = UUID.randomUUID() + estensione;
        try {
            Path cartella = Path.of(uploadDir, "alloggi", String.valueOf(alloggioId));
            Files.createDirectories(cartella);
            Files.write(cartella.resolve(nomeFile), contenuto);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile salvare il file caricato", e);
        }
        String url = publicBaseUrl + UPLOAD_URL_PREFIX + alloggioId + "/" + nomeFile;
        Foto f = Foto.builder().alloggio(a).url(url).didascalia(didascalia).copertina(copertina).build();
        f = fotoRepository.save(f);
        return new FotoView(f.getId(), f.getUrl(), f.getDidascalia(), f.getCopertina());
    }

    private static boolean firmaCorrisponde(byte[] c, String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> c.length >= 3 && (c[0] & 0xFF) == 0xFF && (c[1] & 0xFF) == 0xD8 && (c[2] & 0xFF) == 0xFF;
            case "image/png" -> c.length >= 8 && (c[0] & 0xFF) == 0x89 && c[1] == 'P' && c[2] == 'N' && c[3] == 'G'
                    && c[4] == 0x0D && c[5] == 0x0A && c[6] == 0x1A && c[7] == 0x0A;
            case "image/gif" -> c.length >= 6 && c[0] == 'G' && c[1] == 'I' && c[2] == 'F'
                    && c[3] == '8' && (c[4] == '7' || c[4] == '9') && c[5] == 'a';
            case "image/webp" -> c.length >= 12 && c[0] == 'R' && c[1] == 'I' && c[2] == 'F' && c[3] == 'F'
                    && c[8] == 'W' && c[9] == 'E' && c[10] == 'B' && c[11] == 'P';
            default -> false;
        };
    }

    // RF-05: rimozione di una singola foto (elimina anche il file su disco se caricato localmente).
    @Transactional
    public void rimuoviFoto(Long alloggioId, Long fotoId, Utente host) {
        caricaProprio(alloggioId, host);
        Foto f = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto non trovata: " + fotoId));
        assicuraAppartieneAdAlloggio(f.getAlloggio().getId(), alloggioId, "Foto non trovata: " + fotoId);
        String prefix = publicBaseUrl + UPLOAD_URL_PREFIX;
        if (f.getUrl().startsWith(prefix)) {
            // best-effort: se il file locale manca o non e' cancellabile, la riga in DB viene rimossa comunque
            try {
                Files.deleteIfExists(Path.of(uploadDir, "alloggi", f.getUrl().substring(prefix.length())));
            } catch (IOException ignored) { /* file gia' assente o non rimovibile: non blocca l'operazione */ }
        }
        fotoRepository.delete(f);
    }

    // RF-06: periodi di indisponibilita' definiti dall'host, indipendenti dalle prenotazioni.
    // La lettura e' pubblica (come foto/servizi): sapere che un alloggio e' bloccato in certe
    // date non e' un'informazione sensibile ed e' utile a chi visita la scheda dell'annuncio.
    @Transactional(readOnly = true)
    public List<DisponibilitaView> disponibilita(Long alloggioId) {
        return bloccoRepository.findByAlloggioId(alloggioId).stream()
                .map(b -> new DisponibilitaView(b.getId(), b.getDataInizio(), b.getDataFine()))
                .toList();
    }

    // RF-09/RF-10: calendario pubblico delle date non disponibili (prenotazioni attive + blocchi
    // host), cosi' il guest le vede gia' nel selettore date prima di tentare una prenotazione.
    // Non espone alcun dato del guest: solo gli intervalli [dataInizio, dataFine).
    @Transactional(readOnly = true)
    public List<PeriodoNonDisponibileView> dateOccupate(Long alloggioId) {
        List<PeriodoNonDisponibileView> daPrenotazioni = prenotazioneRepository
                .findByAlloggioIdAndStatoIn(alloggioId, List.of(StatoPrenotazione.CONFERMATA, StatoPrenotazione.IN_ATTESA))
                .stream()
                .map(p -> new PeriodoNonDisponibileView(p.getDataCheckin(), p.getDataCheckout()))
                .toList();
        List<PeriodoNonDisponibileView> daBlocchi = bloccoRepository.findByAlloggioId(alloggioId).stream()
                .map(b -> new PeriodoNonDisponibileView(b.getDataInizio(), b.getDataFine()))
                .toList();
        return Stream.concat(daPrenotazioni.stream(), daBlocchi.stream()).toList();
    }

    @Transactional
    public DisponibilitaView aggiungiDisponibilita(Long alloggioId, DisponibilitaRequest req, Utente host) {
        Alloggio a = caricaProprio(alloggioId, host);
        // VR-09: la data di inizio deve precedere la data di fine
        if (!req.dataInizio().isBefore(req.dataFine())) {
            throw new BusinessException("La data di inizio deve precedere la data di fine");
        }
        // VR-10: non si possono bloccare date gia' coperte da una prenotazione attiva
        long occupate = prenotazioneRepository.contaSovrapposizioni(alloggioId, req.dataInizio(), req.dataFine());
        if (occupate > 0) {
            throw new BusinessException("Non puoi bloccare queste date: ci sono prenotazioni attive che si sovrappongono");
        }
        Blocco b = Blocco.builder()
                .alloggio(a)
                .dataInizio(req.dataInizio())
                .dataFine(req.dataFine())
                .build();
        b = bloccoRepository.save(b);
        return new DisponibilitaView(b.getId(), b.getDataInizio(), b.getDataFine());
    }

    @Transactional
    public void rimuoviDisponibilita(Long alloggioId, Long disponibilitaId, Utente host) {
        caricaProprio(alloggioId, host);
        Blocco b = bloccoRepository.findById(disponibilitaId)
                .orElseThrow(() -> new ResourceNotFoundException("Periodo non trovato: " + disponibilitaId));
        assicuraAppartieneAdAlloggio(b.getAlloggio().getId(), alloggioId, "Periodo non trovato: " + disponibilitaId);
        bloccoRepository.delete(b);
    }

    // ---- helper ----
    private Set<Servizio> risolviServizi(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(servizioRepository.findAllById(ids));
    }

    private Alloggio caricaProprio(Long id, Utente host) {
        Alloggio a = alloggioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato: " + id));
        if (!a.getHost().getId().equals(host.getId())) {
            throw new BusinessException("Non sei il proprietario di questo alloggio");
        }
        return a;
    }

    // Usato da rimuoviFoto/rimuoviDisponibilita: senza questo controllo un host potrebbe
    // cancellare una foto/un periodo altrui passando l'id di un proprio alloggio e l'id
    // dell'entita' figlia di un alloggio non suo.
    private void assicuraAppartieneAdAlloggio(Long alloggioIdEntita, Long alloggioAtteso, String messaggioSeAssente) {
        if (!alloggioIdEntita.equals(alloggioAtteso)) {
            throw new ResourceNotFoundException(messaggioSeAssente);
        }
    }
}
