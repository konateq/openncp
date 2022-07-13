package eu.epsos.protocolterminators.ws.server.exception;

import eu.europa.ec.sante.ehdsi.constant.error.OpenNCPErrorCode;

public class RenderingIncompleteException extends NIException {

	private static final long serialVersionUID = -8555270493456361182L;

	public RenderingIncompleteException(String message) {
		super(OpenNCPErrorCode.ERROR_RENDERING_INCOMPLETE, message);
	}
}
