package it.unife.soggiorna.controller;

import it.unife.soggiorna.dto.AdminDtos.*;
import it.unife.soggiorna.dto.AlloggioDtos.AlloggioView;
import it.unife.soggiorna.dto.AlloggioDtos.ServizioView;
import it.unife.soggiorna.dto.EsperienzaDtos.EsperienzaView;
import it.unife.soggiorna.dto.PuntoInteresseDtos.PuntoInteresseRequest;
import it.unife.soggiorna.dto.PuntoInteresseDtos.PuntoInteresseView;
import it.unife.soggiorna.dto.RecensioneDtos.RecensioneView;
import it.unife.soggiorna.dto.VeicoloDtos.VeicoloView;
import it.unife.soggiorna.security.CurrentUser;
import it.unife.soggiorna.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint riservati all'Amministratore (autorizzazione a livello di rotta in SecurityConfig,
 * {@code hasRole("ADMIN")}): gestione del catalogo servizi e moderazione di alloggi, recensioni
 * e utenti. Nessuno di questi endpoint e' raggiungibile da un utente con ruolo UTENTE.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final CurrentUser currentUser;

    public AdminController(AdminService adminService, CurrentUser currentUser) {
        this.adminService = adminService;
        this.currentUser = currentUser;
    }

    // ---- Catalogo servizi ----
    @PostMapping("/servizi")
    @ResponseStatus(HttpStatus.CREATED)
    public ServizioView creaServizio(@Valid @RequestBody ServizioRequest req) {
        return adminService.creaServizio(req);
    }

    @PutMapping("/servizi/{id}")
    public ServizioView rinominaServizio(@PathVariable Long id, @Valid @RequestBody ServizioRequest req) {
        return adminService.rinominaServizio(id, req);
    }

    @DeleteMapping("/servizi/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaServizio(@PathVariable Long id) {
        adminService.eliminaServizio(id);
    }

    // ---- Moderazione alloggi ----
    @GetMapping("/alloggi")
    public List<AlloggioView> elencoAlloggi() {
        return adminService.elencoAlloggi();
    }

    @PutMapping("/alloggi/{id}/disattiva")
    public AlloggioView disattivaAlloggio(@PathVariable Long id) {
        return adminService.impostaAttivoAlloggio(id, false);
    }

    @PutMapping("/alloggi/{id}/riattiva")
    public AlloggioView riattivaAlloggio(@PathVariable Long id) {
        return adminService.impostaAttivoAlloggio(id, true);
    }

    // ---- Moderazione recensioni ----
    @GetMapping("/recensioni")
    public List<RecensioneView> elencoRecensioni() {
        return adminService.elencoRecensioni();
    }

    @DeleteMapping("/recensioni/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaRecensione(@PathVariable Long id) {
        adminService.eliminaRecensione(id);
    }

    // ---- Gestione utenti ----
    @GetMapping("/utenti")
    public List<UtenteAdminView> elencoUtenti() {
        return adminService.elencoUtenti();
    }

    @PutMapping("/utenti/{id}/sospendi")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sospendiUtente(@PathVariable Long id) {
        adminService.sospendiUtente(id, currentUser.get());
    }

    @PutMapping("/utenti/{id}/riattiva")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void riattivaUtente(@PathVariable Long id) {
        adminService.riattivaUtente(id);
    }

    // ---- Moderazione veicoli a noleggio ----
    @GetMapping("/veicoli")
    public List<VeicoloView> elencoVeicoli() {
        return adminService.elencoVeicoli();
    }

    @PutMapping("/veicoli/{id}/disattiva")
    public VeicoloView disattivaVeicolo(@PathVariable Long id) {
        return adminService.impostaAttivoVeicolo(id, false);
    }

    @PutMapping("/veicoli/{id}/riattiva")
    public VeicoloView riattivaVeicolo(@PathVariable Long id) {
        return adminService.impostaAttivoVeicolo(id, true);
    }

    // ---- Moderazione esperienze ----
    @GetMapping("/esperienze")
    public List<EsperienzaView> elencoEsperienze() {
        return adminService.elencoEsperienze();
    }

    @PutMapping("/esperienze/{id}/disattiva")
    public EsperienzaView disattivaEsperienza(@PathVariable Long id) {
        return adminService.impostaAttivoEsperienza(id, false);
    }

    @PutMapping("/esperienze/{id}/riattiva")
    public EsperienzaView riattivaEsperienza(@PathVariable Long id) {
        return adminService.impostaAttivoEsperienza(id, true);
    }

    // ---- CRUD guida "Scopri la citta'" ----
    @PostMapping("/punti-interesse")
    @ResponseStatus(HttpStatus.CREATED)
    public PuntoInteresseView creaPuntoInteresse(@Valid @RequestBody PuntoInteresseRequest req) {
        return adminService.creaPuntoInteresse(req);
    }

    @PutMapping("/punti-interesse/{id}")
    public PuntoInteresseView aggiornaPuntoInteresse(@PathVariable Long id, @Valid @RequestBody PuntoInteresseRequest req) {
        return adminService.aggiornaPuntoInteresse(id, req);
    }

    @DeleteMapping("/punti-interesse/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaPuntoInteresse(@PathVariable Long id) {
        adminService.eliminaPuntoInteresse(id);
    }
}
