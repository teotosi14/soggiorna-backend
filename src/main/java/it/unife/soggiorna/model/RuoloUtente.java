package it.unife.soggiorna.model;

/**
 * Ruolo applicativo di un {@link Utente}:
 * <ul>
 *   <li>{@code UTENTE}: utente normale (può pubblicare/prenotare alloggi, ecc.);</li>
 *   <li>{@code ADMIN}: amministratore della piattaforma;</li>
 *   <li>{@code PARTNER}: agenzia partner (noleggio auto / esperienze). Non prenota né pubblica
 *       alloggi: gestisce soltanto le prenotazioni ricevute sui propri servizi.</li>
 * </ul>
 */
public enum RuoloUtente {
    UTENTE, ADMIN, PARTNER
}
