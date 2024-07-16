package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

public class TOElementException extends Exception {

    private static final long serialVersionUID = 5497948833180029330L;

    public TOElementException() {
    }

    public TOElementException(final String message) {
        super(message);
    }

    public TOElementException(final Throwable cause) {
        super(cause);
    }

    public TOElementException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
