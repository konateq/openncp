package eu.europa.ec.sante.openncp.core.server.api.ihe.xca;


import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.NIException;

public class NoMatchException extends NIException {

    private static final long serialVersionUID = -1353577541109670053L;

    public NoMatchException(String message) {
        super(OpenNCPErrorCode.ERROR_EP_NOT_MATCHING, message);
    }
}
