package eu.europa.ec.sante.openncp.core.common.fhir.audit;

public class AuditException extends RuntimeException {
    public AuditException() {
    }

    public AuditException(String message) {
        super(message);
    }

    public AuditException(String message, Throwable cause) {
        super(message, cause);
    }
}
