package eu.europa.ec.sante.openncp.sts;

import javax.xml.crypto.dsig.XMLSignatureException;

import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;

/**
 * FIXME
 * Temporary class to be able to build the project.
 * Once the security is lift and shifted we can delete this class and use the actual implementation
 */
public class SMgrException extends XMLSignatureException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final OpenNCPErrorCode openncpErrorCode = OpenNCPErrorCode.ERROR_SEC_DATA_INTEGRITY_NOT_ENSURED;

    /**
     * Constructor of the exception
     *
     * @param exceptionMessage - the error message
     */
    public SMgrException(String exceptionMessage) {
        super(exceptionMessage);
    }


    public SMgrException(String exceptionMessage, Exception e) {
        super(exceptionMessage, e);
    }

    public OpenNCPErrorCode getErrorCode(){
        return openncpErrorCode;
    }

}
