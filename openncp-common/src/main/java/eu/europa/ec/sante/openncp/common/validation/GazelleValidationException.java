package eu.europa.ec.sante.openncp.common.validation;

public class GazelleValidationException extends Exception {

    public GazelleValidationException(String message) {
        super(message);
    }

    public GazelleValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
