package eu.europa.ec.sante.openncp.webmanager.backend.service;

import eu.europa.ec.sante.openncp.webmanager.backend.WebManagerBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class ExceptionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionFactory.class);

    private ExceptionFactory() {
    }

    public static WebManagerBackendException create(final Throwable cause, final ExceptionType exceptionType,
                                                    final Object... messageArguments) {
        LOGGER.error(MessageFormat.format(exceptionType.getMessage(), messageArguments), cause);
        return new WebManagerBackendException(exceptionType, cause, messageArguments);
    }

    public static WebManagerBackendException create(final ExceptionType exceptionType, final Object... messageArguments) {
        LOGGER.error(MessageFormat.format(exceptionType.getMessage(), messageArguments));
        return new WebManagerBackendException(exceptionType, messageArguments);
    }
}
