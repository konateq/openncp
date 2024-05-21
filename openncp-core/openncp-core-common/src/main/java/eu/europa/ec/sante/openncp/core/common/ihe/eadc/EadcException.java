package eu.europa.ec.sante.openncp.core.common.ihe.eadc;

public class EadcException extends Exception {
    
    public EadcException(String message) {
        super(message);
    }

    public EadcException(String message, Throwable cause) {
        super(message, cause);
    }
}
