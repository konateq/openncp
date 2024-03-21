package eu.europa.ec.sante.openncp.application.client.connector;

import java.util.Optional;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

public class ClientConnectorException extends RuntimeException {

    private OpenNCPErrorCode openncpErrorCode;
    String context;

    public ClientConnectorException(final OpenNCPErrorCode openncpErrorCode, final String message, final String context) {
        super(message);
        this.openncpErrorCode = openncpErrorCode;
        this.context = context;
    }

    public ClientConnectorException(final OpenNCPErrorCode openncpErrorCode, final String message, final String context, final Throwable cause) {
        super(message, cause);
        this.openncpErrorCode = openncpErrorCode;
        this.context = context;
    }

    public ClientConnectorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ClientConnectorException(final String message) {
        super(message);
    }

    public Optional<OpenNCPErrorCode> getOpenncpErrorCode() {
        return Optional.ofNullable(openncpErrorCode);
    }

    public String getContext() {
        return context;
    }
}
