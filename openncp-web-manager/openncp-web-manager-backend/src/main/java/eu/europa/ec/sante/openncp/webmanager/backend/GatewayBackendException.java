package eu.europa.ec.sante.openncp.webmanager.backend;

import eu.europa.ec.sante.openncp.webmanager.backend.service.ExceptionType;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

public class GatewayBackendException extends RuntimeException {

    private final ExceptionType exceptionType;

    public GatewayBackendException(ExceptionType exceptionType, Object... messageArguments) {
        super(MessageFormat.format(exceptionType.getMessage(), messageArguments));
        this.exceptionType = exceptionType;
    }

    public GatewayBackendException(ExceptionType exceptionType, final Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(exceptionType.getMessage(), messageArguments), cause);
        this.exceptionType = exceptionType;
    }

    public HttpStatus getHttpStatus() {
        return exceptionType.getStatus();
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
}
