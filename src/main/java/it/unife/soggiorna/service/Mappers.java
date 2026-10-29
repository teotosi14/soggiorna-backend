package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.AlloggioDtos.*;
import it.unife.soggiorna.dto.EsperienzaDtos.EsperienzaView;
import it.unife.soggiorna.dto.PacchettoDtos.PacchettoView;
import it.unife.soggiorna.dto.PrenotazioneDtos.PrenotazioneView;
import it.unife.soggiorna.dto.PrenotazioneEsperienzaDtos.PrenotazioneEsperienzaView;
import it.unife.soggiorna.dto.PrenotazioneVeicoloDtos.PrenotazioneVeicoloView;
import it.unife.soggiorna.dto.PuntoInteresseDtos.PuntoInteresseView;
import it.unife.soggiorna.dto.RecensioneDtos.RecensioneView;
import it.unife.soggiorna.dto.SpesaDtos.SpesaView;
import it.unife.soggiorna.dto.VeicoloDtos.VeicoloView;
import it.unife.soggiorna.model.*;
import it.unife.soggiorna.repository.FotoRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Conversioni entita' -> DTO di risposta (evita la serializzazione diretta delle entita'). */
public final class Mappers {

    private Mappers() {}

    public static AlloggioView toView(Alloggio a, List<Foto> foto) {
        List<ServizioView> servizi = a.getServizi().stream()
                .map(s -> new ServizioView(s.getId(), s.getNome()))
                .toList();
        List<FotoView> fotoView = foto.stream()
                .map(f -> new FotoView(f.getId(), f.getUrl(), f.getDidascalia(), f.getCopertina()))
                .toList();
        return new AlloggioView(
                a.getId(), a.getHost().getId(), a.getHost().getNome(),
                a.getTitolo(), a.getDescrizione(), a.getTipologia(),
                a.getIndirizzo(), a.getCitta(), a.getPaese(), a.getCap(),
                a.getPrezzoNotte(), a.getNumOspitiMax(), a.getNumCamere(), a.getNumBagni(),
                a.getAttivo(), servizi, fotoView);
    }

    // Come toView, ma per liste di alloggi: carica le foto di tutti in un'unica query invece
    // di una per alloggio (usato da ricerca, "i miei alloggi" e dall'elenco amministratore).
    public static List<AlloggioView> toViews(List<Alloggio> alloggi, FotoRepository fotoRepository) {
        if (alloggi.isEmpty()) return List.of();
        List<Long> ids = alloggi.stream().map(Alloggio::getId).toList();
        Map<Long, List<Foto>> fotoPerAlloggio = fotoRepository.findByAlloggioIdIn(ids).stream()
                .collect(Collectors.groupingBy(f -> f.getAlloggio().getId()));
        return alloggi.stream()
                .map(a -> toView(a, fotoPerAlloggio.getOrDefault(a.getId(), List.of())))
                .toList();
    }

    public static PrenotazioneView toView(Prenotazione p, boolean recensito) {
        return new PrenotazioneView(
                p.getId(), p.getAlloggio().getId(), p.getAlloggio().getTitolo(),
                p.getGuest().getId(), p.getDataCheckin(), p.getDataCheckout(),
                p.getNumOspiti(), p.getPrezzoTotale(), p.getStato(), recensito, p.getMotivoAnnullamento());
    }

    public static RecensioneView toView(Recensione r) {
        Prenotazione p = r.getPrenotazione();
        return new RecensioneView(
                r.getId(), p.getId(), p.getAlloggio().getId(),
                p.getGuest().getNome(), r.getRating(), r.getCommento(), r.getData());
    }

    public static VeicoloView toView(Veicolo v) {
        return new VeicoloView(
                v.getId(), v.getHost().getId(), v.getHost().getNome(),
                v.getMarca(), v.getModello(), v.getAnno(), v.getTipo(), v.getCambio(),
                v.getPosti(), v.getPrezzoGiorno(), v.getCitta(), v.getImmagineUrl(), v.getAttivo());
    }

    public static PrenotazioneVeicoloView toView(PrenotazioneVeicolo pv) {
        return new PrenotazioneVeicoloView(
                pv.getId(), pv.getVeicolo().getId(), pv.getVeicolo().getMarca(), pv.getVeicolo().getModello(),
                pv.getGuest().getId(), pv.getDataRitiro(), pv.getDataRiconsegna(), pv.getPrezzoTotale(), pv.getStato());
    }

    public static EsperienzaView toView(Esperienza e, int postiDisponibili) {
        return new EsperienzaView(
                e.getId(), e.getHost().getId(), e.getHost().getNome(),
                e.getTitolo(), e.getDescrizione(), e.getCitta(), e.getCategoria(),
                e.getPrezzoPersona(), e.getDataOra(), e.getDurataOre(), e.getPostiMax(),
                postiDisponibili, e.getImmagineUrl(), e.getAttivo());
    }

    public static PrenotazioneEsperienzaView toView(PrenotazioneEsperienza pe) {
        return new PrenotazioneEsperienzaView(
                pe.getId(), pe.getEsperienza().getId(), pe.getEsperienza().getTitolo(), pe.getEsperienza().getDataOra(),
                pe.getGuest().getId(), pe.getNumPartecipanti(), pe.getPrezzoTotale(), pe.getStato());
    }

    public static SpesaView toView(Spesa s) {
        return new SpesaView(s.getId(), s.getCategoria(), s.getImporto(), s.getDescrizione(), s.getData());
    }

    public static PuntoInteresseView toView(PuntoInteresse p) {
        return new PuntoInteresseView(
                p.getId(), p.getCitta(), p.getNome(), p.getCategoria(),
                p.getDescrizione(), p.getIndirizzo(), p.getImmagineUrl());
    }

    public static PacchettoView toView(PacchettoVacanza p) {
        return new PacchettoView(
                p.getId(), p.getTitolo(), p.getSottotitolo(), p.getDescrizione(), p.getCitta(),
                p.getCategoria(), p.getDurataNotti(), p.getPrezzoIndicativo(), p.getImmagineUrl());
    }
}
