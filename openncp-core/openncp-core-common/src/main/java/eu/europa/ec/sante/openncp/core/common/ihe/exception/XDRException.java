package eu.europa.ec.sante.openncp.core.common.ihe.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.ExceptionWithContext;

/**
 * Holds exceptions originated in the XDR Submit process.
 */
public class XDRException extends ExceptionWithContext {

    public XDRException(OpenNCPErrorCode openncpErrorCode, Throwable e) {
        super(openncpErrorCode, e);
    }

    public XDRException(OpenNCPErrorCode openncpErrorCode, String message, String codeContext) {
        super(openncpErrorCode, message, codeContext);
    }

}
