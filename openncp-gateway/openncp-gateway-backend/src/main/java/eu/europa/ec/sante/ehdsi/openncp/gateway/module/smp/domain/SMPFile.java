package eu.europa.ec.sante.ehdsi.openncp.gateway.module.smp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Structure of a SMPFile
 */
public class SMPFile {

    private String fileName;
    private File generatedFile;
    private SMPType type;
    private String country;
    private String clientServer;
    //ServiceInformation
    private String uri;
    private String issuanceType;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date serviceActivationDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date serviceExpirationDate;
    private String certificate;
    private String serviceDescription;
    private String technicalContactUrl;
    private String technicalInformationUrl;
    private String extension;
    @JsonIgnore
    private FileInputStream certificateFile;
    //Redirect
    private String href;
    private String certificateUID;

    public SMPFile() {
        super();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTechnicalContactUrl() {
        return technicalContactUrl;
    }

    public void setTechnicalContactUrl(String TechnicalContactUrl) {
        this.technicalContactUrl = TechnicalContactUrl;
    }

    public String getTechnicalInformationUrl() {
        return technicalInformationUrl;
    }

    public void setTechnicalInformationUrl(String TechnicalInformationUrl) {
        this.technicalInformationUrl = TechnicalInformationUrl;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public SMPType getType() {
        return this.type;
    }

    public void setType(SMPType type) {
        this.type = type;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getServiceActivationDate() {
        return serviceActivationDate;
    }

    public void setServiceActivationDate(Date ServiceActivationDate) {
        this.serviceActivationDate = ServiceActivationDate;
    }

    public Date getServiceExpirationDate() {
        return serviceExpirationDate;
    }

    public void setServiceExpirationDate(Date ServiceExpirationDate) {
        this.serviceExpirationDate = ServiceExpirationDate;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String Certificate) {
        this.certificate = Certificate;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String ServiceDescription) {
        this.serviceDescription = ServiceDescription;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String Extension) {
        this.extension = Extension;
    }

    public FileInputStream getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(FileInputStream certificateFile) {
        this.certificateFile = certificateFile;
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

    public String getCertificateUID() {
        return certificateUID;
    }

    public void setCertificateUID(String certificateUID) {
        this.certificateUID = certificateUID;
    }

    public String getIssuanceType() {
        return issuanceType;
    }

    public void setIssuanceType(String issuanceType) {
        this.issuanceType = issuanceType;
    }

    public String getClientServer() {
        return clientServer;
    }

    public void setClientServer(String clientServer) {
        this.clientServer = clientServer;
    }
}
