package eu.epsos.protocolterminators.ws.server.exception;

import eu.europa.ec.sante.ehdsi.constant.error.OpenNCPErrorCode;

@Deprecated(forRemoval = true, since = "6.0.0")
public class CollectionIncompleteException extends NIException {

	private static final long serialVersionUID = -8728511084030064015L;

	public CollectionIncompleteException(String message) {
		super(OpenNCPErrorCode.ERROR_COLLECTION_INCOMPLETE, message);
	}
}
