package epsos.ccd.posam.tm.exception;

import eu.europa.ec.sante.ehdsi.constant.error.ITMTSAMError;
import eu.europa.ec.sante.ehdsi.constant.error.TMError;

public class TmErrorCtx implements ITMTSAMError {

	/**
	 * Exception code
	 */
	private final String code;
	/**
	 * Exception description (issue - is English description/constant enough ?)
	 */
	private final String description;
	
	private String context;

	/**
	 * Default enum constructor
	 * 
	 * @param code
	 * @param descripton
	 */
	public TmErrorCtx(TMError error, String ctx) {
		this.code = error.getCode();
		this.description = error.getDescription();
		this.context=ctx;
	}

	/**
	 * 
	 * @return String - code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 
	 * @return String - Description
	 */
	public String getDescription() {
		return description +" [" + context + "]";
	}

	@Override
	/**
	 * @return String in format code:description
	 */
	public String toString() {
		return code + ": " + getDescription();
	}

}
