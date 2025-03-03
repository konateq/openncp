package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.domain;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Data necessary to update or sign a SMPFile.
 */
public class SMPFileOps {

    private String fileName;
    private File generatedFile;
    private MultipartFile updateFile;
    private SMPType type;
    private String country;
    //ServiceInformation
    private String issuanceType;
    private String documentIdentifier;
    private String documentIdentifierScheme;
    private String participantIdentifier;
    private String participantIdentifierScheme;
    private String processIdentifier;
    private String processIdentifierScheme;
    private String transportProfile;
    private Boolean requiredBusinessLevelSig;
    private String minimumAutenticationLevel;
    private String endpointURI;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date serviceActivationDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date serviceExpirationDate;
    private String certificateContent;
    private byte[] certificate;
    private FileInputStream certificateFile;
    private String serviceDescription;
    private String technicalContactUrl;
    private String technicalInformationUrl;
    private String extensionContent;
    private Element extension;
    private MultipartFile extensionFile;
    //Redirect
    private String href;
    private String certificateUID;
    //Sign
    private File fileToSign;
    private String signFileName;
    private String serviceActivationDateS;
    private String serviceExpirationDateS;

    private Alert alert;

    public String getIssuanceType() {
        return issuanceType;
    }

    public void setIssuanceType(String issuanceType) {
        this.issuanceType = issuanceType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getGeneratedFile() {
        return generatedFile;
    }

    public void setGeneratedFile(File generatedFile) {
        this.generatedFile = generatedFile;
    }

    public MultipartFile getUpdateFile() {
        return updateFile;
    }

    public void setUpdateFile(MultipartFile updateFile) {
        this.updateFile = updateFile;
    }

    public SMPType getType() {
        return type;
    }

    public void setType(SMPType type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public String getDocumentIdentifierScheme() {
        return documentIdentifierScheme;
    }

    public void setDocumentIdentifierScheme(String documentIdentifierScheme) {
        this.documentIdentifierScheme = documentIdentifierScheme;
    }

    public String getParticipantIdentifier() {
        return participantIdentifier;
    }

    public void setParticipantIdentifier(String participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public String getParticipantIdentifierScheme() {
        return participantIdentifierScheme;
    }

    public void setParticipantIdentifierScheme(String participantIdentifierScheme) {
        this.participantIdentifierScheme = participantIdentifierScheme;
    }

    public String getProcessIdentifier() {
        return processIdentifier;
    }

    public void setProcessIdentifier(String processIdentifier) {
        this.processIdentifier = processIdentifier;
    }

    public String getProcessIdentifierScheme() {
        return processIdentifierScheme;
    }

    public void setProcessIdentifierScheme(String processIdentifierScheme) {
        this.processIdentifierScheme = processIdentifierScheme;
    }

    public String getTransportProfile() {
        return transportProfile;
    }

    public void setTransportProfile(String transportProfile) {
        this.transportProfile = transportProfile;
    }

    public Boolean getRequiredBusinessLevelSig() {
        return requiredBusinessLevelSig;
    }

    public void setRequiredBusinessLevelSig(Boolean requiredBusinessLevelSig) {
        this.requiredBusinessLevelSig = requiredBusinessLevelSig;
    }

    public String getMinimumAutenticationLevel() {
        return minimumAutenticationLevel;
    }

    public void setMinimumAutenticationLevel(String minimumAutenticationLevel) {
        this.minimumAutenticationLevel = minimumAutenticationLevel;
    }

    public String getEndpointURI() {
        return endpointURI;
    }

    public void setEndpointURI(String endpointURI) {
        this.endpointURI = endpointURI;
    }

    public Date getServiceActivationDate() {
        return serviceActivationDate;
    }

    public void setServiceActivationDate(Date serviceActivationDate) {
        this.serviceActivationDate = serviceActivationDate;
    }

    public Date getServiceExpirationDate() {
        return serviceExpirationDate;
    }

    public void setServiceExpirationDate(Date serviceExpirationDate) {
        this.serviceExpirationDate = serviceExpirationDate;
    }

    public String getCertificateContent() {
        return certificateContent;
    }

    public void setCertificateContent(String certificateContent) {
        this.certificateContent = certificateContent;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public FileInputStream getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(FileInputStream certificateFile) {
        this.certificateFile = certificateFile;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getTechnicalContactUrl() {
        return technicalContactUrl;
    }

    public void setTechnicalContactUrl(String technicalContactUrl) {
        this.technicalContactUrl = technicalContactUrl;
    }

    public String getTechnicalInformationUrl() {
        return technicalInformationUrl;
    }

    public void setTechnicalInformationUrl(String technicalInformationUrl) {
        this.technicalInformationUrl = technicalInformationUrl;
    }

    public String getExtensionContent() {
        return extensionContent;
    }

    public void setExtensionContent(String extensionContent) {
        this.extensionContent = extensionContent;
    }

    public Element getExtension() {
        return extension;
    }

    public void setExtension(Element extension) {
        this.extension = extension;
    }

    public MultipartFile getExtensionFile() {
        return extensionFile;
    }

    public void setExtensionFile(MultipartFile extensionFile) {
        this.extensionFile = extensionFile;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getCertificateUID() {
        return certificateUID;
    }

    public void setCertificateUID(String certificateUID) {
        this.certificateUID = certificateUID;
    }

    public String getSignFileName() {
        return signFileName;
    }

    public void setSignFileName(String signFileName) {
        this.signFileName = signFileName;
    }

    public String getServiceActivationDateS() {
        return serviceActivationDateS;
    }

    public void setServiceActivationDateS(String serviceActivationDateS) {
        this.serviceActivationDateS = serviceActivationDateS;
    }

    public String getServiceExpirationDateS() {
        return serviceExpirationDateS;
    }

    public void setServiceExpirationDateS(String serviceExpirationDateS) {
        this.serviceExpirationDateS = serviceExpirationDateS;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public File getFileToSign() {
        return fileToSign;
    }

    public void setFileToSign(File fileToSign) {
        this.fileToSign = fileToSign;
    }
}
