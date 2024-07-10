package eu.europa.ec.sante.openncp.webmanager.backend.error;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

public class ApiException extends ResponseStatusException {

    public ApiException(final HttpStatus status) {
        super(status);
    }

    public ApiException(final HttpStatus status, @Nullable final String message) {
        super(status, message);
    }

    public ApiException(final HttpStatus status, @Nullable final String reason, @Nullable final Throwable cause) {
        super(status, reason, cause);
    }

    @Override
    public String getMessage() {
        return this.getReason();
    }
}
