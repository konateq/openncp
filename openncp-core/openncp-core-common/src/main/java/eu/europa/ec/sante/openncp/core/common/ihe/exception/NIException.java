package eu.europa.ec.sante.openncp.core.common.ihe.exception;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

public class NIException extends Exception {

    private static final long serialVersionUID = 2148051521948531853L;
    private OpenNCPErrorCode openncpErrorCode;
    private String message;

    public NIException(OpenNCPErrorCode openncpErrorCode, String message) {
        this.openncpErrorCode = openncpErrorCode;
        this.message = message;
    }

    public NIException(OpenNCPErrorCode openncpErrorCode) {
        this.openncpErrorCode = openncpErrorCode;
        this.message = openncpErrorCode.getDescription();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public OpenNCPErrorCode getOpenncpErrorCode() {
        return openncpErrorCode;
    }

    public void setOpenncpErrorCode(OpenNCPErrorCode openncpErrorCode) {
        this.openncpErrorCode = openncpErrorCode;
    }
}
