package eu.europa.ec.sante.ehdsi.openncp.gateway.cfg;

import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.entities.SMPFieldProperties;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.entities.SMPFile;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.entities.SMPFileOps;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.service.SimpleErrorHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Inês Garganta
 */

@Service
public class ReadSMPProperties {

    org.slf4j.Logger logger = LoggerFactory.getLogger(ReadSMPProperties.class);
    @Autowired
    private Environment env;

    private String type;
    private SMPFieldProperties uri;
    private SMPFieldProperties issuanceType;
    private SMPFieldProperties serviceActDate;
    private SMPFieldProperties serviceExpDate;
    private SMPFieldProperties certificate;
    private SMPFieldProperties serviceDesc;
    private SMPFieldProperties techContact;
    private SMPFieldProperties techInformation;
    private SMPFieldProperties extension;
    private SMPFieldProperties redirectHref;
    private SMPFieldProperties certificateUID;
    private SMPFieldProperties requireBusinessLevelSignature;
    private SMPFieldProperties minimumAuthLevel;

    /**
     * Read the properties for the html forms from the smpeditor.properties file
     *
     * @param smpfile
     */
    public void readProperties(SMPFile smpfile) {

        type = env.getProperty("type." + smpfile.getType().name()); //smpeditor.properties

        boolean uriEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".uri.enable"));
        boolean uriMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".uri.mandatory"));
        uri = new SMPFieldProperties();
        uri.setEnable(uriEnable);
        uri.setMandatory(uriMandatory);

        boolean issuanceEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".issuanceType.enable"));
        boolean issuanceMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".issuanceType.mandatory"));
        issuanceType = new SMPFieldProperties();
        issuanceType.setEnable(issuanceEnable);
        issuanceType.setMandatory(issuanceMandatory);

        boolean serviceActDateEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceActivationDate.enable"));
        boolean serviceActDateMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceActivationDate.mandatory"));
        serviceActDate = new SMPFieldProperties();
        serviceActDate.setEnable(serviceActDateEnable);
        serviceActDate.setMandatory(serviceActDateMandatory);

        boolean serviceExpDateEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceExpirationDate.enable"));
        boolean serviceExpDateMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceExpirationDate.mandatory"));
        serviceExpDate = new SMPFieldProperties();
        serviceExpDate.setEnable(serviceExpDateEnable);
        serviceExpDate.setMandatory(serviceExpDateMandatory);

        boolean certificateEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".certificate.enable"));
        certificate = new SMPFieldProperties();
        certificate.setEnable(certificateEnable);

        boolean serviceDescEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceDescription.enable"));
        boolean serviceDescMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceDescription.mandatory"));
        serviceDesc = new SMPFieldProperties();
        serviceDesc.setEnable(serviceDescEnable);
        serviceDesc.setMandatory(serviceDescMandatory);

        boolean techContactEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalContactUrl.enable"));
        boolean techContactMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalContactUrl.mandatory"));
        techContact = new SMPFieldProperties();
        techContact.setEnable(techContactEnable);
        techContact.setMandatory(techContactMandatory);

        boolean techInformationEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalInformationUrl.enable"));
        boolean techInformationMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalInformationUrl.mandatory"));
        techInformation = new SMPFieldProperties();
        techInformation.setEnable(techInformationEnable);
        techInformation.setMandatory(techInformationMandatory);

        boolean extensionEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".extension.enable"));
        boolean extensionMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".extension.mandatory"));
        extension = new SMPFieldProperties();
        extension.setEnable(extensionEnable);
        extension.setMandatory(extensionMandatory);

        boolean busSigEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".requireBussinessLevelSignature.enable"));
        requireBusinessLevelSignature = new SMPFieldProperties();
        requireBusinessLevelSignature.setEnable(busSigEnable);

        boolean authLevelEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".minimumAuthLevel.enable"));
        minimumAuthLevel = new SMPFieldProperties();
        minimumAuthLevel.setEnable(authLevelEnable);

        boolean redirectHrefEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".redirectHref.enable"));
        boolean redirectHrefMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".redirectHref.mandatory"));
        redirectHref = new SMPFieldProperties();
        redirectHref.setEnable(redirectHrefEnable);
        redirectHref.setMandatory(redirectHrefMandatory);

        boolean certificateUIDEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".certificateUID.enable"));
        boolean certificateUIDMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".certificateUID.mandatory"));
        certificateUID = new SMPFieldProperties();
        certificateUID.setEnable(certificateUIDEnable);
        certificateUID.setMandatory(certificateUIDMandatory);
    }

    /**
     * Read the properties for the html forms from the smpeditor.properties file
     *
     * @param smpfile
     */
    public void readProperties(SMPFileOps smpfile) {

        boolean uriEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".uri.enable"));
        boolean uriMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".uri.mandatory"));
        uri = new SMPFieldProperties();
        uri.setEnable(uriEnable);
        uri.setMandatory(uriMandatory);

        boolean issuanceEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".issuanceType.enable"));
        boolean issuanceMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".issuanceType.mandatory"));
        issuanceType = new SMPFieldProperties();
        issuanceType.setEnable(issuanceEnable);
        issuanceType.setMandatory(issuanceMandatory);

        boolean serviceActDateEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceActivationDate.enable"));
        logger.debug("\n ****************** serviceActDateEnable - '{}'", serviceActDateEnable);
        boolean serviceActDateMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceActivationDate.mandatory"));
        logger.debug("\n ****************** serviceActDateMandatory - '{}'", serviceActDateMandatory);
        serviceActDate = new SMPFieldProperties();
        serviceActDate.setEnable(serviceActDateEnable);
        serviceActDate.setMandatory(serviceActDateMandatory);

        boolean serviceExpDateEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceExpirationDate.enable"));
        logger.debug("\n ****************** serviceExpDateEnable - '{}'", serviceExpDateEnable);
        boolean serviceExpDateMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceExpirationDate.mandatory"));
        logger.debug("\n ****************** serviceExpDateMandatory - '{}'", serviceExpDateMandatory);
        serviceExpDate = new SMPFieldProperties();
        serviceExpDate.setEnable(serviceExpDateEnable);
        serviceExpDate.setMandatory(serviceExpDateMandatory);

        boolean certificateEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".certificate.enable"));
        certificate = new SMPFieldProperties();
        certificate.setEnable(certificateEnable);

        boolean serviceDescEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceDescription.enable"));
        boolean serviceDescMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".serviceDescription.mandatory"));
        serviceDesc = new SMPFieldProperties();
        serviceDesc.setEnable(serviceDescEnable);
        serviceDesc.setMandatory(serviceDescMandatory);

        boolean techContactEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalContactUrl.enable"));
        boolean techContactMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalContactUrl.mandatory"));
        techContact = new SMPFieldProperties();
        techContact.setEnable(techContactEnable);
        techContact.setMandatory(techContactMandatory);

        boolean techInformationEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalInformationUrl.enable"));
        boolean techInformationMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".technicalInformationUrl.mandatory"));
        techInformation = new SMPFieldProperties();
        techInformation.setEnable(techInformationEnable);
        techInformation.setMandatory(techInformationMandatory);

        boolean extensionEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".extension.enable"));
        boolean extensionMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".extension.mandatory"));
        extension = new SMPFieldProperties();
        extension.setEnable(extensionEnable);
        extension.setMandatory(extensionMandatory);

        boolean busSigEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".requireBussinessLevelSignature.enable"));
        requireBusinessLevelSignature = new SMPFieldProperties();
        requireBusinessLevelSignature.setEnable(busSigEnable);

        boolean authLevelEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".minimumAuthLevel.enable"));
        minimumAuthLevel = new SMPFieldProperties();
        minimumAuthLevel.setEnable(authLevelEnable);

        boolean redirectHrefEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".redirectHref.enable"));
        boolean redirectHrefMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".redirectHref.mandatory"));
        redirectHref = new SMPFieldProperties();
        redirectHref.setEnable(redirectHrefEnable);
        redirectHref.setMandatory(redirectHrefMandatory);

        boolean certificateUIDEnable = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".certificateUID.enable"));
        boolean certificateUIDMandatory = Boolean.parseBoolean(env.getProperty(smpfile.getType().name() + ".certificateUID.mandatory"));
        certificateUID = new SMPFieldProperties();
        certificateUID.setEnable(certificateUIDEnable);
        certificateUID.setMandatory(certificateUIDMandatory);
    }

    public Map readPropertiesFile() {

        logger.debug("\n *************** in readPropertiesFile **************");

        Properties properties = new Properties();
        String filename = "/smpeditor.properties";

        Resource resource = new ClassPathResource(filename);

        InputStream input = null;
        try {
            input = resource.getInputStream();
        } catch (IOException ex) {
            logger.error("\n IOException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
        }

        if (input == null) {
            logger.error("\n Unable to find - '{}'", filename);
        }

        try {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("\n IOException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
        }

        Enumeration<?> keys = properties.propertyNames();
        HashMap<String, String> propertiesMap = new HashMap<>();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = properties.getProperty(key);
            propertiesMap.put(value, key);
        }

        return propertiesMap;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SMPFieldProperties getUri() {
        return uri;
    }

    public void setUri(SMPFieldProperties uri) {
        this.uri = uri;
    }

    public SMPFieldProperties getIssuanceType() {
        return issuanceType;
    }

    public void setIssuanceType(SMPFieldProperties issuanceType) {
        this.issuanceType = issuanceType;
    }

    public SMPFieldProperties getServiceActDate() {
        return serviceActDate;
    }

    public void setServiceActDate(SMPFieldProperties serviceActDate) {
        this.serviceActDate = serviceActDate;
    }

    public SMPFieldProperties getServiceExpDate() {
        return serviceExpDate;
    }

    public void setServiceExpDate(SMPFieldProperties serviceExpDate) {
        this.serviceExpDate = serviceExpDate;
    }

    public SMPFieldProperties getCertificate() {
        return certificate;
    }

    public void setCertificate(SMPFieldProperties certificate) {
        this.certificate = certificate;
    }

    public SMPFieldProperties getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(SMPFieldProperties serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public SMPFieldProperties getTechContact() {
        return techContact;
    }

    public void setTechContact(SMPFieldProperties techContact) {
        this.techContact = techContact;
    }

    public SMPFieldProperties getTechInformation() {
        return techInformation;
    }

    public void setTechInformation(SMPFieldProperties techInformation) {
        this.techInformation = techInformation;
    }

    public SMPFieldProperties getExtension() {
        return extension;
    }

    public void setExtension(SMPFieldProperties extension) {
        this.extension = extension;
    }

    public SMPFieldProperties getRedirectHref() {
        return redirectHref;
    }

    public void setRedirectHref(SMPFieldProperties redirectHref) {
        this.redirectHref = redirectHref;
    }

    public SMPFieldProperties getCertificateUID() {
        return certificateUID;
    }

    public void setCertificateUID(SMPFieldProperties certificateUID) {
        this.certificateUID = certificateUID;
    }

    public SMPFieldProperties getRequireBusinessLevelSignature() {
        return requireBusinessLevelSignature;
    }

    public void setRequireBusinessLevelSignature(SMPFieldProperties requireBusinessLevelSignature) {
        this.requireBusinessLevelSignature = requireBusinessLevelSignature;
    }

    public SMPFieldProperties getMinimumAuthLevel() {
        return minimumAuthLevel;
    }

    public void setMinimumAuthLevel(SMPFieldProperties minimumAuthLevel) {
        this.minimumAuthLevel = minimumAuthLevel;
    }
}