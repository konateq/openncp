package eu.europa.ec.sante.openncp.webmanager.backend.error;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

public class ApiException extends ResponseStatusException {

    public ApiException(HttpStatus status) {
        super(status);
    }

    public ApiException(HttpStatus status, @Nullable String message) {
        super(status, message);
    }

    @Override
    public String getMessage() {
        return this.getReason();
    }
}
