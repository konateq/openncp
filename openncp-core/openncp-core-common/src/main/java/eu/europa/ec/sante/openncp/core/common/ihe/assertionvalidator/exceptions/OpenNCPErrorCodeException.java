package eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.exceptions;


import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

public abstract class OpenNCPErrorCodeException extends Exception {

    public abstract OpenNCPErrorCode getErrorCode();
}
