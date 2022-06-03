package eu.epsos.protocolterminators.ws.server.xdr.exception;

import eu.epsos.protocolterminators.ws.server.xdr.DocumentProcessingException;
import eu.europa.ec.sante.ehdsi.openncp.util.security.EhdsiCode;

public class WeakAuthenticationException extends DocumentProcessingException {
	private static final long serialVersionUID = -47386094031497956L;

	public WeakAuthenticationException() {
		super("Country A requests a higher authentication trust level than assigned to the HP (e.g. password-based login is not accepted for the requested operation).");
		super.setEhdsiCode(EhdsiCode.EHDSI_ERROR_4702);
	}
}
