package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.PrenotazioneDtos.PrenotazioneRequest;
import it.unife.soggiorna.exception.BusinessException;
import it.unife.soggiorna.model.Alloggio;
import it.unife.soggiorna.model.Prenotazione;
import it.unife.soggiorna.model.StatoPrenotazione;
import it.unife.soggiorna.model.Utente;
import it.unife.soggiorna.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test delle regole di business di PrenotazioneService (creazione e annullamento).
 * Repository e entita' sono simulati con Mockito: non serve il database.
 * I nomi TC-xx corrispondono ai casi di test della relazione.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PrenotazioneServiceTest {

    @Mock PrenotazioneRepository prenotazioneRepository;
    @Mock AlloggioRepository alloggioRepository;
    @Mock PagamentoRepository pagamentoRepository;
    @Mock RecensioneRepository recensioneRepository;
    @Mock BloccoRepository bloccoRepository;

    @Mock Alloggio alloggio;
    @Mock Utente host;
    @Mock Utente guest;

    PrenotazioneService service;

    @BeforeEach
    void setUp() {
        service = new PrenotazioneService(prenotazioneRepository, alloggioRepository,
                pagamentoRepository, recensioneRepository, bloccoRepository);

        // alloggio attivo, 4 ospiti al massimo, 80 euro a notte, host diverso dal guest
        when(host.getId()).thenReturn(1L);
        when(guest.getId()).thenReturn(2L);
        when(alloggio.getId()).thenReturn(10L);
        when(alloggio.getAttivo()).thenReturn(true);
        when(alloggio.getHost()).thenReturn(host);
        when(alloggio.getNumOspitiMax()).thenReturn(4);
        when(alloggio.getPrezzoNotte()).thenReturn(new BigDecimal("80.00"));
        when(alloggioRepository.findById(10L)).thenReturn(Optional.of(alloggio));
    }

    private PrenotazioneRequest richiesta(LocalDate checkin, LocalDate checkout, int ospiti) {
        return new PrenotazioneRequest(10L, checkin, checkout, ospiti);
    }

    // TC-01: check-in dopo il check-out
    @Test
    void tc01_checkinDopoCheckout_lanciaErrore() {
        var req = richiesta(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 8), 2);
        assertThrows(BusinessException.class, () -> service.crea(req, guest));
        verify(prenotazioneRepository, never()).save(any());
    }

    // TC-02: date gia' occupate da una prenotazione attiva
    @Test
    void tc02_dateOccupate_lanciaErrore() {
        when(prenotazioneRepository.contaSovrapposizioni(eq(10L), any(), any())).thenReturn(1L);
        var req = richiesta(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14), 2);
        assertThrows(BusinessException.class, () -> service.crea(req, guest));
        verify(prenotazioneRepository, never()).save(any());
    }

    // TC-03: l'utente prenota un proprio alloggio
    @Test
    void tc03_alloggioProprio_lanciaErrore() {
        when(guest.getId()).thenReturn(1L); // stesso id dell'host
        var req = richiesta(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), 2);
        assertThrows(BusinessException.class, () -> service.crea(req, guest));
        verify(prenotazioneRepository, never()).save(any());
    }

    // TC-04: ospiti oltre la capienza
    @Test
    void tc04_ospitiOltreCapienza_lanciaErrore() {
        var req = richiesta(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), 5);
        assertThrows(BusinessException.class, () -> service.crea(req, guest));
        verify(prenotazioneRepository, never()).save(any());
    }

    // TC-05: prenotazione valida, 3 notti a 80 euro -> totale 240, stato IN_ATTESA
    @Test
    void tc05_prenotazioneValida_calcolaTotaleEStato() {
        when(prenotazioneRepository.contaSovrapposizioni(eq(10L), any(), any())).thenReturn(0L);
        when(bloccoRepository.contaSovrapposizioni(eq(10L), any(), any())).thenReturn(0L);
        when(prenotazioneRepository.save(any(Prenotazione.class))).thenAnswer(i -> i.getArgument(0));

        var req = richiesta(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), 2);
        service.crea(req, guest);

        ArgumentCaptor<Prenotazione> captor = ArgumentCaptor.forClass(Prenotazione.class);
        verify(prenotazioneRepository).save(captor.capture());
        assertEquals(0, new BigDecimal("240.00").compareTo(captor.getValue().getPrezzoTotale()));
        assertEquals(StatoPrenotazione.IN_ATTESA, captor.getValue().getStato());
    }

    // TC-06: non si puo' annullare una prenotazione gia' completata
    @Test
    void tc06_annullaPrenotazioneCompletata_lanciaErrore() {
        Prenotazione completata = Prenotazione.builder()
                .alloggio(alloggio)
                .guest(guest)
                .stato(StatoPrenotazione.COMPLETATA)
                .build();
        when(prenotazioneRepository.findById(99L)).thenReturn(Optional.of(completata));

        assertThrows(BusinessException.class, () -> service.annulla(99L, guest));
        assertEquals(StatoPrenotazione.COMPLETATA, completata.getStato());
    }
}
