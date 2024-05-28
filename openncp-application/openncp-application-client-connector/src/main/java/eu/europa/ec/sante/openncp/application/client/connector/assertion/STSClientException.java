package eu.europa.ec.sante.openncp.application.client.connector.assertion;

public class STSClientException extends RuntimeException {

    public STSClientException(String message) {
        super(message);
    }

    public STSClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
