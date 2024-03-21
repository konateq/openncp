package eu.europa.ec.sante.openncp.core.common.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

public class DocumentProcessingException extends NIException {

    private static final long serialVersionUID = 2212600691470466466L;

    public DocumentProcessingException() {
        this( "Invalid Dispensation");
    }

    public DocumentProcessingException(String message) {
        super(OpenNCPErrorCode.ERROR_EP_ALREADY_DISPENSED, message);
    }
}
