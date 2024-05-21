package eu.europa.ec.sante.openncp.core.common.fhir.transformation.exception;

public abstract class BaseException extends RuntimeException {

    public BaseException(final String technicalMessage) {
        super(technicalMessage);
    }

    public BaseException(final String technicalMessage, final Throwable cause) {
        super(technicalMessage, cause);
    }

    public BaseException(final String technicalMessage, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(technicalMessage, cause, enableSuppression, writableStackTrace);
    }
}
