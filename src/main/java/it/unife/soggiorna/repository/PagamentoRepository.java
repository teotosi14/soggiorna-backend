package it.unife.soggiorna.repository;

import it.unife.soggiorna.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Accesso dati per {@link Pagamento}; usata da PrenotazioneService per evitare di duplicare il pagamento alla conferma. */
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByPrenotazioneId(Long prenotazioneId);
}
