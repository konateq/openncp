package eu.epsos.protocolterminators.ws.server.xdr.exception;

import eu.epsos.protocolterminators.ws.server.xdr.DocumentProcessingException;
import eu.epsos.util.ErrorCode;

public class WeakAuthenticationException extends DocumentProcessingException {
	private static final long serialVersionUID = -47386094031497956L;

	public WeakAuthenticationException() {
		super("Country A requests a higher authentication trust level than assigned to the HP (e.g. password-based login is not accepted for the requested operation).");
		super.setCode(ErrorCode.ERROR_CODE_4702.getCodeToString());
	}
}
