package eu.europa.ec.sante.openncp.webmanager.backend.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@ControllerAdvice
public class DefaultExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public void handleApiException(final HttpServletResponse response, final ApiException ex) throws IOException {
        logger.error(String.format("[%s] with message [%s]", ex.getClass().getSimpleName(), ex.getMessage()), ex.getCause());
        response.sendError(ex.getRawStatusCode());
    }
}

