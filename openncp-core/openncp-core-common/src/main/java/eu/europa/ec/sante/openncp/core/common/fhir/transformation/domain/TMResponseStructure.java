package eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain;

import eu.europa.ec.sante.openncp.core.common.tsam.error.ITMTSAMError;
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
    private List<ITMTSAMError> errors;

    /**
     * List of TM Warnings
     */
    private List<ITMTSAMError> warnings;

    /**
     * failure or success
     */
    private String status;

    public TMResponseStructure(final Bundle fhirDocument, final String status, final List<ITMTSAMError> errors, final List<ITMTSAMError> warnings) {
        this.fhirDocument = fhirDocument;
        this.errors = errors;
        this.warnings = warnings;
        this.status = status;
    }

    public Bundle getFhirDocument() {
        return fhirDocument;
    }

    public void setFhirDocument(final Bundle fhirDocument) {
        this.fhirDocument = fhirDocument;
    }

    public List<ITMTSAMError> getErrors() {
        return errors;
    }

    public void setErrors(final List<ITMTSAMError> errors) {
        this.errors = errors;
    }

    public List<ITMTSAMError> getWarnings() {
        return warnings;
    }

    public void setWarnings(final List<ITMTSAMError> warnings) {
        this.warnings = warnings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}
