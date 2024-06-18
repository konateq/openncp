package eu.europa.ec.sante.openncp.core.common.fhir.transformation.exception;

public class TranslationException extends TransformationException {

    public TranslationException(final String technicalMessage) {
        super(technicalMessage);
    }

    public TranslationException(final String technicalMessage, final Throwable cause) {
        super(technicalMessage, cause);
    }

    public TranslationException(final String technicalMessage, final Throwable cause, final boolean enableSuppression,
                                final boolean writableStackTrace) {
        super(technicalMessage, cause, enableSuppression, writableStackTrace);
    }
}
