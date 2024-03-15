package eu.europa.ec.sante.openncp.application.client.connector;


import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

public class ClientConnectorException extends Exception {


    private OpenNCPErrorCode openncpErrorCode;
    String context;

    public ClientConnectorException(OpenNCPErrorCode openncpErrorCode, String message, String context) {
        super(message);
        this.openncpErrorCode = openncpErrorCode;
        this.context = context;
    }

    public ClientConnectorException(OpenNCPErrorCode openncpErrorCode, String message, String context, Throwable cause) {
        super(message, cause);
        this.openncpErrorCode = openncpErrorCode;
        this.context = context;
    }

    public OpenNCPErrorCode getOpenncpErrorCode() {
        return openncpErrorCode;
    }

    public String getContext() {
        return context;
    }


}
