package eu.europa.ec.sante.openncp.core.common.tsam;

import eu.europa.ec.sante.openncp.core.common.tsam.error.ITMTSAMError;
import eu.europa.ec.sante.openncp.core.common.tsam.error.TSAMError;
import eu.europa.ec.sante.openncp.core.common.tsam.error.TSAMErrorCtx;


import java.util.ArrayList;
import java.util.List;

public class TSAMResponseStructure {

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILURE = "failure";

    /**
     * target Concept Code
     */
    private String code;

    /**
     * target Concept designation
     */
    private String designation;

    /**
     * target Concept designation language (optional)
     */
    private String language;

    /**
     * target Concept code system OID
     */
    private String codeSystem;

    /**
     * targed Concept code system Name
     */
    private String codeSystemName;

    /**
     * target Concept Code system version (optional)
     */
    private String codeSystemVersion;

    private CodeConcept codeConcept;

    /**
     * List od Exceptions
     */
    private List<ITMTSAMError> errors;

    /**
     * List od Warnings
     */
    private List<ITMTSAMError> warnings;

    /**
     * failure or success
     */
    private String status;

    public TSAMResponseStructure(final CodeConcept codeConcept) {
        this.codeConcept = codeConcept;
        //status is SUCCESS until Error is added
        this.status = STATUS_SUCCESS;
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public TSAMResponseStructure(final CodeConcept codeConcept, final String status) {
        this.codeConcept = codeConcept;
        this.status = status;
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    public String getCodeSystemVersion() {
        return codeSystemVersion;
    }

    public void setCodeSystemVersion(String codeSystemVersion) {
        this.codeSystemVersion = codeSystemVersion;
    }

    public CodeConcept getCodeConcept() {
        return codeConcept;
    }

    public void setCodeConcept(CodeConcept codeConcept) {
        this.codeConcept = codeConcept;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return List of TSAM errors
     */
    public List<ITMTSAMError> getErrors() {
        return (errors == null ? new ArrayList<>() : errors);
    }

    public void setErrors(List<ITMTSAMError> errors) {
        this.errors = errors;
    }

    /**
     * @return List of TSAM warnings
     */
    public List<ITMTSAMError> getWarnings() {
        return (warnings == null ? new ArrayList<>() : warnings);
    }

    public void setWarnings(List<ITMTSAMError> warnings) {
        this.warnings = warnings;
    }

    public void addError(ITMTSAMError newError) {
        if (!errors.contains(newError)) {
            errors.add(newError);
        }
        //if error is added, status is changed to failure
        if (status.equals(STATUS_SUCCESS)) {
            status = STATUS_FAILURE;
        }
    }

    public void addError(TSAMError newError, String ctx) {
        TSAMErrorCtx errCtx = new TSAMErrorCtx(newError, ctx);
        addError(errCtx);
    }

    public void addWarning(ITMTSAMError newWarning) {
        if (!warnings.contains(newWarning)) {
            warnings.add(newWarning);
        }
    }

    public void addWarning(TSAMError newWarning, String ctx) {
        TSAMErrorCtx warnCtx = new TSAMErrorCtx(newWarning, ctx);
        addWarning(warnCtx);
    }

    /**
     * @return true if status is SUCCESS otherwise false
     */
    public boolean isStatusSuccess() {
        return (status != null && status.equals(STATUS_SUCCESS));
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName) {
        this.codeSystemName = codeSystemName;
    }
}
