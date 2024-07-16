package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

public class ObligationDischargeException extends Exception {

    private static final long serialVersionUID = 8580686800820475109L;

    public ObligationDischargeException() {
    }

    public ObligationDischargeException(final String message) {
        super(message);
    }

    public ObligationDischargeException(final Throwable cause) {
        super(cause);
    }

    public ObligationDischargeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
