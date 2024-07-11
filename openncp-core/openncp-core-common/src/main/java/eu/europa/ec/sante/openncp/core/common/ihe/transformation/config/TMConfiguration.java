package eu.europa.ec.sante.openncp.core.common.ihe.transformation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:${EPSOS_PROPS_PATH}tm.properties")
public class TMConfiguration {
    @Value("${tm.audittrail.facility}")
    private String auditTrailFacility;

    @Value("${tm.audittrail.severity}")
    private String auditTrailSeverity;

    @Value("${tm.ncp.side}")
    private String ncpSide;

    @Value( "${tm.schematron.validation.enabled}" )
    private boolean schematronValidationEnabled;

    @Value( "${tm.schema.validation.enabled}" )
    private boolean schemaValidationEnabled;

    @Value("${EPSOS_PROPS_PATH}${tm.schemafilepath}")
    private String schemaFilePath;

    @Value( "${tm.mda.validation.enabled}" )
    private boolean modelValidationEnabled;

    @Value( "${EPSOS_PROPS_PATH}${tm.mda.cda_xsd_path}" )
    private String mdaCdaXsdPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.mda.cda_epsos_xsd_path}" )
    private String mdaCdaEpsosXsdPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.mda.cda_xsl_transformer_path}" )
    private String mdaCdaXslTransformerPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.mda.value_set_repository_path}" )
    private String mdaValuesetRepositoryPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.patientsummary.friendly}" )
    private String patientSummarySchematronFriendlyPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.eprescription.friendly}" )
    private String ePrescriptionSchematronFriendlyPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.edispensation.friendly}" )
    private String eDispensationSchematronFriendlyPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.hcer.friendly}" )
    private String hcerSchematronFriendlyPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.mro.friendly}" )
    private String mroSchematronFriendlyPath;

    @Value( "${tm.schematron.path.scannedDocument.friendly}" )
    private String scannedDocFriendlyPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.patientsummary.pivot}" )
    private String patientSummarySchematronPivotPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.eprescription.pivot}" )
    private String ePrescriptionSchematronPivotPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.edispensation.pivot}" )
    private String eDispensationSchematronPivotPath;

    @Value( "${EPSOS_PROPS_PATH}{tm.schematron.path.hcer.pivot}" )
    private String hcerSchematronPivotPath;

    @Value( "${EPSOS_PROPS_PATH}${tm.schematron.path.mro.pivot}" )
    private String mroSchematronPivotPath;

    @Value( "${tm.schematron.path.scannedDocument.pivot}" )
    private String scannedDocPivotPath;

    @Value( "${tm.documenttype.patientsummary}" )  // LOINC codes could also be hardcoded as it is unlikely they will change - 60591-5
    private String patientSummaryCode;

    @Value( "${tm.documenttype.eprescription}" )  // LOINC codes could also be hardcoded as it is unlikely they will change - 57833-6
    private String ePrescriptionCode;

    @Value("${tm.documenttype.edispensation}") // LOINC codes could also be hardcoded as it is unlikely they will change - 60593-1
    private String eDispensationCode;

    @Value( "${tm.documenttype.hcer}" ) // LOINC codes could also be hardcoded as it is unlikely they will change - 34133-9
    private String hcerCode;

    @Value( "${tm.documenttype.mro}" ) // LOINC codes could also be hardcoded as it is unlikely they will change - 56445-0
    private String mroCode;


    @Value("${tm.codedelementlist.path}")
    private String codedElementListPath;


    @Value("${tm.codedelementlist.enabled}")
    private boolean configurableElementIdentification;


    @Value("${tm.codedelementlist.overridden}")
    private boolean codedElementListOverride;

    public String getCodedElementListPath() {
        return codedElementListPath;
    }

    public void setCodedElementListPath(final String codedElementListPath) {
        this.codedElementListPath = codedElementListPath;
    }

    public boolean isConfigurableElementIdentification() {
        return configurableElementIdentification;
    }

    public void setConfigurableElementIdentification(final boolean configurableElementIdentification) {
        this.configurableElementIdentification = configurableElementIdentification;
    }

    public boolean isCodedElementListOverride() {
        return codedElementListOverride;
    }

    public void setCodedElementListOverride(final boolean codedElementListOverride) {
        this.codedElementListOverride = codedElementListOverride;
    }

    public boolean isSchematronValidationEnabled() {
        return schematronValidationEnabled;
    }

    public void setSchematronValidationEnabled(final boolean schematronValidationEnabled) {
        this.schematronValidationEnabled = schematronValidationEnabled;
    }

    public boolean isSchemaValidationEnabled() {
        return schemaValidationEnabled;
    }

    public void setSchemaValidationEnabled(final boolean schemaValidationEnabled) {
        this.schemaValidationEnabled = schemaValidationEnabled;
    }

    public boolean isModelValidationEnabled() {
        return modelValidationEnabled;
    }

    public void setModelValidationEnabled(final boolean modelValidationEnabled) {
        this.modelValidationEnabled = modelValidationEnabled;
    }

    public String getAuditTrailFacility() {
        return auditTrailFacility;
    }

    public void setAuditTrailFacility(final String auditTrailFacility) {
        this.auditTrailFacility = auditTrailFacility;
    }

    public String getAuditTrailSeverity() {
        return auditTrailSeverity;
    }

    public void setAuditTrailSeverity(final String auditTrailSeverity) {
        this.auditTrailSeverity = auditTrailSeverity;
    }

    public String getNcpSide() {
        return ncpSide;
    }

    public void setNcpSide(final String ncpSide) {
        this.ncpSide = ncpSide;
    }

    public String getMdaCdaXsdPath() {
        return mdaCdaXsdPath;
    }

    public void setMdaCdaXsdPath(final String mdaCdaXsdPath) {
        this.mdaCdaXsdPath = mdaCdaXsdPath;
    }

    public String getMdaCdaEpsosXsdPath() {
        return mdaCdaEpsosXsdPath;
    }

    public void setMdaCdaEpsosXsdPath(final String mdaCdaEpsosXsdPath) {
        this.mdaCdaEpsosXsdPath = mdaCdaEpsosXsdPath;
    }

    public String getMdaCdaXslTransformerPath() {
        return mdaCdaXslTransformerPath;
    }

    public void setMdaCdaXslTransformerPath(final String mdaCdaXslTransformerPath) {
        this.mdaCdaXslTransformerPath = mdaCdaXslTransformerPath;
    }

    public String getMdaValuesetRepositoryPath() {
        return mdaValuesetRepositoryPath;
    }

    public void setMdaValuesetRepositoryPath(final String mdaValuesetRepositoryPath) {
        this.mdaValuesetRepositoryPath = mdaValuesetRepositoryPath;
    }

    public String getSchemaFilePath() {
        return schemaFilePath;
    }

    public void setSchemaFilePath(final String schemaFilePath) {
        this.schemaFilePath = schemaFilePath;
    }


    public String getPatientSummarySchematronFriendlyPath() {
        return patientSummarySchematronFriendlyPath;
    }

    public void setPatientSummarySchematronFriendlyPath(final String patientSummarySchematronFriendlyPath) {
        this.patientSummarySchematronFriendlyPath = patientSummarySchematronFriendlyPath;
    }

    public String getePrescriptionSchematronFriendlyPath() {
        return ePrescriptionSchematronFriendlyPath;
    }

    public void setePrescriptionSchematronFriendlyPath(final String ePrescriptionSchematronFriendlyPath) {
        this.ePrescriptionSchematronFriendlyPath = ePrescriptionSchematronFriendlyPath;
    }

    public String geteDispensationSchematronFriendlyPath() {
        return eDispensationSchematronFriendlyPath;
    }

    public void seteDispensationSchematronFriendlyPath(final String eDispensationSchematronFriendlyPath) {
        this.eDispensationSchematronFriendlyPath = eDispensationSchematronFriendlyPath;
    }

    public String getHcerSchematronFriendlyPath() {
        return hcerSchematronFriendlyPath;
    }

    public void setHcerSchematronFriendlyPath(final String hcerSchematronFriendlyPath) {
        this.hcerSchematronFriendlyPath = hcerSchematronFriendlyPath;
    }

    public String getMroSchematronFriendlyPath() {
        return mroSchematronFriendlyPath;
    }

    public void setMroSchematronFriendlyPath(final String mroSchematronFriendlyPath) {
        this.mroSchematronFriendlyPath = mroSchematronFriendlyPath;
    }

    public String getScannedDocFriendlyPath() {
        return scannedDocFriendlyPath;
    }

    public void setScannedDocFriendlyPath(final String scannedDocFriendlyPath) {
        this.scannedDocFriendlyPath = scannedDocFriendlyPath;
    }

    public String getPatientSummarySchematronPivotPath() {
        return patientSummarySchematronPivotPath;
    }

    public void setPatientSummarySchematronPivotPath(final String patientSummarySchematronPivotPath) {
        this.patientSummarySchematronPivotPath = patientSummarySchematronPivotPath;
    }

    public String getePrescriptionSchematronPivotPath() {
        return ePrescriptionSchematronPivotPath;
    }

    public void setePrescriptionSchematronPivotPath(final String ePrescriptionSchematronPivotPath) {
        this.ePrescriptionSchematronPivotPath = ePrescriptionSchematronPivotPath;
    }

    public String geteDispensationSchematronPivotPath() {
        return eDispensationSchematronPivotPath;
    }

    public void seteDispensationSchematronPivotPath(final String eDispensationSchematronPivotPath) {
        this.eDispensationSchematronPivotPath = eDispensationSchematronPivotPath;
    }

    public String getHcerSchematronPivotPath() {
        return hcerSchematronPivotPath;
    }

    public void setHcerSchematronPivotPath(final String hcerSchematronPivotPath) {
        this.hcerSchematronPivotPath = hcerSchematronPivotPath;
    }

    public String getMroSchematronPivotPath() {
        return mroSchematronPivotPath;
    }

    public void setMroSchematronPivotPath(final String mroSchematronPivotPath) {
        this.mroSchematronPivotPath = mroSchematronPivotPath;
    }

    public String getScannedDocPivotPath() {
        return scannedDocPivotPath;
    }

    public void setScannedDocPivotPath(final String scannedDocPivotPath) {
        this.scannedDocPivotPath = scannedDocPivotPath;
    }


    public String getPatientSummaryCode() {
        return patientSummaryCode;
    }

    public void setPatientSummaryCode(final String patientSummaryCode) {
        this.patientSummaryCode = patientSummaryCode;
    }

    public String getePrescriptionCode() {
        return ePrescriptionCode;
    }

    public void setePrescriptionCode(final String ePrescriptionCode) {
        this.ePrescriptionCode = ePrescriptionCode;
    }

    public String getHcerCode() {
        return hcerCode;
    }

    public void setHcerCode(final String hcerCode) {
        this.hcerCode = hcerCode;
    }

    public String getMroCode() {
        return mroCode;
    }

    public void setMroCode(final String mroCode) {
        this.mroCode = mroCode;
    }

    public String geteDispensationCode() {
        return eDispensationCode;
    }

    public void seteDispensationCode(final String eDispensationCode) {
        this.eDispensationCode = eDispensationCode;
    }
}
