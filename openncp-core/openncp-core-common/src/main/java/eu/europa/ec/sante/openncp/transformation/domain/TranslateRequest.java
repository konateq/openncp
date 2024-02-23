package eu.europa.ec.sante.openncp.transformation.domain;

public class TranslateRequest {

    private String pivotCDA;
    private String targetLanguageCode;

    public String getPivotCDA() {
        return pivotCDA;
    }

    public void setPivotCDA(String pivotCDA) {
        this.pivotCDA = pivotCDA;
    }

    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }

    public void setTargetLanguageCode(String targetLanguageCode) {
        this.targetLanguageCode = targetLanguageCode;
    }
}
