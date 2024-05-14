package eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain;

import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TMResponseStructure {

    private final Logger logger = LoggerFactory.getLogger(TMResponseStructure.class);

    private Bundle fhirDocument;

    /**
     * List of TM Errors
     */
    private List<String> errors;

    /**
     * List of TM Warnings
     */
    private List<String> warnings;

    /**
     * failure or success
     */
    private String status;

    public TMResponseStructure(Bundle fhirDocument, String status, List<String> errors, List<String> warnings) {
        this.fhirDocument = fhirDocument;
        this.errors = errors;
        this.warnings = warnings;
        this.status = status;
    }

    public Bundle getFhirDocument() {
        return fhirDocument;
    }

    public void setFhirDocument(Bundle fhirDocument) {
        this.fhirDocument = fhirDocument;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
