package eu.europa.ec.sante.openncp.core.server.api.ihe.xca;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.exception.NIException;

public class OriginalDataMissingException extends NIException {
    private static final long serialVersionUID = 4254468101664118588L;

    public OriginalDataMissingException(String message) {
        super(OpenNCPErrorCode.ERROR_ORIGINAL_DATA_MISSING, message);
    }
}
