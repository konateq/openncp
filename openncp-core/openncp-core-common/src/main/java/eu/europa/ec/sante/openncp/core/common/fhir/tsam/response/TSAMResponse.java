package eu.europa.ec.sante.openncp.core.common.fhir.tsam.response;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.exception.TSAMErrorCtx;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.util.TSAMError;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.util.Error;
import org.hl7.fhir.r4.model.Coding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class TSAMResponse {

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

    private Coding coding;
    private Element responseElement;

    /**
     * List od Exceptions
     */
    private List<Error> errors;

    /**
     * List od Warnings
     */
    private List<Error> warnings;

    /**
     * failure or success
     */
    private String status;

    public TSAMResponse(Coding coding) {
        super();
        this.coding = coding;
        //status is SUCCESS until Error is added
        this.status = STATUS_SUCCESS;
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public TSAMResponse(Coding coding, String status) {
        super();
        this.coding = coding;
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

    public Coding getCoding() {
        return coding;
    }

    public void setOriginalCodeableConcept(Coding originalCodeableConcept) {
        this.coding = coding;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return Response Element (translated or transcoded)
     */
    public Element getResponseElement() {
        return responseElement;
    }

    /**
     * @return List of TSAM errors
     */
    public List<Error> getErrors() {
        return (errors == null ? new ArrayList<Error>() : errors);
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    /**
     * @return List of TSAM warnings
     */
    public List<Error> getWarnings() {
        return (warnings == null ? new ArrayList<Error>() : warnings);
    }

    public void setWarnings(List<Error> warnings) {
        this.warnings = warnings;
    }

    public void addError(Error newError) {
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

    public void addWarning(Error newWarning) {
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

    /**
     * Returns xml presentation of Response Structure in form:
     * <pre>
     * &lt;responseStructure&gt;
     * &lt;responseElement&gt;
     * &lt;!-- Translated/transcoded Coded Element--&gt;
     * &lt;/responseElement&gt;
     * &lt;responseStatus&gt;
     * &lt;status result="success/failure"/&gt;
     * &lt;!-- optional --&gt;
     * &lt;errors&gt;
     * &lt;error code="..." description=".."/&gt;
     * &lt;error code="..." description=".."/&gt;
     * &lt;/errors&gt;
     * &lt;!-- optional --&gt;
     * &lt;warnings&gt;
     * &lt;warning code="..." description=".."/&gt;
     * &lt;warning code="..." description=".."/&gt;
     * &lt;/warnings&gt;
     * &lt;/responseStatus&gt;
     * &lt;/responseStructure&gt;
     * </pre>
     *
     * @return Document
     */
    public Document getDocument() {
        return null;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName) {
        this.codeSystemName = codeSystemName;
    }
}
