package it.unife.soggiorna.exception;

/** Violazione di una regola di business (es. date non valide, sovrapposizioni). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
