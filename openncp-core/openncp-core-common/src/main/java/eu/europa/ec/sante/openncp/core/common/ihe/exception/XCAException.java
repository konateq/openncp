package eu.europa.ec.sante.openncp.core.common.ihe.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.ExceptionWithContext;

/**
 * Holds exceptions originated in the XCA Query / Retrieve process.
 */
public class XCAException extends ExceptionWithContext {

    public XCAException(OpenNCPErrorCode openncpErrorCode, String message, String context) {
        super(openncpErrorCode, message, context);
    }

}
