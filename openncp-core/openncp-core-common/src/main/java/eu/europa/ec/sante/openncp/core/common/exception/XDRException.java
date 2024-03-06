package eu.europa.ec.sante.openncp.core.common.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

/**
 * Holds exceptions originated in the XDR Submit process.
 *
 * @author Marcelo Fonseca<code> - marcelo.fonseca@iuz.pt</code>
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public class XDRException extends ExceptionWithContext {

    public XDRException(OpenNCPErrorCode openncpErrorCode, Throwable e) {
        super(openncpErrorCode, e);
    }

    public XDRException(OpenNCPErrorCode openncpErrorCode, String message, String codeContext) {
        super(openncpErrorCode, message, codeContext);
    }

}
