package eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain;

import org.w3c.dom.Document;

public class TranslateRequest {


    private Document pivotFHIR;
    private String targetLanguageCode;

    public Document getPivotFHIR() {
        return pivotFHIR;
    }

    public void setPivotFHIR(Document pivotFHIR) {
        this.pivotFHIR = pivotFHIR;
    }

    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }

    public void setTargetLanguageCode(String targetLanguageCode) {
        this.targetLanguageCode = targetLanguageCode;
    }
}
