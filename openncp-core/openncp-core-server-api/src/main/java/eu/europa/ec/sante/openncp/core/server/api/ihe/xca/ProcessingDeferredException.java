package eu.europa.ec.sante.openncp.core.server.api.ihe.xca;


import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.exception.NIException;

public class ProcessingDeferredException extends NIException {

    private static final long serialVersionUID = 4872216168488255110L;

    public ProcessingDeferredException(String message) {
        super(OpenNCPErrorCode.ERROR_DOCUMENT_NOT_PROCESSED, message);
    }
}
