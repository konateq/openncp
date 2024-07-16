package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

public class EnforcePolicyException extends Exception {
    
    private static final long serialVersionUID = -6196729998607048379L;

    public EnforcePolicyException() {
    }

    public EnforcePolicyException(final String message) {
        super(message);
    }

    public EnforcePolicyException(final Throwable cause) {
        super(cause);
    }

    public EnforcePolicyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
