package eu.epsos.protocolterminators.ws.server.exception;

import eu.europa.ec.sante.ehdsi.constant.error.XcpdErrorCode;

public class PrivacyViolationException extends NIException {
	private static final long serialVersionUID = -2047613187403340704L;

	public PrivacyViolationException(String message) {
		super(null, message, XcpdErrorCode.PrivacyViolation.getCodeSystem());
	}
}
