package eu.europa.ec.sante.openncp.core.common.ihe.exception;


import eu.europa.ec.sante.openncp.common.error.ErrorCode;

public abstract class ExceptionWithContext extends Exception {

    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;

    //Use to pass information from the National Country A
    private String context;

    public ExceptionWithContext(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ExceptionWithContext(ErrorCode errorCode, String message, String context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
