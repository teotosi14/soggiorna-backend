package it.unife.soggiorna.model;

/** Stato di un {@link Pagamento}: nasce IN_ATTESA, in questa demo passa subito a COMPLETATO alla conferma host. */
public enum StatoPagamento {
    IN_ATTESA, COMPLETATO, RIMBORSATO
}
