package eu.europa.ec.sante.openncp.core.common.fhir.transformation.exception;

public class TranscodingException extends TransformationException {

    public TranscodingException(final String technicalMessage) {
        super(technicalMessage);
    }

    public TranscodingException(final String technicalMessage, final Throwable cause) {
        super(technicalMessage, cause);
    }

    public TranscodingException(final String technicalMessage, final Throwable cause, final boolean enableSuppression,
                                final boolean writableStackTrace) {
        super(technicalMessage, cause, enableSuppression, writableStackTrace);
    }
}
