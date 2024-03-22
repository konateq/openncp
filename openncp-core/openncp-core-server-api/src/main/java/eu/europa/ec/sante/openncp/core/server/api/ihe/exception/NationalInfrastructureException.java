package eu.europa.ec.sante.openncp.core.server.api.ihe.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.exception.NIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NationalInfrastructureException extends NIException {

    public NationalInfrastructureException(OpenNCPErrorCode openncpErrorCode, String message) {

        super(openncpErrorCode, message);
        Logger logger = LoggerFactory.getLogger(NationalInfrastructureException.class);
        logger.error("NationalInfrastructureException: '{}': '{}'", openncpErrorCode.getCode(), message);
    }
}
