package eu.europa.ec.sante.openncp.core.common.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

public class NoConsentException extends NIException {

    private static final long serialVersionUID = 2194752799478399763L;

    public NoConsentException(String message) {
        super(OpenNCPErrorCode.ERROR_NO_CONSENT, message);
    }
}
