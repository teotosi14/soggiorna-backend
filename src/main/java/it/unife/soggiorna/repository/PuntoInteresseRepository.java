package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.PuntoInteresse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Accesso dati per {@link PuntoInteresse}: consultazione pubblica per città, gestione admin CRUD. */
public interface PuntoInteresseRepository extends JpaRepository<PuntoInteresse, Long> {

    List<PuntoInteresse> findByCittaIgnoreCaseOrderByNome(String citta);

    List<PuntoInteresse> findAllByOrderByCittaAscIdAsc();
}
