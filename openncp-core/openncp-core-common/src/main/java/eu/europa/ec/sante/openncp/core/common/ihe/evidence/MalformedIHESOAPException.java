package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

public class MalformedIHESOAPException extends Exception {

    private static final long serialVersionUID = 1273575902438414667L;

    public MalformedIHESOAPException() {
    }

    public MalformedIHESOAPException(final String message) {
        super(message);
    }

    public MalformedIHESOAPException(final Throwable cause) {
        super(cause);
    }

    public MalformedIHESOAPException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
