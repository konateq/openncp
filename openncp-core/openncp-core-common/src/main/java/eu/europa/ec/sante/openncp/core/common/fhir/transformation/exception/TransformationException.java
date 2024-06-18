package eu.europa.ec.sante.openncp.core.common.fhir.transformation.exception;

public abstract class TransformationException extends RuntimeException {

    public TransformationException(final String technicalMessage) {
        super(technicalMessage);
    }

    public TransformationException(final String technicalMessage, final Throwable cause) {
        super(technicalMessage, cause);
    }

    public TransformationException(final String technicalMessage, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(technicalMessage, cause, enableSuppression, writableStackTrace);
    }
}
