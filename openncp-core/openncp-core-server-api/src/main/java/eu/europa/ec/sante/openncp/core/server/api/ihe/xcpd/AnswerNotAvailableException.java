package eu.europa.ec.sante.openncp.core.server.api.ihe.xcpd;


import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.XCPDErrorCode;

public class AnswerNotAvailableException extends XCPDNIException {
	private static final long serialVersionUID = 7640387067196506306L;

	public AnswerNotAvailableException(String message) {
		super(XCPDErrorCode.AnswerNotAvailable, OpenNCPErrorCode.ERROR_PI_NO_MATCH, message);
	}

	public AnswerNotAvailableException(OpenNCPErrorCode code) {
		super(XCPDErrorCode.AnswerNotAvailable, code, code.getDescription());
	}

}
