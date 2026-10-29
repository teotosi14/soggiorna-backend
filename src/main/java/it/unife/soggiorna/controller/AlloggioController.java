package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.AlloggioDtos.*;
import it.unife.soggiorna.dto.DisponibilitaDtos.*;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.AlloggioService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Endpoint REST per gli annunci: ricerca e dettaglio sono pubblici (vedi SecurityConfig),
 * pubblicazione/modifica/disattivazione/foto richiedono un host autenticato ({@link CurrentUser}).
 */
@RestController
@RequestMapping("/api/alloggi")
public class AlloggioController {

    private final AlloggioService alloggioService;
    private final CurrentUser currentUser;

    public AlloggioController(AlloggioService alloggioService, CurrentUser currentUser) {
        this.alloggioService = alloggioService;
        this.currentUser = currentUser;
    }

    // RF-09: ricerca pubblica con filtri opzionali (citta'/ospiti/prezzo + intervallo di date)
    @GetMapping
    public List<AlloggioView> cerca(@RequestParam(required = false) String citta,
                                    @RequestParam(required = false) Integer ospiti,
                                    @RequestParam(required = false) BigDecimal prezzoMax,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout) {
        return alloggioService.cerca(citta, ospiti, prezzoMax, checkin, checkout);
    }

    // RF-10: dettaglio pubblico
    @GetMapping("/{id}")
    public AlloggioView dettaglio(@PathVariable Long id) {
        return alloggioService.get(id);
    }

    // I miei alloggi (host autenticato)
    @GetMapping("/miei")
    public List<AlloggioView> miei() {
        return alloggioService.deiMieiAlloggi(currentUser.get().getId());
    }

    // RF-04
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlloggioView crea(@Valid @RequestBody AlloggioRequest req) {
        return alloggioService.crea(req, currentUser.get());
    }

    // RF-07
    @PutMapping("/{id}")
    public AlloggioView aggiorna(@PathVariable Long id, @Valid @RequestBody AlloggioRequest req) {
        return alloggioService.aggiorna(id, req, currentUser.get());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disattiva(@PathVariable Long id) {
        alloggioService.disattiva(id, currentUser.get());
    }

    // Riattivazione da parte dell'host proprietario (in precedenza possibile solo da admin).
    @PutMapping("/{id}/riattiva")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void riattiva(@PathVariable Long id) {
        alloggioService.riattiva(id, currentUser.get());
    }

    // RF-05: aggiunge una foto tramite URL esterno
    @PostMapping("/{id}/foto")
    @ResponseStatus(HttpStatus.CREATED)
    public FotoView aggiungiFoto(@PathVariable Long id, @Valid @RequestBody FotoRequest req) {
        return alloggioService.aggiungiFoto(id, req, currentUser.get());
    }

    // RF-05: aggiunge una foto tramite upload reale del file (in alternativa all'URL)
    @PostMapping(value = "/{id}/foto/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FotoView caricaFoto(@PathVariable Long id,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam(required = false) String didascalia,
                               @RequestParam(defaultValue = "false") boolean copertina) {
        return alloggioService.caricaFoto(id, file, didascalia, copertina, currentUser.get());
    }

    // RF-05: rimuove una singola foto
    @DeleteMapping("/{id}/foto/{fotoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuoviFoto(@PathVariable Long id, @PathVariable Long fotoId) {
        alloggioService.rimuoviFoto(id, fotoId, currentUser.get());
    }

    // RF-06: periodi di indisponibilita' definiti dall'host. Lettura pubblica (come foto/servizi),
    // scrittura riservata al proprietario dell'alloggio.
    @GetMapping("/{id}/disponibilita")
    public List<DisponibilitaView> disponibilita(@PathVariable Long id) {
        return alloggioService.disponibilita(id);
    }

    @PostMapping("/{id}/disponibilita")
    @ResponseStatus(HttpStatus.CREATED)
    public DisponibilitaView aggiungiDisponibilita(@PathVariable Long id, @Valid @RequestBody DisponibilitaRequest req) {
        return alloggioService.aggiungiDisponibilita(id, req, currentUser.get());
    }

    @DeleteMapping("/{id}/disponibilita/{dispId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuoviDisponibilita(@PathVariable Long id, @PathVariable Long dispId) {
        alloggioService.rimuoviDisponibilita(id, dispId, currentUser.get());
    }

    // RF-09/RF-10: calendario pubblico delle date non disponibili, usato dal selettore date
    // nella scheda dell'alloggio prima ancora di tentare una prenotazione.
    @GetMapping("/{id}/date-occupate")
    public List<PeriodoNonDisponibileView> dateOccupate(@PathVariable Long id) {
        return alloggioService.dateOccupate(id);
    }
}
