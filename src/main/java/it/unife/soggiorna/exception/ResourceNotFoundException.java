package it.unife.soggiorna.exception;

/** Lanciata quando un id richiesto (alloggio, prenotazione, ecc.) non esiste; mappata a HTTP 404 da GlobalExceptionHandler. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
