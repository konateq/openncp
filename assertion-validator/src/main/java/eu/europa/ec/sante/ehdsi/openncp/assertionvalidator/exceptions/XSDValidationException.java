package eu.europa.ec.sante.ehdsi.openncp.assertionvalidator.exceptions;

import eu.europa.ec.sante.ehdsi.constant.error.EhdsiErrorCode;

public class XSDValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6854562291880477762L;

	private String message;

	private EhdsiErrorCode ehdsiErrorCode = EhdsiErrorCode.EHDSI_ERROR_SEC_DATA_INTEGRITY_NOT_ENSURED;

	public XSDValidationException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return ehdsiErrorCode.getCode();
	}
}
