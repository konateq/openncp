package eu.europa.ec.sante.ehdsi.constant.error;

/**
 * Enumeration for TSAM Errors & Warnings codes
 * <p>
 * Provides get/set methods for code and description
 */
public enum TSAMError implements ITMTSAMError, ErrorCode {
    // ERRORS
    /**
     * Error; Software processing error (e.g. "out of memory" )<br>
     * code, description:<br>
     * "4500","Processing error (sw failure)"
     */
    ERROR_PROCESSING_ERROR("4500", "Processing error (sw failure)"),
    ERROR_CODE_SYSTEM_NOTFOUND("4501", "Code System not found"),
    ERROR_CODE_SYSTEM_VERSION_NOTFOUND("4502", "Code System Version not found"),
    ERROR_CODE_SYSTEM_CONCEPT_NOTFOUND("4503", "Code System Concept not found"),
    ERROR_TARGET_CONCEPT_NOTFOUND("4504", "Target Code System Concept not found"),
    ERROR_DESIGNATION_NOTFOUND("4505", "Designation for Code System Concept not found"),
    ERROR_EPSOS_VERSION_NOTFOUND("4506", "Epsos Code System Version not found"),
    ERROR_EPSOS_CODE_SYSTEM_NOTFOUND("4507", "Epsos Code System  not found"),
    ERROR_EPSOS_CS_OID_NOTFOUND("4508", "Epsos Code System OID not found"),
    ERROR_TRANSCODING_INVALID("4509", "Transcoding Association is invalid"),
    ERROR_NO_CURRENT_DESIGNATIONS("4510", "No Designations with STATUS=\"Current\" found"),
    // WARNINGS
    WARNING_CODED_ELEMENT_TRANSCODING_FAILURE("2502", "Coded element transcoding failure"),
    /**
     * Warning; Processing error (sw failure); failing coded element + Software
     * processing error (e.g. "out of memory" ) <br>
     * code, description:<br>
     * "2503","Coded element processing failure"
     */
    WARNING_CODED_ELEMENT_PROCESSING_FAILURE("2503", "Coded element processing failure"),
    /**
     * Warning; Not defined target language designation, Input concept <br>
     * code, description:<br>
     * "2504","Coded element translation failure"
     */
    WARNING_CODED_ELEMENT_TRANSLATION_FAILURE("2504", "Coded element translation failure"),
    /**
     * Warning; Input Target Language <br>
     * code, description:<br>
     * "2505","Unknown Target Language"
     */
    WARNING_UNKNOWN_TARGET_LANGUAGE("2505", "Unknown Target Language"),
    WARNING_MANY_DESIGNATIONS("2506", "More than one Display Name was found in status \"Current\" and none of them has IS_PREFERRED tag set"),
    WARNING_VS_DOESNT_MATCH("2507", "ValueSet doesn't match for selected CodeSystemConcept"),
    WARNING_CODE_SYSTEM_NAME_DOESNT_MATCH("2508", "CodeSystem name doesn't match provided name"),
    WARNING_CONCEPT_STATUS_NOT_CURRENT("2509", "Concept STATUS is not \"Current\"");

    /**
     * Exception code
     */
    private final String code;

    /**
     * Exception description (issue - is English description/constant enough ?)
     */
    private final String description;

    /**
     * Default enum constructor
     *
     * @param code        - TSAM error code.
     * @param description - Description of the error providing information about the context.
     */
    TSAMError(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * @return String - code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return String - Description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String getCodeSystem() {
        return null;
    }

    /**
     * @return String in format code:description
     */
    @Override
    public String toString() {
        return code + ": " + description;
    }
}
