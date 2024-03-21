package eu.europa.ec.sante.openncp.application.client.connector.assertions;

public class STSClientException extends Exception {

    public STSClientException(String message) {
        super(message);
    }

    public STSClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
