package it.unife.soggiorna.service;

import it.unife.soggiorna.dto.PuntoInteresseDtos.CittaSommario;
import it.unife.soggiorna.dto.PuntoInteresseDtos.PuntoInteresseView;
import it.unife.soggiorna.model.PuntoInteresse;
import it.unife.soggiorna.repository.PuntoInteresseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Lettura pubblica della guida "Scopri la città". La scrittura (CRUD) è riservata all'admin, vedi {@link AdminService}. */
@Service
public class PuntoInteresseService {

    private final PuntoInteresseRepository puntoInteresseRepository;

    public PuntoInteresseService(PuntoInteresseRepository puntoInteresseRepository) {
        this.puntoInteresseRepository = puntoInteresseRepository;
    }

    @Transactional(readOnly = true)
    public List<PuntoInteresseView> perCitta(String citta) {
        return puntoInteresseRepository.findByCittaIgnoreCaseOrderByNome(citta).stream()
                .map(Mappers::toView).toList();
    }

    /** Una riga per città (ordine alfabetico), con foto di copertina (la prima disponibile per quella
     *  città) e conteggio: alimenta il selettore fotografico di "Scopri la città" senza dover caricare
     *  l'elenco completo dei punti per ogni città. */
    @Transactional(readOnly = true)
    public List<CittaSommario> tutteLeCitta() {
        Map<String, List<PuntoInteresse>> perCitta = puntoInteresseRepository.findAllByOrderByCittaAscIdAsc().stream()
                .collect(Collectors.groupingBy(PuntoInteresse::getCitta, LinkedHashMap::new, Collectors.toList()));
        return perCitta.entrySet().stream()
                .map(e -> new CittaSommario(e.getKey(), e.getValue().size(), e.getValue().get(0).getImmagineUrl()))
                .toList();
    }
}
