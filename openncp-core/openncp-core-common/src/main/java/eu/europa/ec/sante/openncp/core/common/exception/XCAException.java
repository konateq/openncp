package eu.europa.ec.sante.openncp.core.common.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

/**
 * Holds exceptions originated in the XCA Query / Retrieve process.
 *
 * @author Marcelo Fonseca<code> - marcelo.fonseca@iuz.pt</code>
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public class XCAException extends ExceptionWithContext {

    public XCAException(OpenNCPErrorCode openncpErrorCode, String message, String context) {
        super(openncpErrorCode, message, context);
    }

}
