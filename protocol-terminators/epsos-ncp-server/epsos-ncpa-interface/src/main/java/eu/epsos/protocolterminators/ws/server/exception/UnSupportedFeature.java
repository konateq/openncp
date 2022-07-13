package eu.epsos.protocolterminators.ws.server.exception;

import eu.europa.ec.sante.ehdsi.constant.error.OpenNCPErrorCode;

public class UnSupportedFeature extends NIException {

	private static final long serialVersionUID = -7044629789540910172L;

	public UnSupportedFeature(String message) {
		super(OpenNCPErrorCode.ERROR_UNSUPPORTED_FEATURE, message);
	}

}
