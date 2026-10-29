package it.unife.soggiorna.model;

/** Ciclo di vita di una {@link Prenotazione}: IN_ATTESA -> CONFERMATA -> COMPLETATA, oppure ANNULLATA in qualsiasi momento dal guest. */
public enum StatoPrenotazione {
    IN_ATTESA, CONFERMATA, ANNULLATA, COMPLETATA
}
