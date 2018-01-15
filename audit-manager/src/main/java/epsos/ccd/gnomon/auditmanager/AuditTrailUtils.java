package epsos.ccd.gnomon.auditmanager;

import epsos.ccd.gnomon.utils.SecurityMgr;
import epsos.ccd.gnomon.utils.Utils;
import eu.epsos.util.audit.AuditLogSerializer;
import eu.epsos.validation.datamodel.audit.AuditModel;
import eu.epsos.validation.datamodel.common.NcpSide;
import eu.epsos.validation.services.AuditValidationService;
import net.RFC3881.*;
import net.RFC3881.AuditMessage.ActiveParticipant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides methods for constructing the audit message and for sending the syslog message to the repository
 *
 * @author Kostas Karkaletsis
 * @author Organization: Gnomon
 * @author mail:k.karkaletsis@gnomon.com.gr
 * @version 1.0, 2010, 30 Jun Provides methods for creating AuditMessage
 * instances of the spSOS defined services, as for constructing and
 * sending the syslog message to the repository
 */
public enum AuditTrailUtils {

    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditTrailUtils.class);

    public static AuditTrailUtils getInstance() {
        return INSTANCE;
    }

    public synchronized static String constructMessage(AuditMessage auditmessage, boolean sign) {

        String auditmsg = "";
        LOGGER.info("Constructing message");
        String eventTypeCode = "EventTypeCode(N/A)";
        try {
            eventTypeCode = auditmessage.getEventIdentification().getEventTypeCode().get(0).getCode();
            LOGGER.debug("'{}' try to convert the message to xml using JAXB", eventTypeCode);
        } catch (NullPointerException e) {
            LOGGER.warn("Unable to log AuditMessageEventTypeCode.", e);
        }

        try {
            auditmsg = AuditTrailUtils.convertAuditObjectToXML(auditmessage);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("Message created");
        INSTANCE.writeTestAudits(auditmessage, auditmsg);
        LOGGER.info("'{}' message constructed", eventTypeCode);

        boolean validated = false;
        URL url = null;
        try {
            url = Utils.class.getClassLoader().getResource("RFC3881.xsd");
        } catch (Exception e) {
            LOGGER.error(auditmessage.getEventIdentification().getEventID().getCode() + " Error getting xsd url", e);
        }
        try {
            validated = Utils.validateSchema(auditmsg, url);
            LOGGER.info(auditmessage.getEventIdentification().getEventID().getCode() + " Validating Schema");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        boolean forceWrite = Boolean.parseBoolean(Utils.getProperty("auditrep.forcewrite", "true", true));
        if (!validated) {
            LOGGER.info(auditmessage.getEventIdentification().getEventID().getCode() + " Message not validated");
            if (!forceWrite) {
                auditmsg = "";
            }
        }
        if (validated || forceWrite) {
            if (validated) {
                LOGGER.info(auditmessage.getEventIdentification().getEventID().getCode() + " Message validated");
            } else {
                LOGGER.info(auditmessage.getEventIdentification().getEventID().getCode() + " message not validated");
            }
            if (forceWrite && !validated) {
                LOGGER.info(auditmessage.getEventIdentification().getEventID().getCode()
                        + " AuditManager is force to send the message. So trying ...");
            }

            try {
                // validate xml according to xsd
                LOGGER.debug(auditmessage.getEventIdentification().getEventID().getCode()
                        + " XML stuff: Create Dom From String");
                Document doc = Utils.createDomFromString(auditmsg);
                if (sign) {
                    // Gnomon SecMan
                    auditmsg = SecurityMgr.getSignedDocumentAsString(SecurityMgr.signDocumentEnveloped(doc));

                    LOGGER.info(auditmessage.getEventIdentification().getEventID().getCode() + " message signed");
                }
            } catch (Exception e) {
                auditmsg = "";
                LOGGER.error(auditmessage.getEventIdentification().getEventID().getCode() + " Error signing doc"
                        + e.getMessage(), e);
            }
        }
        // TODO: Audit Message built is not displayed into the logs
        // LOGGER.info("Audit Message: '{}'", auditmsg);
        return auditmsg;
    }

    /**
     * The method converts the audit message to xml format, having as input the Audit Message.
     * Uses the JAXB library to marshal the audit message object
     *
     * @param am
     * @return
     * @throws JAXBException
     */
    public synchronized static String convertAuditObjectToXML(AuditMessage am) throws JAXBException {

        LOGGER.info("Converting message - JAXB marshalling the Audit Object");
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(AuditMessage.class);
        Marshaller m = context.createMarshaller();
        try {
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (PropertyException e) {
            LOGGER.error("Unable to format converted AuditMessage to XML: '{}'", e.getMessage(), e);
        }
        m.marshal(am, sw);
        LOGGER.info("Converting message finished");
        return sw.toString();
    }

    /**
     * Method to create the audit message to be passed to the syslog client
     *
     * @param eventLog
     * @return the Audit Message object
     */
    public AuditMessage createAuditMessage(EventLog eventLog) {

        LOGGER.info("createAuditMessage(EventLog '{}')", eventLog.getEventType());
        AuditMessage am = new AuditMessage();
        AuditTrailUtils au = AuditTrailUtils.getInstance();
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosIdentificationServiceFindIdentityByTraits.getCode())) {
            am = au._CreateAuditTrailForIdentificationService(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosPatientServiceList.getCode())) {
            am = au._CreateAuditTrailForPatientService(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosPatientServiceRetrieve.getCode())) {
            am = au._CreateAuditTrailForPatientService(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosOrderServiceList.getCode())) {
            am = au._CreateAuditTrailForOrderService(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosOrderServiceRetrieve.getCode())) {
            am = au._CreateAuditTrailForOrderService(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosDispensationServiceInitialize.getCode())) {
            am = au._CreateAuditTrailForDispensationService(eventLog, "Initialize");
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosDispensationServiceDiscard.getCode())) {
            am = au._CreateAuditTrailForDispensationService(eventLog, "Discard");
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosConsentServicePut.getCode())) {
            am = au._CreateAuditTrailForConsentService(eventLog, "Put");
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosConsentServiceDiscard.getCode())) {
            am = au._CreateAuditTrailForConsentService(eventLog, "Discard");
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosConsentServicePin.getCode())) {
            am = au._CreateAuditTrailForConsentService(eventLog, "Pin");
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosHcpAuthentication.getCode())) {
            am = au._CreateAuditTrailHCPIdentity(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosTRCAssertion.getCode())) {
            am = au._CreateAuditTrailTRCA(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosNCPTrustedServiceList.getCode())) {
            am = au._CreateAuditTrailNCPTrustedServiceList(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosPivotTranslation.getCode())) {
            am = au._CreateAuditTrailPivotTranslation(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosCommunicationFailure.getCode())) {
            am = au.createAuditTrailForCommunicationFailure(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosPACRetrieve.getCode())) {
            am = au._CreateAuditTrailForPACService(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosHCERPut.getCode())) {
            am = au._CreateAuditTrailForHCERService(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosMroList.getCode())) {
            am = au._CreateAuditTrailForRequestOfData(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.epsosMroRetrieve.getCode())) {
            am = au._CreateAuditTrailForRequestOfData(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.ehealthSMPQuery.getCode())) {
            am = au._CreateAuditTrailForEhealthSMPQuery(eventLog);
        }
        if (StringUtils.equals(eventLog.getEventType(), EventType.ehealthSMPPush.getCode())) {
            am = au._CreateAuditTrailForEhealthSMPPush(eventLog);
        }
        //  Non Repudiation information are not relevant for SML/SMP process
        if (!StringUtils.equals(eventLog.getEventType(), EventType.ehealthSMPQuery.getCode())
                && !StringUtils.equals(eventLog.getEventType(), EventType.ehealthSMPPush.getCode())) {

            am = AuditTrailUtils.getInstance().addNonRepudiationSection(am, eventLog.getReqM_ParticipantObjectID(),
                    eventLog.getReqM_PatricipantObjectDetail(), eventLog.getResM_ParticipantObjectID(),
                    eventLog.getResM_PatricipantObjectDetail());
        }
        /* Invoke audit message validation services */
        boolean validated = validateAuditMessage(eventLog, am);
        LOGGER.info("Audit Message validated and  report generated: '{}'", validated);
        return am;
    }

    public AuditMessage addNonRepudiationSection(AuditMessage auditMessage, String ReqM_ParticipantObjectID,
                                                 byte[] ReqM_PatricipantObjectDetail, String ResM_ParticipantObjectID,
                                                 byte[] ResM_PatricipantObjectDetail) {

        // Request
        ParticipantObjectIdentificationType poit = new ParticipantObjectIdentificationType();
        poit.setParticipantObjectID(ReqM_ParticipantObjectID);
        poit.setParticipantObjectTypeCode(new Short("4"));
        CodedValueType PS_object = new CodedValueType();
        PS_object.setCode("req");
        PS_object.setCodeSystemName("epSOS Msg");
        PS_object.setDisplayName("Request Message");
        poit.setParticipantObjectIDTypeCode(PS_object);
        if (ReqM_PatricipantObjectDetail != null) {
            TypeValuePairType pod = new TypeValuePairType();
            pod.setType("securityheader");
            pod.setValue(ReqM_PatricipantObjectDetail);
            poit.getParticipantObjectDetail().add(pod);
        }
        auditMessage.getParticipantObjectIdentification().add(poit);

        // Response
        ParticipantObjectIdentificationType poit1 = new ParticipantObjectIdentificationType();
        poit1.setParticipantObjectID(ResM_ParticipantObjectID);
        poit1.setParticipantObjectTypeCode(new Short("4"));
        CodedValueType PS_object1 = new CodedValueType();
        PS_object1.setCode("rsp");
        PS_object1.setCodeSystemName("epSOS Msg");
        PS_object1.setDisplayName("Response Message");
        poit1.setParticipantObjectIDTypeCode(PS_object1);
        if (ResM_PatricipantObjectDetail != null) {
            TypeValuePairType pod1 = new TypeValuePairType();
            pod1.setType("securityheader");
            pod1.setValue(ResM_PatricipantObjectDetail);
            poit1.getParticipantObjectDetail().add(pod1);
        }
        auditMessage.getParticipantObjectIdentification().add(poit1);

        return auditMessage;
    }

    /**
     * Constructs an Audit Message for the Patient Privacy Audit schema in eHealth NCP Query
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForEhealthSMPQuery(EventLog eventLog) {

        AuditMessage am = createAuditTrailForEhealthSMPQuery(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), null, "SMP",
                    "eHealth Security", "SignedServiceMetadata");
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the Patient Privacy Audit schema in eHealth NCP Push
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForEhealthSMPPush(EventLog eventLog) {

        AuditMessage am = createAuditTrailForEhealthSMPPush(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), null, "SMP",
                    "eHealth Security", "SignedServiceMetadata");
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the epSOS Order Service According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForOrderService(EventLog eventLog) {
        AuditMessage am = createAuditTrailForHCPAssurance(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("4"), "12",
                    "", new Short("0"));
        }
        return am;
    }

    /**
     * Constructs an Audit Message for HCP Identity According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailHCPIdentity(EventLog eventLog) {

        AuditMessage am = createAuditTrailForHCPIdentity(eventLog);
        if (am != null) {
            // Event Target
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), null, "IdA",
                    "epSOS Security", "HCP Identity Assertion");
        }
        return am;
    }

    /**
     * Constructs an Audit Message for NCP Trusted Service List According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailNCPTrustedServiceList(EventLog eventLog) {

        AuditMessage am = createAuditTrailForNCPTrustedServiceList(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), null, "NSL",
                    "epSOS Security", "Trusted Service List");
        }
        return am;
    }

    /**
     * Constructs an Audit Message for Pivot Translation According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailPivotTranslation(EventLog eventLog) {

        AuditMessage am = createAuditTrailForPivotTranslation(eventLog);
        if (am != null) {
            // Event Target
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("4"), new Short("5"), "in",
                    "epSOS Tranlation", "Input Data");
            addEventTarget(am, eventLog.getET_ObjectID_additional(), new Short("4"), new Short("5"), "out",
                    "epSOS Tranlation", "Output Data");
        }
        return am;
    }

    /**
     * Constructs an Audit Message for TRCA According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailTRCA(EventLog eventLog) {

        AuditMessage am = createAuditTrailForTRCA(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), null, "TrcA",
                    "epSOS Security", "TRC Assertion");
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the epSOS Dispensation Service According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @param action   the action of the service
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForDispensationService(EventLog eventLog, String action) {

        AuditMessage am = createAuditTrailForHCPAssurance(eventLog);
        // Event Target
        if (am != null) {
            if (action.equals("Discard")) {
                addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("4"), "12",
                        "Discard", new Short("14"));
            } else {
                addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("4"), "12",
                        "", new Short("0"));
            }
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the epSOS HCER Service According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForHCERService(EventLog eventLog) {

        AuditMessage am = createAuditTrailForHCPAssurance(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("4"), "12",
                    "", new Short("0"));
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the epSOS Consent Service According schema is HCP Assurance
     *
     * @param eventLog the EventLog object
     * @param action   the action of the service
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForConsentService(EventLog eventLog, String action) {

        AuditMessage am = createAuditTrailForHCPAssurance(eventLog);
        if (am != null) {
            if (StringUtils.equalsIgnoreCase(action, "Discard")) {
                addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("4"), "12", action,
                        new Short("14"));
            }

            if (StringUtils.equalsIgnoreCase(action, "Put")) {
                addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("4"), "12",
                        "Put", new Short("0"));
            }
            if (StringUtils.equalsIgnoreCase(action, "Pin")) {
                addEventTarget(am, eventLog.getET_ObjectID(), new Short("4"), new Short("12"), "PIN",
                        "esSOS Security",
                        "Privacy Information Notice");
            }
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the epSOS Patient Service According
     * schema is Patient Service
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForPatientService(EventLog eventLog) {

        AuditMessage am = createAuditTrailForHCPAssurance(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("4"), "12", "",
                    new Short("0"));
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the epSOS Identification Service
     * According schema is Mapping Service
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForIdentificationService(EventLog eventLog) {

        // If patient id mapping has occurred (there is a patient source ID), use patient mapping audit scheme
        if (eventLog.getPS_PatricipantObjectID() != null) {
            return createAuditTrailForPatientMapping(eventLog);
        } else {
            return createAuditTrailForHCPAssurance(eventLog);
        }
    }

    /**
     * Constructs an Audit Message for the epSOS Identification Service According schema is Mapping Service
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForPACService(EventLog eventLog) {

        AuditMessage am = createAuditTrailForHCPAssurance(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("24"), "10", "",
                    new Short("0"));
        }
        return am;
    }

    /**
     * Constructs an Audit Message for the generic Request For Data Scheme
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage _CreateAuditTrailForRequestOfData(EventLog eventLog) {

        AuditMessage am = createAuditTrailForHCPAssurance(eventLog);
        if (am != null) {
            addEventTarget(am, eventLog.getET_ObjectID(), new Short("2"), new Short("24"), "10", "",
                    new Short("0"));
        }
        return am;
    }

    private AuditMessage addAuditSource(AuditMessage am, String auditSource) {

        AuditSourceIdentificationType auditSourceIdentification = new AuditSourceIdentificationType();
        auditSourceIdentification.setAuditSourceID(auditSource);
        am.getAuditSourceIdentification().add(auditSourceIdentification);
        return am;
    }

    private String getMappedEventType(String eventType) {

        if (eventType.equals(
                epsos.ccd.gnomon.auditmanager.EventType.epsosIdentificationServiceFindIdentityByTraits.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosIdentificationServiceFindIdentityByTraits.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosCommunicationFailure.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosCommunicationFailure.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosConsentServiceDiscard.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosConsentServiceDiscard.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosConsentServicePin.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosConsentServicePin.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosConsentServicePut.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosConsentServicePut.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosDispensationServiceDiscard.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosDispensationServiceDiscard.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosDispensationServiceInitialize.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosDispensationServiceInitialize.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosHCERPut.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosHCERPut.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosHcpAuthentication.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosHcpAuthentication.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosNCPTrustedServiceList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosNCPTrustedServiceList.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosOrderServiceList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosOrderServiceList.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosOrderServiceRetrieve.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosOrderServiceRetrieve.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosPACRetrieve.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosPACRetrieve.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosPatientServiceList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosPatientServiceList.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosPatientServiceRetrieve.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosPatientServiceRetrieve.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosPivotTranslation.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosPivotTranslation.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosTRCAssertion.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosTRCAssertion.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosMroList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosMroList.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosMroRetrieve.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.epsosMroRetrieve.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.ehealthSMPQuery.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.ehealthSMPQuery.getCode();
        }
        if (eventType.equals(epsos.ccd.gnomon.auditmanager.EventType.ehealthSMPPush.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHEEventType.ehealthSMPPush.getCode();
        }
        // TODO: Fix this issue, does the mappedEventType should be initialized?
        return "Event Type Not Mapped";
    }

    private String getMappedTransactionName(String name) {

        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosIdentificationServiceFindIdentityByTraits
                .getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosIdentificationServiceFindIdentityByTraits
                    .getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosCommunicationFailure.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosCommunicationFailure.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosConsentServiceDiscard.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosConsentServiceDiscard.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosConsentServicePin.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosConsentServicePin.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosConsentServicePut.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosConsentServicePut.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosDispensationServiceDiscard.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosDispensationServiceDiscard.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosDispensationServiceInitialize.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosDispensationServiceInitialize.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosHCERPut.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosHCERPut.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosHcpAuthentication.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosHcpAuthentication.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosNCPTrustedServiceList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosNCPTrustedServiceList.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosOrderServiceList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosOrderServiceList.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosOrderServiceRetrieve.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosOrderServiceRetrieve.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosPatientServiceList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosPatientServiceList.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosPatientServiceRetrieve.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosPatientServiceRetrieve.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosPivotTranslation.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosPivotTranslation.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosTRCAssertion.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosTRCAssertion.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosMroServiceList.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosMroServiceList.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.epsosMroServiceRetrieve.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.epsosMroServiceRetrieve.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.ehealthSMPQuery.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.ehealthSMPQuery.getCode();
        }
        if (name.equals(epsos.ccd.gnomon.auditmanager.TransactionName.ehealthSMPPush.getCode())) {
            return epsos.ccd.gnomon.auditmanager.IHETransactionName.ehealthSMPPush.getCode();
        }
        // TODO: Fix this issue, does the mappedEventType should be initialized?
        return "Transaction not Mapped";
    }

    private AuditMessage addEventIdentification(AuditMessage am, String EventType, String transactionName,
                                                String EventActionCode, XMLGregorianCalendar EventDateTime,
                                                BigInteger EventOutcomeIndicator) {

        // Change EventType to new ones
        EventIdentificationType eit = new EventIdentificationType();

        CodedValueType iheEventID = new CodedValueType();
        iheEventID.setCode(getMappedEventType(EventType));
        iheEventID.setCodeSystemName("IHE Transactions");
        iheEventID.setDisplayName(getMappedTransactionName(transactionName));
        eit.setEventID(iheEventID);

        CodedValueType eventID = new CodedValueType();
        eventID.setCode(EventType);
        eventID.setCodeSystemName("epSOS Transaction");
        eventID.setDisplayName(transactionName);
        eit.getEventTypeCode().add(eventID);

        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosPatientServiceList.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("60591-5");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("epSOS Patient Summary");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosPatientServiceRetrieve.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("60591-5");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("epSOS Patient Summary");
            eit.getEventTypeCode().add(eventID_epsos);
        }

        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosOrderServiceList.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("57833-6");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("epSOS ePrescription");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosOrderServiceRetrieve.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("57833-6");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("epSOS ePrescription");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosConsentServicePut.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("57016-8");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("Privacy Policy Acknowledgement Document");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosConsentServiceDiscard.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("57016-8");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("Privacy Policy Acknowledgement Document");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosDispensationServiceInitialize.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("60593-1");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("eDispensation");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosDispensationServiceDiscard.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("60593-1");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("eDispensation");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosHCERPut.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("34133-9");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("Summarization of Episode Note");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.epsosPACRetrieve.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("N/A");
            eventID_epsos.setCodeSystemName("epSOS LOINC");
            eventID_epsos.setDisplayName("PAC");
            eit.getEventTypeCode().add(eventID_epsos);
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.ehealthSMPQuery.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("SMP");
            eventID_epsos.setCodeSystemName("ehealth-193");
            eventID_epsos.setDisplayName("SMP::Query");
            eit.getEventTypeCode().add(eventID_epsos);

            eit.getEventID().setCode("SMP");
            eit.getEventID().setCodeSystemName("ehealth-193");
            eit.getEventID().setDisplayName("SMP::Query");
        }
        if (EventType.equals(epsos.ccd.gnomon.auditmanager.EventType.ehealthSMPPush.getCode())) {
            CodedValueType eventID_epsos = new CodedValueType();
            eventID_epsos.setCode("SMP");
            eventID_epsos.setCodeSystemName("ehealth-194");
            eventID_epsos.setDisplayName("SMP::Push");
            eit.getEventTypeCode().add(eventID_epsos);

            eit.getEventID().setCode("SMP");
            eit.getEventID().setCodeSystemName("ehealth-194");
            eit.getEventID().setDisplayName("SMP::Push");
        }

        // eit.setEventID(eventID);
        eit.setEventActionCode(EventActionCode);
        eit.setEventDateTime(EventDateTime);
        eit.setEventOutcomeIndicator(EventOutcomeIndicator); // (0,1,4,8)
        am.setEventIdentification(eit);

        // <EventTypeCode code="60591-5" codeSystemName="epSOS LOINC"
        // displayName="epSOS Patient Summary"
        // originalText="urn:uuid:1.2.3.4.5.6.7.8.9.10"/>
        return am;
    }

    private AuditMessage addPointOfCare(AuditMessage am, String PC_UserID, String PC_RoleID, boolean UserIsRequestor,
                                        String codeSystem) {

        //if (PC_UserID == null || PC_UserID.equals("")) {
        if (StringUtils.isBlank(PC_UserID)) {
            LOGGER.info("This is service provider and doesn't need Point of Care");
        } else {
            ActiveParticipant a = new ActiveParticipant();
            a.setUserID(PC_UserID);
            a.setUserIsRequestor(UserIsRequestor);
            CodedValueType roleId = new CodedValueType();
            roleId.setCode(PC_RoleID);
            roleId.setCodeSystem(codeSystem);
            a.getRoleIDCode().add(roleId);
            am.getActiveParticipant().add(a);
        }
        return am;
    }

    private AuditMessage addHumanRequestor(AuditMessage am, String HR_UserID, String HR_AlternativeUserID,
                                           String HR_RoleID, boolean UserIsRequestor) {

        ActiveParticipant hr = new ActiveParticipant();
        hr.setUserID(HR_UserID);
        hr.setAlternativeUserID(HR_AlternativeUserID);
        hr.setUserIsRequestor(UserIsRequestor);
        CodedValueType hrroleId = new CodedValueType();
        hrroleId.setCode(HR_RoleID);
        hr.getRoleIDCode().add(hrroleId);
        am.getActiveParticipant().add(hr);
        return am;
    }

    private AuditMessage addService(AuditMessage am, String SC_UserID, boolean UserIsRequestor, String code,
                                    String codeSystem, String displayName, String ipaddress) {

        if (StringUtils.isBlank(SC_UserID)) {
            LOGGER.warn("No Service, as this is Service Consumer");
        } else {
            ActiveParticipant sc = new ActiveParticipant();
            sc.setNetworkAccessPointID(ipaddress);
            sc.setNetworkAccessPointTypeCode(new Short("2"));
            sc.setUserID(SC_UserID);
            sc.setUserIsRequestor(UserIsRequestor);
            CodedValueType scroleId = new CodedValueType();
            scroleId.setCode(code);
            scroleId.setCodeSystem(codeSystem);
            scroleId.setDisplayName(displayName);
            sc.getRoleIDCode().add(scroleId);
            am.getActiveParticipant().add(sc);
        }
        return am;
    }

    private AuditMessage addParticipantObject(AuditMessage am, String PS_PatricipantObjectID, Short PS_TypeCode,
                                              Short PS_TypeRole, String PS_Name, String PS_ObjectCode, String PS_ObjectCodeName,
                                              String PS_ObjectCodeValue) {

        ParticipantObjectIdentificationType poit = new ParticipantObjectIdentificationType();
        poit.setParticipantObjectID(PS_PatricipantObjectID);
        poit.setParticipantObjectTypeCode(PS_TypeCode);
        poit.setParticipantObjectTypeCodeRole(PS_TypeRole);
        poit.setParticipantObjectName(PS_Name);
        CodedValueType PS_object = new CodedValueType();
        PS_object.setCode(PS_ObjectCode);
        PS_object.setCodeSystemName(PS_ObjectCodeName);
        PS_object.setDisplayName(PS_ObjectCodeValue);
        poit.setParticipantObjectIDTypeCode(PS_object);
        am.getParticipantObjectIdentification().add(poit);
        return am;
    }

    private AuditMessage addError(AuditMessage am, String EM_PatricipantObjectID, byte[] EM_PatricipantObjectDetail,
                                  Short EM_Code, Short EM_CodeRole, String EM_TypeCode, String EM_qualifier) {

        // Error Message
        boolean noErrorOccured;
        LOGGER.info("The EM_PatricipantObjectID is: '{}'", EM_PatricipantObjectID);
        try {
            noErrorOccured = Utils.isEmpty(EM_PatricipantObjectID);
        } catch (Exception e) {
            noErrorOccured = true;
            LOGGER.error("The participant object id is null. So no error occured" + e.getMessage(), e);
        }

        if (noErrorOccured) {
            LOGGER.info("There is no error occured");
        } else {
            ParticipantObjectIdentificationType em = new ParticipantObjectIdentificationType();
            em.setParticipantObjectID(EM_PatricipantObjectID);
            em.setParticipantObjectTypeCode(EM_Code);
            em.setParticipantObjectTypeCodeRole(EM_CodeRole);
            CodedValueType EM_object = new CodedValueType();
            EM_object.setCode(EM_TypeCode);
            em.setParticipantObjectIDTypeCode(EM_object);
            if (EM_PatricipantObjectDetail != null) {
                TypeValuePairType pod = new TypeValuePairType();
                pod.setType(EM_qualifier);
                pod.setValue(EM_PatricipantObjectDetail);
                em.getParticipantObjectDetail().add(pod);
            }
            am.getParticipantObjectIdentification().add(em);
        }
        return am;
    }

    private AuditMessage addEventTarget(AuditMessage am, String ET_ObjectID, Short TypeCode, Short TypeCodeRole,
                                        String EM_Code, String action, Short ObjectDataLifeCycle) {

        ParticipantObjectIdentificationType em = new ParticipantObjectIdentificationType();
        em.setParticipantObjectID(ET_ObjectID);
        em.setParticipantObjectTypeCode(TypeCode);
        em.setParticipantObjectTypeCodeRole(TypeCodeRole);
        CodedValueType EM_object = new CodedValueType();
        EM_object.setCode(EM_Code);
        if (action.equals("Discard") || action.equals("Pin")) {
            em.setParticipantObjectDataLifeCycle(ObjectDataLifeCycle);
        }
        em.setParticipantObjectIDTypeCode(EM_object);
        am.getParticipantObjectIdentification().add(em);
        return am;
    }

    private AuditMessage addEventTarget(AuditMessage am, String ET_ObjectID, Short ObjectTypeCode, Short ObjectDataLifeCycle,
                                        String EM_Code, String EM_CodeSystemName, String EM_DisplayName) {

        LOGGER.info("AuditMessage addEventTarget('{}','{}','{}','{}','{}','{}','{}')", am.toString(), ET_ObjectID,
                ObjectTypeCode, ObjectDataLifeCycle, EM_Code, EM_CodeSystemName, EM_DisplayName);

        ParticipantObjectIdentificationType em = new ParticipantObjectIdentificationType();
        em.setParticipantObjectID(ET_ObjectID);
        em.setParticipantObjectTypeCode(ObjectTypeCode);
        if (ObjectDataLifeCycle != null) {
            em.setParticipantObjectDataLifeCycle(ObjectDataLifeCycle);
        }
        CodedValueType EM_object = new CodedValueType();
        EM_object.setCode(EM_Code);
        EM_object.setCodeSystemName(EM_CodeSystemName);
        EM_object.setDisplayName(EM_DisplayName);
        em.setParticipantObjectIDTypeCode(EM_object);
        am.getParticipantObjectIdentification().add(em);

        return am;
    }

    /**
     * Constructs an Audit Message for Patient Privacy Audit Schema for eHealth SMP Query
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForEhealthSMPQuery(EventLog eventLog) {

        //TODO
        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(),
                    eventLog.getEI_EventActionCode(), eventLog.getEI_EventDateTime(),
                    eventLog.getEI_EventOutcomeIndicator());
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS",
                    "epSOS Service Consumer", eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS",
                    "epSOS Service Provider", eventLog.getTargetip());
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            addError(am, eventLog.getEM_PatricipantObjectID(), eventLog.getEM_PatricipantObjectDetail(), new Short("2"),
                    new Short("3"), "9", "errormsg");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for Patient Privacy Audit Schema for eHealth SMP Push
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForEhealthSMPPush(EventLog eventLog) {

        //TODOO
        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(),
                    eventLog.getEI_EventActionCode(), eventLog.getEI_EventDateTime(),
                    eventLog.getEI_EventOutcomeIndicator());
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS", "epSOS Service Consumer",
                    eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS", "epSOS Service Provider",
                    eventLog.getTargetip());
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            addError(am, eventLog.getEM_PatricipantObjectID(), eventLog.getEM_PatricipantObjectDetail(), new Short("2"),
                    new Short("3"), "9", "errormsg");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for HCP Assurance Schema
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForHCPAssurance(EventLog eventLog) {

        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(),
                    eventLog.getEI_EventActionCode(), eventLog.getEI_EventDateTime(),
                    eventLog.getEI_EventOutcomeIndicator());
            addPointOfCare(am, eventLog.getPC_UserID(), eventLog.getPC_RoleID(), true,
                    "1.3.6.1.4.1.12559.11.10.1.3.2.2.2");
            addHumanRequestor(am, eventLog.getHR_UserID(), eventLog.getHR_AlternativeUserID(), eventLog.getHR_RoleID(),
                    true);
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS", "epSOS Service Consumer",
                    eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS", "epSOS Service Provider",
                    eventLog.getTargetip());
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            addParticipantObject(am, eventLog.getPT_PatricipantObjectID(), new Short("1"), new Short("1"), "Patient",
                    "2", "RFC-3881", "Patient Number");
            addError(am, eventLog.getEM_PatricipantObjectID(), eventLog.getEM_PatricipantObjectDetail(), new Short("2"),
                    new Short("3"), "9", "errormsg");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for CommunicationFailure
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForCommunicationFailure(EventLog eventLog) {

        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            addAuditSource(am, "N/A");
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(),
                    eventLog.getEI_EventActionCode(), eventLog.getEI_EventDateTime(),
                    eventLog.getEI_EventOutcomeIndicator());
            addHumanRequestor(am, eventLog.getHR_UserID(), eventLog.getHR_AlternativeUserID(), eventLog.getHR_RoleID(),
                    true);
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS", "epSOS Service Consumer",
                    eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS", "epSOS Service Provider",
                    eventLog.getTargetip());
            addParticipantObject(am, eventLog.getPT_PatricipantObjectID(), new Short("1"), new Short("1"), "Patient",
                    "2", "RFC-3881", "Patient Number");
            addError(am, eventLog.getEM_PatricipantObjectID(), eventLog.getEM_PatricipantObjectDetail(), new Short("2"),
                    new Short("3"), "9", "errormsg");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for Issuance of a Treatment Relationship
     * Confirmation Assertion
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForTRCA(EventLog eventLog) {

        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            // Audit Source
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            // Event Identification
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(), "E",
                    eventLog.getEI_EventDateTime(), eventLog.getEI_EventOutcomeIndicator());
            // Point Of Care
            addPointOfCare(am, eventLog.getPC_UserID(), eventLog.getPC_RoleID(), true,
                    "1.3.6.1.4.1.12559.11.10.1.3.2.2.2");
            // Human Requestor
            addHumanRequestor(am, eventLog.getHR_UserID(), eventLog.getHR_AlternativeUserID(), eventLog.getHR_RoleID(),
                    true);
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS", "epSOS Service Consumer",
                    eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS", "epSOS Service Provider",
                    eventLog.getTargetip());
            addParticipantObject(am, eventLog.getPT_PatricipantObjectID(), new Short("1"), new Short("1"), "Patient",
                    "2", "RFC-3881", "Patient Number");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for NCP Trusted Service List
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForNCPTrustedServiceList(EventLog eventLog) {

        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            // Audit Source
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            // Event Identification
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(),
                    eventLog.getEI_EventActionCode(), eventLog.getEI_EventDateTime(),
                    eventLog.getEI_EventOutcomeIndicator());
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS", "epSOS Service Consumer",
                    eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS", "epSOS Service Provider",
                    eventLog.getTargetip());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for Pivot Translation of a Medical Document
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForPivotTranslation(EventLog eventLog) {

        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(), "E",
                    eventLog.getEI_EventDateTime(), eventLog.getEI_EventOutcomeIndicator());

            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS",
                    "epSOS Service Provider", eventLog.getTargetip());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for HCP Identity Assertion
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForHCPIdentity(EventLog eventLog) {

        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            // Audit Source
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            // Event Identification
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(), "E",
                    eventLog.getEI_EventDateTime(), eventLog.getEI_EventOutcomeIndicator());
            // Point Of Care
            addPointOfCare(am, eventLog.getPC_UserID(), eventLog.getPC_RoleID(), true,
                    "1.3.6.1.4.1.12559.11.10.1.3.2.2.2");
            // Human Requestor
            addHumanRequestor(am, eventLog.getHR_UserID(), eventLog.getHR_AlternativeUserID(), eventLog.getHR_RoleID(),
                    true);
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS",
                    "epSOS Service Consumer", eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS",
                    "epSOS Service Provider", eventLog.getTargetip());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    /**
     * Constructs an Audit Message for Patient Mapping Schema
     *
     * @param eventLog the EventLog object
     * @return the created AuditMessage object
     */
    private AuditMessage createAuditTrailForPatientMapping(EventLog eventLog) {

        AuditMessage am = null;
        try {
            ObjectFactory of = new ObjectFactory();
            am = of.createAuditMessage();
            addEventIdentification(am, eventLog.getEventType(), eventLog.getEI_TransactionName(),
                    eventLog.getEI_EventActionCode(), eventLog.getEI_EventDateTime(),
                    eventLog.getEI_EventOutcomeIndicator());
            addHumanRequestor(am, eventLog.getHR_UserID(), eventLog.getHR_AlternativeUserID(), eventLog.getHR_RoleID(),
                    true);
            addService(am, eventLog.getSC_UserID(), true, "ServiceConsumer", "epSOS",
                    "epSOS Service Consumer", eventLog.getSourceip());
            addService(am, eventLog.getSP_UserID(), false, "ServiceProvider", "epSOS",
                    "epSOS Service Provider", eventLog.getTargetip());
            addService(am, eventLog.getSP_UserID(), false, "MasterPatientIndex", "epSOS",
                    "Master Patient Index", eventLog.getTargetip());
            addAuditSource(am, eventLog.getAS_AuditSourceId());
            addParticipantObject(am, eventLog.getPS_PatricipantObjectID(), new Short("1"), new Short("1"),
                    "PatientSource", "2", "RFC-3881", "Patient Number");
            addParticipantObject(am, eventLog.getPT_PatricipantObjectID(), new Short("1"), new Short("1"),
                    "PatientTarget", "2", "RFC-3881", "Patient Number");
            addError(am, eventLog.getEM_PatricipantObjectID(), eventLog.getEM_PatricipantObjectDetail(), new Short("2"),
                    new Short("3"), "9", "errormsg");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(am.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return am;
    }

    private void writeTestAudits(AuditMessage auditmessage, String auditmsg) {

        String wta = Utils.getProperty("WRITE_TEST_AUDITS");
        LOGGER.info("Writing test audits: '{}'", wta);
        if (StringUtils.equals(wta, "true")) {
            LOGGER.info("Writing test audits");
            String tap = Utils.getProperty("TEST_AUDITS_PATH");
            try {
                Utils.writeXMLToFile(auditmsg, tap + (auditmessage.getEventIdentification().getEventTypeCode()
                        .get(0).getDisplayName().split("::"))[0] + "-" +
                        new SimpleDateFormat("yyyy.MM.dd'at'HH-mm-ss.SSS").format(new Date(System.currentTimeMillis())) + ".xml");
            } catch (Exception e) {
                LOGGER.error("Exception: '{}'", e.getMessage(), e);
                try {
                    Utils.writeXMLToFile(auditmsg, tap + new SimpleDateFormat("yyyy.MM.dd'at'HH-mm-ss.SSS")
                            .format(new Date(System.currentTimeMillis())) + "-ERROR.xml");
                } catch (Exception ex) {
                    LOGGER.warn("Unable to write test audit: '{}'", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void sendATNASyslogMessage(AuditLogSerializer auditLogSerializer, AuditMessage auditmessage,
                                                   String facility, String severity) {

        MessageSender t = new MessageSender(auditLogSerializer, auditmessage, facility, severity);
        LOGGER.debug("Starting new thread for sending message");
        t.start();
    }

    /**
     * Based on a given EventLog this method will infer about the correct model to apply for a message validation.
     *
     * @param eventLog an EventLog object containing information about the audit message.
     * @return the Audit Message Model
     */
    private boolean validateAuditMessage(EventLog eventLog, AuditMessage am) {

        LOGGER.info("validateAuditMessage(EventLog '{}', AudiMessage '{}', PC UserId: '{}')", eventLog.getEventType(),
                am.getEventIdentification().getEventActionCode(), eventLog.getPC_UserID());
        String model = "";
        NcpSide ncpSide;

        if (StringUtils.isBlank(eventLog.getPC_UserID())) {
            ncpSide = NcpSide.NCP_A;
        } else {
            ncpSide = NcpSide.NCP_B;
        }

        if (StringUtils.equals(eventLog.getEventType(), "epsos-cf")) {
            throw new UnsupportedOperationException("EventCode not supported.");
        }

        // Infer model according to NCP Side and EventCode
        if (ncpSide == NcpSide.NCP_A) {
            if (StringUtils.equals(eventLog.getEventType(), "epsos-11")) {
                model = AuditModel.EPSOS2_IDENTIFICATION_SERVICE_AUDIT_SP.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-21") || StringUtils.equals(eventLog.getEventType(), "epsos-22")
                    || StringUtils.equals(eventLog.getEventType(), "epsos-31") || StringUtils.equals(eventLog.getEventType(), "epsos-32")
                    || StringUtils.equals(eventLog.getEventType(), "epsos-94") || StringUtils.equals(eventLog.getEventType(), "epsos-96")
                    || StringUtils.equals(eventLog.getEventType(), "ITI-38") || StringUtils.equals(eventLog.getEventType(), "ITI-39")
                    || StringUtils.equals(eventLog.getEventType(), EventType.epsosPACRetrieve.getCode())) {
                model = AuditModel.EPSOS2_FETCH_DOC_SERVICE_SP.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-41") || StringUtils.equals(eventLog.getEventType(), "epsos-51")) {
                model = AuditModel.EPSOS2_PROVIDE_DATA_SERVICE_SP.toString();
            }
        } else {
            if (StringUtils.equals(eventLog.getEventType(), "epsos-11")) {
                model = AuditModel.EPSOS2_HCP_ASSURANCE_AUDIT.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-21") || StringUtils.equals(eventLog.getEventType(), "epsos-22")
                    || StringUtils.equals(eventLog.getEventType(), "epsos-31") || StringUtils.equals(eventLog.getEventType(), "epsos-94")
                    || StringUtils.equals(eventLog.getEventType(), "epsos-96") || StringUtils.equals(eventLog.getEventType(), "ITI-38")
                    || StringUtils.equals(eventLog.getEventType(), "ITI-39") || StringUtils.equals(eventLog.getEventType(), EventType.epsosPACRetrieve.getCode())) {
                model = AuditModel.EPSOS2_HCP_ASSURANCE_AUDIT.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-32")) {
                model = AuditModel.EPSOS2_FETCH_DOC_SERVICE_SC.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-41") || StringUtils.equals(eventLog.getEventType(), "epsos-51")) {
                model = AuditModel.EPSOS2_PROVIDE_DATA_SERVICE_SC.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-91")) {
                model = AuditModel.EPSOS2_ISSUANCE_HCP_ASSERTION.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-92")) {
                model = AuditModel.EPSOS2_ISSUANCE_TRC_ASSERTION.toString();
            }
            if (StringUtils.equals(eventLog.getEventType(), "epsos-93")) {
                model = AuditModel.EPSOS2_IMPORT_NCP_TRUSTED_LIST.toString();
            }
        }
        try {
            return AuditValidationService.getInstance().validateModel(convertAuditObjectToXML(am), model, ncpSide);
        } catch (JAXBException e) {
            LOGGER.error("JAXBException: {}", e.getMessage(), e);
            return false;
        }
        //return AuditValidationService.getInstance().validateModel(AuditTrailUtils.constructMessage(am, false), model, ncpSide);
    }
}
