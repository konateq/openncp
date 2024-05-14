package eu.europa.ec.sante.openncp.core.common.fhir.tsam.util;

public enum TSAMError implements Error {

    ERROR_PROCESSING_ERROR("4500", "Processing error (sw failure)"),
    ERROR_CODE_SYSTEM_NOTFOUND("4501", "Code System not found"),
    ERROR_CODE_SYSTEM_VERSION_NOTFOUND("4502", "Code System Version not found"),
    ERROR_CODE_SYSTEM_CONCEPT_NOTFOUND("4503", "Code System Concept not found"),
    ERROR_TARGET_CONCEPT_NOTFOUND("4504", "Target Code System Concept not found"),
    ERROR_DESIGNATION_NOTFOUND("4505", "Designation for Code System Concept not found"),
    ERROR_EPSOS_VERSION_NOTFOUND("4506", "Code System Version not found"),
    ERROR_EPSOS_CODE_SYSTEM_NOTFOUND("4507", "Code System not found"),
    ERROR_EPSOS_CS_OID_NOTFOUND("4508", "Code System OID not found"),
    ERROR_TRANSCODING_INVALID("4509", "Transcoding Association is invalid"),
    ERROR_NO_CURRENT_DESIGNATIONS("4510", "No Designations with STATUS=\"Current\" found"),
    WARNING_MANY_DESIGNATIONS("2506", "More than one Display Name was found in status \"Current\" and none of them has IS_PREFERRED tag set"),
    WARNING_VS_DOESNT_MATCH("2507", "ValueSet doesn't match for selected CodeSystemConcept"),
    WARNING_CODE_SYSTEM_NAME_DOESNT_MATCH("2508", "CodeSystem name doesn't match provided name"),
    WARNING_CONCEPT_STATUS_NOT_CURRENT("2509", "Concept STATUS is not \"Current\"");

    private final String code;

    /**
     * Exception description (issue - is English description/constant enough ?)
     */
    private final String description;

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    TSAMError(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
