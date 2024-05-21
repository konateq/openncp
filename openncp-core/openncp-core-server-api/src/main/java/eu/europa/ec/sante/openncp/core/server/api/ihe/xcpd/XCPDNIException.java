package eu.europa.ec.sante.openncp.core.server.api.ihe.xcpd;


import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.NIException;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.XCPDErrorCode;

public class XCPDNIException extends NIException {

    private final XCPDErrorCode xcpdErrorCode;

    public XCPDNIException(XCPDErrorCode xcpdErrorCode, OpenNCPErrorCode openncpErrorCode, String message) {
        super(openncpErrorCode, message);
        this.xcpdErrorCode = xcpdErrorCode;
    }

    public XCPDErrorCode getXcpdErrorCode() {
        return xcpdErrorCode;
    }

    public String getCodeSystem() {
        return xcpdErrorCode.getCodeSystem();
    }
}
