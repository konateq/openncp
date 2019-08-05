package epsos.ccd.posam.tm.util;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Class/Singleton stores configurable parameters for TM module Parameters are written in tm.properties file.
 *
 * @author Frantisek Rudik.
 */
public class TMConfiguration implements InitializingBean {

    private static TMConfiguration instance = null;

    private String patientSummaryCode;
    private String ePrescriptionCode;
    private String eDispensationCode;
    private String hcerCode;
    private String mroCode;
    private String schemaFilePath;
    private String patientSummarySchematronFriendlyPath;
    private String ePrescriptionSchematronFriendlyPath;
    private String eDispensationSchematronFriendlyPath;
    private String hcerSchematronFriendlyPath;
    private String mroSchematronFriendlyPath;
    private String scannedDocFriendlyPath;
    private String patientSummarySchematronPivotPath;
    private String ePrescriptionSchematronPivotPath;
    private String eDispensationSchematronPivotPath;
    private String hcerSchematronPivotPath;
    private String mroSchematronPivotPath;
    private String scannedDocPivotPath;
    private String scannedDocumentFriendlyPath;
    private String scannedDocumentPivotPath;
    private String mdaCdaXsdPath;
    private String mdaCdaEpsosXsdPath;
    private String mdaCdaXslTransformerPath;
    private String mdaValuesetRepositoryPath;
    private boolean schemaValidationEnabled;
    private boolean schematronValidationEnabled;
    private boolean modelValidationEnabled;
    private String auditTrailFacility;
    private String auditTrailSeverity;
    private Collection<String> docTypesCollection;
    private String ncpSide;

    private TMConfiguration() {
    }

    public static TMConfiguration getInstance() {
        if (instance == null) {
            instance = new TMConfiguration();
        }
        return instance;
    }

    public String getMdaCdaXsdPath() {
        return mdaCdaXsdPath;
    }

    public void setMdaCdaXsdPath(String mdaCdaXsdPath) {
        this.mdaCdaXsdPath = mdaCdaXsdPath;
    }

    public String getMdaCdaEpsosXsdPath() {
        return mdaCdaEpsosXsdPath;
    }

    public void setMdaCdaEpsosXsdPath(String mdaCdaEpsosXsdPath) {
        this.mdaCdaEpsosXsdPath = mdaCdaEpsosXsdPath;
    }

    public String getMdaCdaXslTransformerPath() {
        return mdaCdaXslTransformerPath;
    }

    public void setMdaCdaXslTransformerPath(String mdaCdaXslTransformerPath) {
        this.mdaCdaXslTransformerPath = mdaCdaXslTransformerPath;
    }

    public String getMdaValuesetRepositoryPath() {
        return mdaValuesetRepositoryPath;
    }

    public void setMdaValuesetRepositoryPath(String mdaValuesetRepositoryPath) {
        this.mdaValuesetRepositoryPath = mdaValuesetRepositoryPath;
    }

    public String getHcerSchematronFriendlyPath() {
        return hcerSchematronFriendlyPath;
    }

    public void setHcerSchematronFriendlyPath(String hcerSchematronFriendlyPath) {
        this.hcerSchematronFriendlyPath = hcerSchematronFriendlyPath;
    }

    public String getMroSchematronFriendlyPath() {
        return mroSchematronFriendlyPath;
    }

    public void setMroSchematronFriendlyPath(String mroSchematronFriendlyPath) {
        this.mroSchematronFriendlyPath = mroSchematronFriendlyPath;
    }

    public String getHcerSchematronPivotPath() {
        return hcerSchematronPivotPath;
    }

    public void setHcerSchematronPivotPath(String hcerSchematronPivotPath) {
        this.hcerSchematronPivotPath = hcerSchematronPivotPath;
    }

    public String getMroSchematronPivotPath() {
        return mroSchematronPivotPath;
    }

    public void setMroSchematronPivotPath(String mroSchematronPivotPath) {
        this.mroSchematronPivotPath = mroSchematronPivotPath;
    }

    public String getPatientSummaryCode() {
        return patientSummaryCode;
    }

    public void setPatientSummaryCode(String patientSummaryCode) {
        this.patientSummaryCode = patientSummaryCode;
    }

    public String getePrescriptionCode() {
        return ePrescriptionCode;
    }

    public void setePrescriptionCode(String ePrescriptionCode) {
        this.ePrescriptionCode = ePrescriptionCode;
    }

    public String geteDispensationCode() {
        return eDispensationCode;
    }

    public void seteDispensationCode(String eDispensationCode) {
        this.eDispensationCode = eDispensationCode;
    }

    public String getHcerCode() {
        return hcerCode;
    }

    public void setHcerCode(String hcerCode) {
        this.hcerCode = hcerCode;
    }

    public String getMroCode() {
        return mroCode;
    }

    public void setMroCode(String mroCode) {
        this.mroCode = mroCode;
    }

    public String getSchemaFilePath() {
        return schemaFilePath;
    }

    public void setSchemaFilePath(String schemaFilePath) {
        this.schemaFilePath = schemaFilePath;
    }

    public boolean isSchemaValidationEnabled() {
        return schemaValidationEnabled;
    }

    public void setSchemaValidationEnabled(boolean schemaValidationEnabled) {
        this.schemaValidationEnabled = schemaValidationEnabled;
    }

    public boolean isSchematronValidationEnabled() {
        return schematronValidationEnabled;
    }

    public void setSchematronValidationEnabled(boolean schematronValidationEnabled) {
        this.schematronValidationEnabled = schematronValidationEnabled;
    }

    public Collection<String> getDocTypesCollection() {
        return docTypesCollection;
    }

    public String getAuditTrailFacility() {
        return auditTrailFacility;
    }

    public void setAuditTrailFacility(String auditTrailFacility) {
        this.auditTrailFacility = auditTrailFacility;
    }

    public String getAuditTrailSeverity() {
        return auditTrailSeverity;
    }

    public void setAuditTrailSeverity(String auditTrailSeverity) {
        this.auditTrailSeverity = auditTrailSeverity;
    }

    public String getNcpSide() {
        return ncpSide;
    }

    public void setNcpSide(String ncpSide) {
        this.ncpSide = ncpSide;
    }

    public String getPatientSummarySchematronFriendlyPath() {
        return patientSummarySchematronFriendlyPath;
    }

    public void setPatientSummarySchematronFriendlyPath(String patientSummarySchematronFriendlyPath) {
        this.patientSummarySchematronFriendlyPath = patientSummarySchematronFriendlyPath;
    }

    public String getePrescriptionSchematronFriendlyPath() {
        return ePrescriptionSchematronFriendlyPath;
    }

    public void setePrescriptionSchematronFriendlyPath(String ePrescriptionSchematronFriendlyPath) {
        this.ePrescriptionSchematronFriendlyPath = ePrescriptionSchematronFriendlyPath;
    }

    public String geteDispensationSchematronFriendlyPath() {
        return eDispensationSchematronFriendlyPath;
    }

    public void seteDispensationSchematronFriendlyPath(String eDispensationSchematronFriendlyPath) {
        this.eDispensationSchematronFriendlyPath = eDispensationSchematronFriendlyPath;
    }

    public String getPatientSummarySchematronPivotPath() {
        return patientSummarySchematronPivotPath;
    }

    public void setPatientSummarySchematronPivotPath(String patientSummarySchematronPivotPath) {
        this.patientSummarySchematronPivotPath = patientSummarySchematronPivotPath;
    }

    public String getePrescriptionSchematronPivotPath() {
        return ePrescriptionSchematronPivotPath;
    }

    public void setePrescriptionSchematronPivotPath(String ePrescriptionSchematronPivotPath) {
        this.ePrescriptionSchematronPivotPath = ePrescriptionSchematronPivotPath;
    }

    public String geteDispensationSchematronPivotPath() {
        return eDispensationSchematronPivotPath;
    }

    public void seteDispensationSchematronPivotPath(String eDispensationSchematronPivotPath) {
        this.eDispensationSchematronPivotPath = eDispensationSchematronPivotPath;
    }

    public String getScannedDocFriendlyPath() {
        return scannedDocFriendlyPath;
    }

    public void setScannedDocFriendlyPath(String scannedDocFriendlyPath) {
        this.scannedDocFriendlyPath = scannedDocFriendlyPath;
    }

    public String getScannedDocPivotPath() {
        return scannedDocPivotPath;
    }

    public void setScannedDocPivotPath(String scannedDocPivotPath) {
        this.scannedDocPivotPath = scannedDocPivotPath;
    }

    public String getScannedDocumentFriendlyPath() {
        return scannedDocumentFriendlyPath;
    }

    public void setScannedDocumentFriendlyPath(String scannedDocumentFriendlyPath) {
        this.scannedDocumentFriendlyPath = scannedDocumentFriendlyPath;
    }

    public String getScannedDocumentPivotPath() {
        return scannedDocumentPivotPath;
    }

    public void setScannedDocumentPivotPath(String scannedDocumentPivotPath) {
        this.scannedDocumentPivotPath = scannedDocumentPivotPath;
    }

    public boolean isModelValidationEnabled() {
        return modelValidationEnabled;
    }

    public void setModelValidationEnabled(boolean modelValidationEnabled) {
        this.modelValidationEnabled = modelValidationEnabled;
    }

    /**
     * Initializes docTypesCollection
     */
    public void afterPropertiesSet() {
        if (docTypesCollection == null) {
            docTypesCollection = new ArrayList<>();
        }
        if (patientSummaryCode != null && patientSummaryCode.length() > 0 && !docTypesCollection.contains(patientSummaryCode)) {
            docTypesCollection.add(patientSummaryCode);
        }
        if (eDispensationCode != null && eDispensationCode.length() > 0 && !docTypesCollection.contains(eDispensationCode)) {
            docTypesCollection.add(eDispensationCode);
        }
        if (ePrescriptionCode != null && ePrescriptionCode.length() > 0 && !docTypesCollection.contains(ePrescriptionCode)) {
            docTypesCollection.add(ePrescriptionCode);
        }
    }
}
