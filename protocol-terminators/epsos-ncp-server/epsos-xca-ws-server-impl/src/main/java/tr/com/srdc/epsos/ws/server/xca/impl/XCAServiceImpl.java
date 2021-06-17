package tr.com.srdc.epsos.ws.server.xca.impl;

import epsos.ccd.gnomon.auditmanager.*;
import epsos.ccd.netsmart.securitymanager.exceptions.SMgrException;
import epsos.ccd.posam.tm.exception.TMError;
import epsos.ccd.posam.tm.response.TMResponseStructure;
import epsos.ccd.posam.tm.service.ITransformationService;
import epsos.ccd.posam.tsam.exception.ITMTSAMEror;
import eu.epsos.protocolterminators.ws.server.exception.NIException;
import eu.epsos.protocolterminators.ws.server.xca.DocumentSearchInterface;
import eu.epsos.protocolterminators.ws.server.xca.XCAServiceInterface;
import eu.epsos.util.EvidenceUtils;
import eu.epsos.util.IheConstants;
import eu.epsos.util.xca.XCAConstants;
import eu.epsos.util.xdr.XDRConstants;
import eu.epsos.validation.datamodel.common.NcpSide;
import eu.europa.ec.sante.ehdsi.gazelle.validation.OpenNCPValidation;
import eu.europa.ec.sante.ehdsi.openncp.assertionvalidator.Helper;
import eu.europa.ec.sante.ehdsi.openncp.assertionvalidator.exceptions.AssertionValidationException;
import eu.europa.ec.sante.ehdsi.openncp.assertionvalidator.exceptions.InsufficientRightsException;
import eu.europa.ec.sante.ehdsi.openncp.assertionvalidator.saml.SAML2Validator;
import eu.europa.ec.sante.ehdsi.openncp.pt.common.AdhocQueryResponseStatus;
import eu.europa.ec.sante.ehdsi.openncp.pt.common.RegistryErrorSeverity;
import eu.europa.ec.sante.ehdsi.openncp.util.OpenNCPConstants;
import eu.europa.ec.sante.ehdsi.openncp.util.ServerMode;
import eu.europa.ec.sante.ehdsi.openncp.util.UUIDHelper;
import fi.kela.se.epsos.data.model.*;
import fi.kela.se.epsos.data.model.SearchCriteria.Criteria;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
import org.apache.axiom.om.*;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tr.com.srdc.epsos.data.model.FilterParams;
import tr.com.srdc.epsos.data.model.xds.DocumentType;
import tr.com.srdc.epsos.util.Constants;
import tr.com.srdc.epsos.util.DateUtil;
import tr.com.srdc.epsos.util.XMLUtil;
import tr.com.srdc.epsos.util.http.HTTPUtil;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class XCAServiceImpl implements XCAServiceInterface {

    private final Logger logger = LoggerFactory.getLogger(XCAServiceImpl.class);
    private final Logger loggerClinical = LoggerFactory.getLogger("LOGGER_CLINICAL");
    private final ITransformationService transformationService;
    private final OMFactory omFactory;
    private final oasis.names.tc.ebxml_regrep.xsd.query._3.ObjectFactory ofQuery;
    private final oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory ofRim;
    private final oasis.names.tc.ebxml_regrep.xsd.rs._3.ObjectFactory ofRs;
    private final DocumentSearchInterface documentSearchService;

    /**
     * Public Constructor for IHE XCA Profile implementation, the default constructor will handle the loading of
     * the National Connector implementation by using the <class>ServiceLoader</class>
     *
     * @see ServiceLoader
     */
    public XCAServiceImpl() {

        ServiceLoader<DocumentSearchInterface> serviceLoader = ServiceLoader.load(DocumentSearchInterface.class);
        try {
            logger.info("Loading National implementation of DocumentSearchInterface...");
            documentSearchService = serviceLoader.iterator().next();
            logger.info("Successfully loaded documentSearchService");
        } catch (Exception e) {
            logger.error("Failed to load implementation of DocumentSearchInterface: " + e.getMessage(), e);
            throw e;
        }

        ofQuery = new oasis.names.tc.ebxml_regrep.xsd.query._3.ObjectFactory();
        ofRim = new oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory();
        ofRs = new oasis.names.tc.ebxml_regrep.xsd.rs._3.ObjectFactory();

        omFactory = OMAbstractFactory.getOMFactory();

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("ctx_tm.xml");
        transformationService = (ITransformationService) applicationContext.getBean(ITransformationService.class.getName());
    }

    /**
     * @param message
     * @return
     */
    private boolean isUUIDValid(String message) {
        try {
            UUID uuid = UUID.fromString(message);
            logger.debug("Valid UUID: '{}'", uuid);
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: '{}'", e.getMessage());
            return false;
        }
    }

    /**
     * @param eventLog
     * @param request
     * @param response
     * @param sh
     * @param classCode
     */
    private void prepareEventLogForQuery(EventLog eventLog, AdhocQueryRequest request, AdhocQueryResponse response,
                                         Element sh, String classCode) {

        logger.info("method prepareEventLogForQuery('{}')", request.getId());

        switch (classCode) {
            case Constants.EP_CLASSCODE:
                eventLog.setEventType(EventType.ORDER_SERVICE_LIST);
                eventLog.setEI_TransactionName(TransactionName.ORDER_SERVICE_LIST);
                break;
            case Constants.PS_CLASSCODE:
                eventLog.setEventType(EventType.PATIENT_SERVICE_LIST);
                eventLog.setEI_TransactionName(TransactionName.PATIENT_SERVICE_LIST);
                break;
            case Constants.MRO_CLASSCODE:
                eventLog.setEventType(EventType.MRO_LIST);
                eventLog.setEI_TransactionName(TransactionName.MRO_SERVICE_LIST);
                break;
            case Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_TITLE:
            case Constants.ORCD_LABORATORY_RESULTS_CLASSCODE:
            case Constants.ORCD_MEDICAL_IMAGING_REPORTS_TITLE:
            case Constants.ORCD_MEDICAL_IMAGES_CLASSCODE:
                eventLog.setEventType(EventType.ORCD_SERVICE_LIST);
                eventLog.setEI_TransactionName(TransactionName.ORCD_SERVICE_LIST);
                break;
        }
        eventLog.setEI_EventActionCode(EventActionCode.READ);
        try {
            eventLog.setEI_EventDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            logger.error("DataTypeConfigurationException: {}", e.getMessage(), e);
        }
        eventLog.setPS_PatricipantObjectID(getDocumentEntryPatientId(request));

        if (response.getRegistryObjectList() != null) {
            List<String> documentIds = new ArrayList<>();
            for (int i = 0; i < response.getRegistryObjectList().getIdentifiable().size(); i++) {
                if (!(response.getRegistryObjectList().getIdentifiable().get(i).getValue() instanceof ExtrinsicObjectType)) {
                    continue;
                }
                ExtrinsicObjectType eot = (ExtrinsicObjectType) response.getRegistryObjectList().getIdentifiable().get(i).getValue();
                for (ExternalIdentifierType eit : eot.getExternalIdentifier()) {
                    if (eit.getIdentificationScheme().equals(XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_SCHEME)) {
                        documentIds.add(eit.getValue());
                    }
                }
            }
            eventLog.setEventTargetParticipantObjectIds(documentIds);
        }

        // Set the operation status to the response
        handleEventLogStatus(eventLog, response, request);

        String userIdAlias = Helper.getAssertionsSPProvidedId(sh);
        eventLog.setHR_UserID(StringUtils.isNotBlank(userIdAlias) ? userIdAlias : "" + "<" + Helper.getUserID(sh)
                + "@" + Helper.getAssertionsIssuer(sh) + ">");
        eventLog.setHR_AlternativeUserID(Helper.getAlternateUserID(sh));
        eventLog.setHR_RoleID(Helper.getFunctionalRoleID(sh));
        eventLog.setSP_UserID(HTTPUtil.getSubjectDN(true));
        eventLog.setPT_PatricipantObjectID(getDocumentEntryPatientId(request));
        eventLog.setAS_AuditSourceId(Constants.COUNTRY_PRINCIPAL_SUBDIVISION);

        if (response.getRegistryErrorList() != null) {
            RegistryError re = response.getRegistryErrorList().getRegistryError().get(0);
            eventLog.setEM_PatricipantObjectID(re.getErrorCode());
            eventLog.setEM_PatricipantObjectDetail(re.getCodeContext().getBytes());
        }
    }

    private void handleEventLogStatus(EventLog eventLog, AdhocQueryResponse queryResponse, AdhocQueryRequest queryRequest) {

        if (queryResponse.getRegistryObjectList() == null) {
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.PERMANENT_FAILURE);
            // In case of failure, the document class code has been provided to the event log as event target as there is no
            // reference available as resources (document ID etc.).
            addDocumentClassCodeToEventLog(eventLog, queryRequest);
        } else if (queryResponse.getRegistryErrorList() == null) {
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.FULL_SUCCESS);
        } else {
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.TEMPORAL_FAILURE);
            // In case of failure, the document class code has been provided to the event log as event target as there is no
            // reference available as resources (document ID etc.).
            addDocumentClassCodeToEventLog(eventLog, queryRequest);
        }
    }

    private void addDocumentClassCodeToEventLog(EventLog eventLog, AdhocQueryRequest queryRequest) {

        for (SlotType1 slotType1 : queryRequest.getAdhocQuery().getSlot()) {
            if (StringUtils.equals(slotType1.getName(), "$XDSDocumentEntryClassCode")) {
                String documentType = slotType1.getValueList().getValue().get(0);
                documentType = org.apache.commons.lang3.StringUtils.remove(documentType, "('");
                documentType = org.apache.commons.lang3.StringUtils.remove(documentType, "')");
                eventLog.getEventTargetParticipantObjectIds().add(documentType);
            }
        }
    }

    /**
     * @param eventLog
     * @param request
     * @param errorsDiscovered
     * @param documentReturned
     * @param registryErrorList
     * @param sh
     * @param classCode
     */
    private void prepareEventLogForRetrieve(EventLog eventLog, RetrieveDocumentSetRequestType request, boolean errorsDiscovered,
                                            boolean documentReturned, OMElement registryErrorList, Element sh, String classCode) {

        logger.info("method prepareEventLogForRetrieve({})", classCode);
        if (classCode == null) {
            // In case the document is not found, audit log cannot be properly filled, as we don't know the event type
            // Log this under Order Service
            eventLog.setEventType(EventType.ORDER_SERVICE_RETRIEVE);
            eventLog.setEI_TransactionName(TransactionName.ORDER_SERVICE_RETRIEVE);
        } else {
            switch (classCode) {
                case Constants.EP_CLASSCODE:
                    eventLog.setEventType(EventType.ORDER_SERVICE_RETRIEVE);
                    eventLog.setEI_TransactionName(TransactionName.ORDER_SERVICE_RETRIEVE);
                    break;
                case Constants.PS_CLASSCODE:
                    eventLog.setEventType(EventType.PATIENT_SERVICE_RETRIEVE);
                    eventLog.setEI_TransactionName(TransactionName.PATIENT_SERVICE_RETRIEVE);
                    break;
                case Constants.MRO_CLASSCODE:
                    eventLog.setEventType(EventType.MRO_RETRIEVE);
                    eventLog.setEI_TransactionName(TransactionName.MRO_SERVICE_RETRIEVE);
                    break;
                case Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                case Constants.ORCD_LABORATORY_RESULTS_CLASSCODE:
                case Constants.ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                case Constants.ORCD_MEDICAL_IMAGES_CLASSCODE:
                    eventLog.setEventType(EventType.ORCD_SERVICE_RETRIEVE);
                    eventLog.setEI_TransactionName(TransactionName.ORCD_SERVICE_RETRIEVE);
                    break;
                default:
                    break;
            }
        }

        eventLog.setEI_EventActionCode(EventActionCode.READ);
        try {
            eventLog.setEI_EventDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            logger.error("DatatypeConfigurationException: {}", e.getMessage(), e);
        }

        eventLog.getEventTargetParticipantObjectIds().add(request.getDocumentRequest().get(0).getDocumentUniqueId());

        if (!documentReturned) {
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.PERMANENT_FAILURE);
        } else if (!errorsDiscovered) {
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.FULL_SUCCESS);
        } else {
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.TEMPORAL_FAILURE);
        }

        String userIdAlias = Helper.getAssertionsSPProvidedId(sh);
        eventLog.setHR_UserID(StringUtils.isNotBlank(userIdAlias) ? userIdAlias : "" + "<" + Helper.getUserID(sh)
                + "@" + Helper.getAssertionsIssuer(sh) + ">");
        eventLog.setHR_AlternativeUserID(Helper.getAlternateUserID(sh));
        eventLog.setHR_RoleID(Helper.getFunctionalRoleID(sh));
        eventLog.setSP_UserID(HTTPUtil.getSubjectDN(true));
        eventLog.setPT_PatricipantObjectID(Helper.getDocumentEntryPatientIdFromTRCAssertion(sh));
        eventLog.setAS_AuditSourceId(Constants.COUNTRY_PRINCIPAL_SUBDIVISION);

        if (errorsDiscovered) {
            Iterator<OMElement> re = registryErrorList.getChildElements();
            //Include only the first error in the audit log.
            if (re.hasNext()) {
                OMElement error = re.next();
                if (logger.isDebugEnabled()) {
                    try {
                        logger.debug("Error to be included in audit: '{}'", XMLUtil.prettyPrint(XMLUtils.toDOM(error)));
                    } catch (Exception e) {
                        logger.debug("Exception: '{}'", e.getMessage(), e);
                    }
                }
                eventLog.setEM_PatricipantObjectID(error.getAttributeValue(new QName("", "errorCode")));
                eventLog.setEM_PatricipantObjectDetail(error.getAttributeValue(new QName("", "codeContext")).getBytes());
            }
        }
    }

    /**
     * @param request
     * @return
     */
    private List<String> getDocumentEntryClassCodes(AdhocQueryRequest request) {
        List<String> classCodes = new ArrayList<>();
        for (SlotType1 sl : request.getAdhocQuery().getSlot()) {
            if (sl.getName().equals("$XDSDocumentEntryClassCode")) {
                String fullClassCodeString = sl.getValueList().getValue().get(0);
                String pattern = "\\(?\\)?\\'?";
                fullClassCodeString = fullClassCodeString.replaceAll(pattern, "");
                String classCodeString[] = fullClassCodeString.split(",");
                for (String classCode : classCodeString) {
                    classCode = classCode.substring(0, classCode.indexOf("^^"));
                    classCodes.add(classCode);
                }
            }
        }
        return classCodes;
    }

    /**
     * Extracts the XDS patient ID from the XCA query
     */
    private String getDocumentEntryPatientId(AdhocQueryRequest request) {

        for (SlotType1 sl : request.getAdhocQuery().getSlot()) {
            if (sl.getName().equals("$XDSDocumentEntryPatientId")) {
                String patientId = sl.getValueList().getValue().get(0);
                patientId = patientId.substring(1, patientId.length() - 1);
                return patientId;
            }
        }
        return "$XDSDocumentEntryPatientId Not Found!";
    }

    private FilterParams getFilterParams(AdhocQueryRequest request) {

        FilterParams filterParams = new FilterParams();

        //TODO fix the formatter Date
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");

        for (SlotType1 sl : request.getAdhocQuery().getSlot()) {
            if (sl.getName().equals(XCAConstants.AdHocQueryRequest.XDS_DOCUMENT_ENTRY_FILTERCREATEDAFTER_SLOT_NAME)) {
                try {
                    filterParams.setCreatedAfter(formatterDate.parse(sl.getValueList().getValue().get(0)));
                } catch (ParseException e) {
                    logger.warn("Could not parse CreatedAfter of filter parameters");
                }
            }
            if (sl.getName().equals(XCAConstants.AdHocQueryRequest.XDS_DOCUMENT_ENTRY_FILTERCREATEDBEFORE_SLOT_NAME)) {
                try {
                    filterParams.setCreatedBefore(formatterDate.parse(sl.getValueList().getValue().get(0)));
                } catch (ParseException e) {
                    logger.warn("Could not parse CreatedBefore of filter parameters");
                }
            }
            if (sl.getName().equals(XCAConstants.AdHocQueryRequest.XDS_DOCUMENT_ENTRY_FILTERMAXIMUMSIZE_SLOT_NAME)) {
                filterParams.setMaximumSize(Integer.parseInt(sl.getValueList().getValue().get(0)));
            }
        }
        return filterParams;
    }

    /**
     * Extracts the patient ID used in epSOS transactions from the XCA query
     */
    private String getEpSOSPatientId(AdhocQueryRequest request) {

        String docPatientId = getDocumentEntryPatientId(request);
        return trimDocumentEntryPatientId(docPatientId);
    }

    /**
     * Extracts repositoryUniqueId from request
     *
     * @return repositoryUniqueId
     */
    private String getRepositoryUniqueId(RetrieveDocumentSetRequestType request) {

        return request.getDocumentRequest().get(0).getRepositoryUniqueId();
    }

    /**
     * @param patientId
     * @return
     */
    private String trimDocumentEntryPatientId(String patientId) {

        if (patientId.contains("^^^")) {
            return patientId.substring(0, patientId.indexOf("^^^"));
        }
        return patientId;
    }

    /**
     * @param name
     * @param value
     * @return
     */
    private SlotType1 makeSlot(String name, String value) {

        SlotType1 sl = ofRim.createSlotType1();
        sl.setName(name);
        sl.setValueList(ofRim.createValueListType());
        sl.getValueList().getValue().add(value);
        return sl;
    }

    /**
     * @param classificationScheme
     * @param classifiedObject
     * @param nodeRepresentation
     * @param value
     * @param name
     * @return
     */
    private ClassificationType makeClassification(String classificationScheme, String classifiedObject, String nodeRepresentation, String value, String name) {

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        ClassificationType cl = ofRim.createClassificationType();
        cl.setId(uuid);
        cl.setNodeRepresentation(nodeRepresentation);
        cl.setClassificationScheme(classificationScheme);
        cl.setClassifiedObject(classifiedObject);
        cl.getSlot().add(makeSlot("codingScheme", value));

        cl.setName(ofRim.createInternationalStringType());
        cl.getName().getLocalizedString().add(ofRim.createLocalizedStringType());
        cl.getName().getLocalizedString().get(0).setValue(name);
        return cl;
    }

    /**
     * @param classificationScheme
     * @param classifiedObject
     * @param nodeRepresentation
     * @return
     */
    private ClassificationType makeClassification(String classificationScheme, String classifiedObject, String nodeRepresentation) {

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        ClassificationType cl = ofRim.createClassificationType();
        cl.setId(uuid);
        cl.setNodeRepresentation(nodeRepresentation);
        cl.setClassificationScheme(classificationScheme);
        cl.setClassifiedObject(classifiedObject);
        return cl;
    }

    /**
     * @param identificationScheme
     * @param registryObject
     * @param value
     * @param name
     * @return
     */
    private ExternalIdentifierType makeExternalIdentifier(String identificationScheme, String registryObject, String value, String name) {

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        ExternalIdentifierType ex = ofRim.createExternalIdentifierType();
        ex.setId(uuid);
        ex.setIdentificationScheme(identificationScheme);
        ex.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier");
        ex.setRegistryObject(registryObject);
        ex.setValue(value);

        ex.setName(ofRim.createInternationalStringType());
        ex.getName().getLocalizedString().add(ofRim.createLocalizedStringType());
        ex.getName().getLocalizedString().get(0).setValue(name);
        return ex;
    }

    /**
     * @param docType
     * @param effectiveTime
     * @param repositoryId
     * @param request
     * @param eot
     * @param isPDF
     * @param documentId
     * @return
     */
    private String prepareExtrinsicObjectEpsosDoc(DocumentType docType, Date effectiveTime, String repositoryId,
                                                  AdhocQueryRequest request, ExtrinsicObjectType eot, boolean isPDF,
                                                  String documentId, String confidentialityCode, String confidentialityDisplay, String languageCode) {

        final String title;
        final String classCode;
        final String nodeRepresentation;
        final String displayName;

        switch (docType) {

            case PATIENT_SUMMARY:
                title = Constants.PS_TITLE;
                classCode = Constants.PS_CLASSCODE;
                nodeRepresentation = XCAConstants.EXTRINSIC_OBJECT.FormatCode.PatientSummary.EpsosPivotCoded.NODE_REPRESENTATION;
                displayName = XCAConstants.EXTRINSIC_OBJECT.FormatCode.PatientSummary.EpsosPivotCoded.DISPLAY_NAME;
                break;
            case MRO:
                title = Constants.MRO_TITLE;
                classCode = Constants.MRO_CLASSCODE;
                nodeRepresentation = XCAConstants.EXTRINSIC_OBJECT.FormatCode.Mro.EpsosPivotCoded.NODE_REPRESENTATION;
                displayName = XCAConstants.EXTRINSIC_OBJECT.FormatCode.Mro.EpsosPivotCoded.DISPLAY_NAME;
                break;
            default:
                logger.error("Unsupported document for query in OpenNCP. Requested document type: {}", docType.name());
                return "";
        }

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        // Set Extrinsic Object
        eot.setStatus(IheConstants.REGREP_STATUSTYPE_APPROVED);
        eot.setHome(Constants.OID_PREFIX + Constants.HOME_COMM_ID);
        eot.setId(uuid);
        eot.setLid(uuid);
        eot.setObjectType(XCAConstants.XDS_DOC_ENTRY_CLASSIFICATION_NODE);

        // Status
        eot.setMimeType(MediaType.TEXT_XML_VALUE);

        // Name
        eot.setName(ofRim.createInternationalStringType());
        eot.getName().getLocalizedString().add(ofRim.createLocalizedStringType());
        eot.getName().getLocalizedString().get(0).setValue(title);

        // Description (optional)
        eot.setDescription(ofRim.createInternationalStringType());
        eot.getDescription().getLocalizedString().add(ofRim.createLocalizedStringType());
        if (isPDF) {
            eot.getDescription().getLocalizedString().get(0)
                    .setValue("The " + title + " document (CDA L1 / PDF body) for patient " + getEpSOSPatientId(request));
        } else {
            eot.getDescription().getLocalizedString().get(0)
                    .setValue("The " + title + " document (CDA L3 / Structured body) for patient " + getEpSOSPatientId(request));
        }

        // Version Info
        eot.setVersionInfo(ofRim.createVersionInfoType());
        eot.getVersionInfo().setVersionName("1.1");

        // Creation Date (optional)
        eot.getSlot().add(makeSlot("creationTime", DateUtil.getDateByDateFormat("yyyyMMddHHmmss", effectiveTime)));

        // Source Patient Id
        eot.getSlot().add(makeSlot("sourcePatientId", getDocumentEntryPatientId(request)));

        // LanguageCode (optional)
        eot.getSlot().add(makeSlot("languageCode", languageCode == null ? Constants.LANGUAGE_CODE : languageCode));

        // repositoryUniqueId (optional)
        eot.getSlot().add(makeSlot("repositoryUniqueId", repositoryId));

        eot.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_SCHEME,
                uuid, classCode, "2.16.840.1.113883.6.1", title));
        // Type code (not written in 3.4.2)
        eot.getClassification().add(makeClassification("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",
                uuid, classCode, "2.16.840.1.113883.6.1", title));
        // Confidentiality Code
        eot.getClassification().add(makeClassification("urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",
                uuid, confidentialityCode, "2.16.840.1.113883.5.25", confidentialityDisplay));
        // FormatCode
        if (isPDF) {
            eot.getClassification().add(makeClassification("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
                    uuid, "urn:ihe:iti:xds-sd:pdf:2008", "IHE PCC", "PDF/A coded document"));
        } else {
            eot.getClassification().add(makeClassification("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
                    uuid, nodeRepresentation, "eHDSI formatCodes", displayName));
        }

        /*
         * Healthcare facility code
         * TODO: Get healthcare facility info from national implementation
         */
        eot.getClassification().add(makeClassification("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",
                uuid, Constants.COUNTRY_CODE, "1.0.3166.1", Constants.COUNTRY_NAME));

        // Practice Setting code
        eot.getClassification().add(makeClassification("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
                uuid, "Not Used", "eHDSI Practice Setting Codes-Not Used", "Not Used"));

        // External Identifiers
        eot.getExternalIdentifier().add(makeExternalIdentifier("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",
                uuid, getDocumentEntryPatientId(request), "XDSDocumentEntry.patientId"));

        eot.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_SCHEME,
                uuid, documentId, XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_STR));

        return uuid;
    }

    /**
     * @param docType
     * @param effectiveTime
     * @param repositoryId
     * @param request
     * @param eot
     * @param documentId
     * @return
     */
    private String prepareExtrinsicObjectOrCD(DocumentType docType,
                                              Date effectiveTime,
                                              String repositoryId,
                                              AdhocQueryRequest request,
                                              ExtrinsicObjectType eot,
                                              String documentId,
                                              String confidentialityCode,
                                              String confidentialityDisplay,
                                              String languageCode,
                                              String classCode,
                                              OrCDDocumentMetaData.DocumentFileType documentFileType,
                                              long size) {

        final String title;
        final String nodeRepresentation;
        final String displayName;

        switch (classCode) {
            case Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                title = Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_TITLE;
                nodeRepresentation = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PdfSourceCoded.NODE_REPRESENTATION;
                displayName = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PdfSourceCoded.DISPLAY_NAME;
                break;
            case Constants.ORCD_LABORATORY_RESULTS_CLASSCODE:
                title = Constants.ORCD_LABORATORY_RESULTS_TITLE;
                nodeRepresentation = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PdfSourceCoded.NODE_REPRESENTATION;
                displayName = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PdfSourceCoded.DISPLAY_NAME;
                break;
            case Constants.ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                title = Constants.ORCD_MEDICAL_IMAGING_REPORTS_TITLE;
                nodeRepresentation = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PdfSourceCoded.NODE_REPRESENTATION;
                displayName = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PdfSourceCoded.DISPLAY_NAME;
                break;
            case Constants.ORCD_MEDICAL_IMAGES_CLASSCODE:
                title = Constants.ORCD_MEDICAL_IMAGES_TITLE;
                switch (documentFileType) {
                    case PNG:
                        nodeRepresentation = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PngSourceCoded.NODE_REPRESENTATION;
                        displayName = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.PngSourceCoded.DISPLAY_NAME;
                        break;
                    case JPEG:
                        nodeRepresentation = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.JpegSourceCoded.NODE_REPRESENTATION;
                        displayName = XCAConstants.EXTRINSIC_OBJECT.FormatCode.OrCD.JpegSourceCoded.DISPLAY_NAME;
                        break;
                    default:
                        logger.error("Unsupported document file type '{}' for OrCD Medical Images", documentFileType);
                        return "";
                }
                break;
            default:
                logger.error("Unsupported classCode for OrCD query in OpenNCP. Requested classCode: {}", classCode);
                return "";

        }

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        // Set Extrinsic Object
        eot.setStatus(IheConstants.REGREP_STATUSTYPE_APPROVED);
        eot.setHome(Constants.OID_PREFIX + Constants.HOME_COMM_ID);
        eot.setId(uuid);
        eot.setLid(uuid);
        eot.setObjectType(XCAConstants.XDS_DOC_ENTRY_CLASSIFICATION_NODE);

        // MimeType
        eot.setMimeType(MediaType.TEXT_XML_VALUE);

        // Name
        eot.setName(ofRim.createInternationalStringType());
        eot.getName().getLocalizedString().add(ofRim.createLocalizedStringType());
        eot.getName().getLocalizedString().get(0).setValue(title);

        // Description (optional)
        eot.setDescription(ofRim.createInternationalStringType());
        eot.getDescription().getLocalizedString().add(ofRim.createLocalizedStringType());

        // Version Info
        eot.setVersionInfo(ofRim.createVersionInfoType());
        eot.getVersionInfo().setVersionName("1.1");

        // Creation Date (optional)
        eot.getSlot().add(makeSlot("creationTime", DateUtil.getDateByDateFormat("yyyyMMddHHmmss", effectiveTime)));

        // Source Patient Id
        eot.getSlot().add(makeSlot("sourcePatientId", getDocumentEntryPatientId(request)));

        // Size
        eot.getSlot().add(makeSlot("size", String.valueOf(size)));

        // LanguageCode (optional)
        eot.getSlot().add(makeSlot("languageCode", languageCode == null ? Constants.LANGUAGE_CODE : languageCode));

        // repositoryUniqueId (optional)
        eot.getSlot().add(makeSlot("repositoryUniqueId", repositoryId));

        eot.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_SCHEME,
                uuid, classCode, "2.16.840.1.113883.6.1", title));
        // Type code (not written in 3.4.2)
        eot.getClassification().add(makeClassification("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",
                uuid, classCode, "2.16.840.1.113883.6.1", title));
        // Confidentiality Code
        eot.getClassification().add(makeClassification("urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",
                uuid, confidentialityCode, "2.16.840.1.113883.5.25", confidentialityDisplay));
        // FormatCode
        eot.getClassification().add(makeClassification("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
                uuid, nodeRepresentation, "eHDSI formatCodes", displayName));

        /*
         * Healthcare facility code
         * TODO: Get healthcare facility info from national implementation
         */
        eot.getClassification().add(makeClassification("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",
                uuid, Constants.COUNTRY_CODE, "1.0.3166.1", Constants.COUNTRY_NAME));

        // Practice Setting code
        eot.getClassification().add(makeClassification("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
                uuid, "Not Used", "eHDSI Practice Setting Codes-Not Used", "Not Used"));

        // External Identifiers
        eot.getExternalIdentifier().add(makeExternalIdentifier("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",
                uuid, getDocumentEntryPatientId(request), "XDSDocumentEntry.patientId"));

        eot.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_SCHEME,
                uuid, documentId, XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_STR));

        return uuid;
    }

    /**
     * @param request
     * @param eot
     * @param document
     * @return
     */
    private String prepareExtrinsicObjectEP(AdhocQueryRequest request, ExtrinsicObjectType eot, EPDocumentMetaData document) {

        String name = "eHDSI - ePrescription";
        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        boolean isPDF = document.getFormat() == EPSOSDocumentMetaData.EPSOSDOCUMENT_FORMAT_PDF;

        // Set Extrinsic Object
        eot.setStatus(IheConstants.REGREP_STATUSTYPE_APPROVED);
        eot.setHome(Constants.OID_PREFIX + Constants.HOME_COMM_ID);
        eot.setId(uuid);
        eot.setLid(uuid);
        eot.setObjectType(XCAConstants.XDS_DOC_ENTRY_CLASSIFICATION_NODE);

        // Status
        eot.setMimeType(MediaType.TEXT_XML_VALUE);

        // Name
        eot.setName(ofRim.createInternationalStringType());
        eot.getName().getLocalizedString().add(ofRim.createLocalizedStringType());
        eot.getName().getLocalizedString().get(0).setValue(name);

        // Description
        eot.setDescription(ofRim.createInternationalStringType());
        eot.getDescription().getLocalizedString().add(ofRim.createLocalizedStringType());
        eot.getDescription().getLocalizedString().get(0).setValue(document.getDescription());

        // Version Info
        eot.setVersionInfo(ofRim.createVersionInfoType());
        eot.getVersionInfo().setVersionName("1");

        // Creation Date (optional)
        eot.getSlot().add(makeSlot("creationTime", DateUtil.getDateByDateFormat("yyyyMMddHHmmss", document.getEffectiveTime())));

        // Source Patient Id
        eot.getSlot().add(makeSlot("sourcePatientId", getDocumentEntryPatientId(request)));

        // LanguageCode (optional)
        String languageCode = document.getLanguage() == null ? Constants.LANGUAGE_CODE : document.getLanguage();
        eot.getSlot().add(makeSlot("languageCode", languageCode));

        // repositoryUniqueId (optional)
        eot.getSlot().add(makeSlot("repositoryUniqueId", document.getRepositoryId()));

        eot.getClassification().add(
                makeClassification(
                        XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_SCHEME, uuid,
                        Constants.EP_CLASSCODE, "2.16.840.1.113883.6.1", name));
        // Type code (not written in 3.4.2)
        eot.getClassification().add(makeClassification("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",
                uuid, Constants.EP_CLASSCODE, "2.16.840.1.113883.6.1", name));

        // Product
        if (document.hasProduct()) {
            EPDocumentMetaData.ProductMetadata product = document.getProduct();
            eot.getClassification()
                    .add(makeClassification("urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4", uuid,
                            product.getProductCode(), "2.16.840.1.113883.6.73", product.getProductName()));
        }

        // Dispensable
        if (document.isDispensable()) {
            eot.getClassification()
                    .add(makeClassification("urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4",
                            uuid, "urn:ihe:iti:xdw:2011:eventCode:open", "1.3.6.1.4.1.19376.1.2.3", "Open"));
        } else {
            eot.getClassification()
                    .add(makeClassification("urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4",
                            uuid, "urn:ihe:iti:xdw:2011:eventCode:closed", "1.3.6.1.4.1.19376.1.2.3", "Closed"));
        }

        // Confidentiality Code
        String confidentialityCode = document.getConfidentiality() != null
                && document.getConfidentiality().getConfidentialityCode() != null
                ? document.getConfidentiality().getConfidentialityCode()
                : "N";
        String confidentialityDisplay = document.getConfidentiality() != null
                && document.getConfidentiality().getConfidentialityDisplay() != null
                ? document.getConfidentiality().getConfidentialityDisplay()
                : "Normal";
        eot.getClassification().add(makeClassification("urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",
                uuid, confidentialityCode, "2.16.840.1.113883.5.25", confidentialityDisplay));
        // FormatCode
        if (isPDF) {
            eot.getClassification().add(makeClassification("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
                    uuid, "urn:ihe:iti:xds-sd:pdf:2008", "IHE PCC", "PDF/A coded document"));
        } else {
            eot.getClassification().add(makeClassification("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
                    uuid, "urn:epSOS:ep:pre:2010", "eHDSI formatCodes", "epSOS coded ePrescription"));
        }
        // Healthcare facility code
        // TODO: Get healthcare facility info from national implementation

        eot.getClassification().add(makeClassification("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",
                uuid, Constants.COUNTRY_CODE, "1.0.3166.1", Constants.COUNTRY_NAME));
        // Practice Setting code
        eot.getClassification().add(makeClassification("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
                uuid, "Not Used", "eHDSI Practice Setting Codes-Not Used", "Not Used"));

        // Author Person
        ClassificationType authorClassification = makeClassification(
                "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d", uuid, "");
        authorClassification.getSlot().add(makeSlot("authorPerson", document.getAuthor()));
        eot.getClassification().add(authorClassification);

        // External Identifiers
        eot.getExternalIdentifier().add(makeExternalIdentifier("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",
                uuid, getDocumentEntryPatientId(request), "XDSDocumentEntry.patientId"));

        eot.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_SCHEME,
                uuid, document.getId(), XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_STR));

        return uuid;
    }

    /**
     * @param source
     * @param target
     * @return
     */
    private AssociationType1 makeAssociation(String source, String target) {

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        AssociationType1 association = ofRim.createAssociationType1();
        association.setId(uuid);
        association.setAssociationType("urn:ihe:iti:2007:AssociationType:XFRM");
        association.setSourceObject(source);
        association.setTargetObject(target);
        /*
         * Gazelle does not like this information when validating. Uncomment if
         * really needed.
         */
        //        association.getClassification().add(makeClassification(
        //                "urn:uuid:abd807a3-4432-4053-87b4-fd82c643d1f3",
        //                uuid,
        //                "epSOS pivot",
        //                "epSOS translation types",
        //                "Translation into epSOS pivot format"));
        return association;
    }

    /**
     * @return
     */
    private String getLocation() {

        //String location = ConfigurationManagerFactory.getConfigurationManager().getEndpointUrl(Constants.COUNTRY_CODE.toLowerCase(Locale.ENGLISH),
        //              RegisteredService.PATIENT_SERVICE);
        // EHNCP-1131
        return Constants.OID_PREFIX + Constants.HOME_COMM_ID;
    }

    /**
     * @param errorCode
     * @param codeContext
     * @param value
     * @param isWarning
     * @return
     */
    private RegistryError createErrorMessage(String errorCode, String codeContext, String value, boolean isWarning) {

        RegistryError re = ofRs.createRegistryError();
        re.setErrorCode(errorCode);
        re.setLocation(getLocation());
        re.setSeverity("urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:" + (isWarning ? "Warning" : "Error"));
        re.setCodeContext(codeContext);
        re.setValue(value);
        return re;
    }

    /**
     * @param ons
     * @param errorCode
     * @param codeContext
     * @param value
     * @param isWarning
     * @return
     */
    private OMElement createErrorOMMessage(OMNamespace ons, String errorCode, String codeContext, String value, boolean isWarning) {

        OMElement re = omFactory.createOMElement("RegistryError", ons);
        re.addAttribute(omFactory.createOMAttribute("codeContext", null, codeContext));
        re.addAttribute(omFactory.createOMAttribute("errorCode", null, errorCode));
        String aux = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:" + (isWarning ? "Warning" : "Error");
        re.addAttribute(omFactory.createOMAttribute("severity", null, aux));
        // EHNCP-1131
        re.addAttribute(omFactory.createOMAttribute("location", null, getLocation()));
        re.setText(value);

        return re;
    }

    /**
     * Main part of the XCA query operation implementation, fills the AdhocQueryResponse with details
     */
    private void adhocQueryResponseBuilder(AdhocQueryRequest request, AdhocQueryResponse response, SOAPHeader sh,
                                           EventLog eventLog) throws Exception {

        String sigCountryCode = null;
        Element shElement = null;
        // What's being requested: eP or PS?
        List<String> classCodeValues = getDocumentEntryClassCodes(request);
        RegistryErrorList rel = ofRs.createRegistryErrorList();
        // Create Registry Object List
        response.setRegistryObjectList(ofRim.createRegistryObjectListType());

        try {
            shElement = XMLUtils.toDOM(sh);
            documentSearchService.setSOAPHeader(shElement);
            sigCountryCode = SAML2Validator.validateXCAHeader(shElement, classCodeValues.get(0));
        } catch (InsufficientRightsException e) {
            logger.debug(e.getMessage(), e);
            rel.getRegistryError().add(createErrorMessage(e.getCode(), e.getMessage(), "", false));
        } catch (AssertionValidationException e) {
            logger.debug(e.getMessage(), e);
            rel.getRegistryError().add(createErrorMessage(e.getCode(), e.getMessage(), "", false));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            rel.getRegistryError().add(createErrorMessage("", e.getMessage(), "", false));
            throw e;
        }

        String fullPatientId = trimDocumentEntryPatientId(Helper.getDocumentEntryPatientIdFromTRCAssertion(shElement));
        if (!getDocumentEntryPatientId(request).contains(fullPatientId)) {
            // Patient ID in TRC assertion does not match the one given in the request. Return "No documents found".
            if (classCodeValues.contains(Constants.EP_CLASSCODE)) {
                rel.getRegistryError().add(createErrorMessage("1101", "No ePrescriptions are registered for the given patient.", "", true));
            } else if (classCodeValues.contains(Constants.PS_CLASSCODE)) {
                rel.getRegistryError().add(createErrorMessage("1102", "No patient summary is registered for the given patient.", "", true));
            } else if (classCodeValues.contains(Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE)
                    || classCodeValues.contains(Constants.ORCD_LABORATORY_RESULTS_CLASSCODE)
                    || classCodeValues.contains(Constants.ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE)
                    || classCodeValues.contains(Constants.ORCD_MEDICAL_IMAGES_CLASSCODE)) {
                rel.getRegistryError().add(createErrorMessage("1104", "There is no original clinical data of the requested type registered for the given patient.", "", true));
            } else {
                rel.getRegistryError().add(createErrorMessage("1100", "No documents are registered for the given patient.", "", true));
            }
        }
        String patientId = trimDocumentEntryPatientId(fullPatientId);
        String countryCode = "";
        String DN = eventLog.getSC_UserID();
        int cIndex = DN.indexOf("C=");

        if (cIndex > 0) {
            countryCode = DN.substring(cIndex + 2, cIndex + 4);
        } // Mustafa: This part is added for handling consents when the call is not https
        // In this case, we check the country code of the signature certificate that
        // ships within the HCP assertion
        // TODO: Might be necessary to remove later, although it does no harm in reality!
        else {
            logger.info("Could not get client country code from the service consumer certificate. " +
                    "The reason can be that the call was not via HTTPS. " +
                    "Will check the country code from the signature certificate now.");
            if (sigCountryCode != null) {
                logger.info("Found the client country code via the signature certificate.");
                countryCode = sigCountryCode;
            }
        }
        logger.info("The client country code to be used by the PDP: '{}'", countryCode);

        // Then, it is the Policy Decision Point (PDP) that decides according to the consent of the patient
        if (!SAML2Validator.isConsentGiven(patientId, countryCode)) {
            InsufficientRightsException e = new InsufficientRightsException(4701);
            rel.getRegistryError().add(createErrorMessage(e.getCode(), e.getMessage(), "", false));
        }

        if (classCodeValues == null || classCodeValues.isEmpty()) {
            rel.getRegistryError().add(createErrorMessage("4202", "Class code missing in XCA query request.", "", false));
        }

        // Evidence for call to NI for XCA List
        try {
            //  e-Sens: we MUST generate NRO when NCPA sends to NI. This was throwing errors because we were not
            //  passing a XML document. We're passing data like:"SearchCriteria: {patientId = 12445ASD}".
            //  So we provided a XML representation of such data.
            Assertion assertionTRC = Helper.getTRCAssertion(shElement);
            String messageUUID = UUIDHelper.encodeAsURN(assertionTRC.getID()) + "_" + assertionTRC.getIssueInstant();

            EvidenceUtils.createEvidenceREMNRO(DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId).asXml(),
                    Constants.NCP_SIG_KEYSTORE_PATH, Constants.NCP_SIG_KEYSTORE_PASSWORD, Constants.NCP_SIG_PRIVATEKEY_ALIAS,
                    Constants.SP_KEYSTORE_PATH, Constants.SP_KEYSTORE_PASSWORD, Constants.SP_PRIVATEKEY_ALIAS,
                    Constants.NCP_SIG_KEYSTORE_PATH, Constants.NCP_SIG_KEYSTORE_PASSWORD, Constants.NCP_SIG_PRIVATEKEY_ALIAS,
                    IHEEventType.PATIENT_SERVICE_LIST.getCode(), new DateTime(), EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
                    "NI_XCA_LIST_REQ", messageUUID);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        // Handling of the Response Status message and Errors.
        if (!rel.getRegistryError().isEmpty()) {
            response.setRegistryErrorList(rel);
            response.setStatus(AdhocQueryResponseStatus.FAILURE);

        } else {

        }

        for (String classCodeValue: classCodeValues) {
            switch (classCodeValue) {
                case Constants.EP_CLASSCODE:
                    List<DocumentAssociation<EPDocumentMetaData>> prescriptions = documentSearchService.getEPDocumentList(
                            DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId));

                    if (prescriptions == null) {

                        rel.getRegistryError().add(createErrorMessage("4103", "ePrescription registry could not be accessed.", "", true));
                        response.setRegistryErrorList(rel);
                        response.setStatus(AdhocQueryResponseStatus.FAILURE);
                    } else if (prescriptions.isEmpty()) {

                        rel.getRegistryError().add(createErrorMessage("1101", "No ePrescriptions are registered for the given patient.", "", true));
                        response.setRegistryErrorList(rel);
                        response.setStatus(AdhocQueryResponseStatus.SUCCESS);
                    } else {

                        // Multiple prescriptions mean multiple PDF and XML files, multiple ExtrinsicObjects and associations
                        response.setStatus(AdhocQueryResponseStatus.SUCCESS);
                        for (DocumentAssociation<EPDocumentMetaData> prescription : prescriptions) {

                            logger.debug("Prescription Repository ID: '{}'", prescription.getXMLDocumentMetaData().getRepositoryId());
                            String xmlUUID;
                            ExtrinsicObjectType eotXML = ofRim.createExtrinsicObjectType();
                            xmlUUID = prepareExtrinsicObjectEP(request, eotXML, prescription.getXMLDocumentMetaData());
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createExtrinsicObject(eotXML));

                            String pdfUUID;
                            ExtrinsicObjectType eotPDF = ofRim.createExtrinsicObjectType();
                            pdfUUID = prepareExtrinsicObjectEP(request, eotPDF, prescription.getPDFDocumentMetaData());
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createExtrinsicObject(eotPDF));

                            if (StringUtils.isNotBlank(xmlUUID) && StringUtils.isNotBlank(pdfUUID)) {

                                response.getRegistryObjectList().getIdentifiable().add(ofRim.createAssociation(makeAssociation(pdfUUID, xmlUUID)));
                            }
                        }
                    }
                    break;
                case Constants.PS_CLASSCODE:
                    DocumentAssociation<PSDocumentMetaData> psDoc = documentSearchService.getPSDocumentList(DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId));

                    if (psDoc == null || (psDoc.getPDFDocumentMetaData() == null && psDoc.getXMLDocumentMetaData() == null)) {

                        rel.getRegistryError().add(createErrorMessage("1102", "No patient summary is registered for the given patient.", "", true));
                        response.setRegistryErrorList(rel);
                        response.setStatus(AdhocQueryResponseStatus.SUCCESS);
                    } else {

                        PSDocumentMetaData docPdf = psDoc.getPDFDocumentMetaData();
                        PSDocumentMetaData docXml = psDoc.getXMLDocumentMetaData();
                        response.setStatus(AdhocQueryResponseStatus.SUCCESS);

                        String xmlUUID = "";
                        if (docXml != null) {
                            ExtrinsicObjectType eotXML = ofRim.createExtrinsicObjectType();
                            String confidentialityCode = docXml.getConfidentiality() == null
                                    || docXml.getConfidentiality().getConfidentialityCode() == null ? "N"
                                    : docXml.getConfidentiality().getConfidentialityCode();
                            String confidentialityDisplay = docXml.getConfidentiality() == null
                                    || docXml.getConfidentiality().getConfidentialityDisplay() == null ? "Normal"
                                    : docXml.getConfidentiality().getConfidentialityDisplay();
                            String languageCode = docXml.getLanguage();
                            xmlUUID = prepareExtrinsicObjectEpsosDoc(DocumentType.PATIENT_SUMMARY,
                                    docXml.getEffectiveTime(), docXml.getRepositoryId(), request, eotXML, false,
                                    docXml.getId(), confidentialityCode, confidentialityDisplay, languageCode);
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createExtrinsicObject(eotXML));
                        }
                        String pdfUUID = "";
                        if (docPdf != null) {
                            ExtrinsicObjectType eotPDF = ofRim.createExtrinsicObjectType();
                            String confidentialityCode = docPdf.getConfidentiality() == null
                                    || docPdf.getConfidentiality().getConfidentialityCode() == null ? "N"
                                    : docPdf.getConfidentiality().getConfidentialityCode();
                            String confidentialityDisplay = docPdf.getConfidentiality() == null
                                    || docPdf.getConfidentiality().getConfidentialityDisplay() == null ? "Normal"
                                    : docPdf.getConfidentiality().getConfidentialityDisplay();
                            String languageCode = docPdf.getLanguage();
                            pdfUUID = prepareExtrinsicObjectEpsosDoc(DocumentType.PATIENT_SUMMARY, docPdf.getEffectiveTime(), docPdf.getRepositoryId(), request, eotPDF, true,
                                    docPdf.getId(), confidentialityCode, confidentialityDisplay, languageCode);
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createExtrinsicObject(eotPDF));
                        }
                        if (!xmlUUID.equals("") && !pdfUUID.equals("")) {
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createAssociation(makeAssociation(pdfUUID, xmlUUID)));
                        }
                    }
                    break;
                case Constants.MRO_CLASSCODE:
                    DocumentAssociation<MroDocumentMetaData> mro = documentSearchService.getMroDocumentList(DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId));

                    if (mro == null || (mro.getPDFDocumentMetaData() == null && mro.getXMLDocumentMetaData() == null)) {

                        rel.getRegistryError().add(createErrorMessage("1100", "No MRO summary is registered for the given patient.", "", true));
                        response.setRegistryErrorList(rel);
                        response.setStatus(AdhocQueryResponseStatus.SUCCESS);
                    } else {

                        MroDocumentMetaData docPdf = mro.getPDFDocumentMetaData();
                        MroDocumentMetaData docXml = mro.getXMLDocumentMetaData();

                        response.setStatus(AdhocQueryResponseStatus.SUCCESS);

                        String xmlUUID = "";
                        if (docXml != null) {
                            ExtrinsicObjectType eotXML = ofRim.createExtrinsicObjectType();
                            String confidentialityCode = docXml.getConfidentiality() == null
                                    || docXml.getConfidentiality().getConfidentialityCode() == null ? "N"
                                    : docXml.getConfidentiality().getConfidentialityCode();
                            String confidentialityDisplay = docXml.getConfidentiality() == null
                                    || docXml.getConfidentiality().getConfidentialityDisplay() == null ? "Normal"
                                    : docXml.getConfidentiality().getConfidentialityDisplay();
                            String languageCode = docXml.getLanguage();
                            xmlUUID = prepareExtrinsicObjectEpsosDoc(DocumentType.MRO, docXml.getEffectiveTime(),
                                    docXml.getRepositoryId(), request, eotXML, false, docXml.getId(), confidentialityCode, confidentialityDisplay, languageCode);
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createExtrinsicObject(eotXML));
                        }
                        String pdfUUID = "";
                        if (docPdf != null) {
                            ExtrinsicObjectType eotPDF = ofRim.createExtrinsicObjectType();
                            String confidentialityCode = docPdf.getConfidentiality() == null
                                    || docPdf.getConfidentiality().getConfidentialityCode() == null ? "N"
                                    : docPdf.getConfidentiality().getConfidentialityCode();
                            String confidentialityDisplay = docPdf.getConfidentiality() == null
                                    || docPdf.getConfidentiality().getConfidentialityDisplay() == null ? "Normal"
                                    : docPdf.getConfidentiality().getConfidentialityDisplay();
                            String languageCode = docPdf.getLanguage();
                            pdfUUID = prepareExtrinsicObjectEpsosDoc(DocumentType.MRO, docPdf.getEffectiveTime(),
                                    docPdf.getRepositoryId(), request, eotPDF, true, docPdf.getId(), confidentialityCode, confidentialityDisplay, languageCode);
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createExtrinsicObject(eotPDF));
                        }
                        if (!xmlUUID.equals("") && !pdfUUID.equals("")) {
                            response.getRegistryObjectList().getIdentifiable().add(ofRim.createAssociation(makeAssociation(pdfUUID, xmlUUID)));
                        }
                    }
                    break;
                case Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                case Constants.ORCD_LABORATORY_RESULTS_CLASSCODE:
                case Constants.ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                case Constants.ORCD_MEDICAL_IMAGES_CLASSCODE:
                    SearchCriteria searchCriteria = DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId);
                    FilterParams filterParams = getFilterParams(request);
                    if(filterParams.getMaximumSize() != null){
                        searchCriteria.add(Criteria.MaximumSize, filterParams.getMaximumSize().toString());
                    }
                    if(filterParams.getCreatedBefore() != null){
                        searchCriteria.add(Criteria.CreatedBefore, filterParams.getCreatedBefore().toString());
                    }
                    if(filterParams.getCreatedAfter() != null){
                        searchCriteria.add(Criteria.CreatedAfter, filterParams.getCreatedAfter().toString());
                    }

                    List<OrCDDocumentMetaData> orCDDocumentMetaDataList = getOrCDDocumentMetaDataList(classCodeValue, searchCriteria);

                    if (orCDDocumentMetaDataList == null || orCDDocumentMetaDataList.isEmpty()) {
                        response = handleOrCDExceptionCases(response, rel, orCDDocumentMetaDataList);
                    } else {

                        response.setStatus(AdhocQueryResponseStatus.SUCCESS);
                        for (OrCDDocumentMetaData orCDDocumentMetaData : orCDDocumentMetaDataList) {
                            logger.debug("OrCD Document Repository ID: '{}'", orCDDocumentMetaData.getRepositoryId());
                            buildOrCDExtrinsicObject(request, response, orCDDocumentMetaData);
                        }
                    }
                    break;

                default:
                    rel.getRegistryError().add(createErrorMessage("4202", "Class code not supported for XCA query(" + classCodeValue + ").", "", false));
                    response.setRegistryErrorList(rel);
                    response.setStatus(AdhocQueryResponseStatus.FAILURE);
                    break;

            }

            try {
                prepareEventLogForQuery(eventLog, request, response, shElement, classCodeValue);
            } catch (Exception e) {
                logger.error("Prepare Audit log failed: '{}'", e.getMessage(), e);
                // Is this fatal?
            }
        }
    }

    private List<OrCDDocumentMetaData> getOrCDDocumentMetaDataList(String classCode, SearchCriteria searchCriteria) throws NIException, InsufficientRightsException {
        List<OrCDDocumentMetaData> orCDDocumentMetaDataList = new ArrayList<>();
        switch (classCode) {
            case Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                orCDDocumentMetaDataList = documentSearchService.getOrCDHospitalDischargeReportsDocumentList(searchCriteria);
                break;
            case Constants.ORCD_LABORATORY_RESULTS_CLASSCODE:
                orCDDocumentMetaDataList = documentSearchService.getOrCDLaboratoryResultsDocumentList(searchCriteria);
                break;
            case Constants.ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                orCDDocumentMetaDataList = documentSearchService.getOrCDMedicalImagingReportsDocumentList(searchCriteria);
                break;
            case Constants.ORCD_MEDICAL_IMAGES_CLASSCODE:
                orCDDocumentMetaDataList = documentSearchService.getOrCDMedicalImagesDocumentList(searchCriteria);
                break;
        }

        return orCDDocumentMetaDataList;
    }

    private void buildOrCDExtrinsicObject(AdhocQueryRequest request, AdhocQueryResponse response, OrCDDocumentMetaData orCDDocumentMetaData) {
        ExtrinsicObjectType eotXML = ofRim.createExtrinsicObjectType();
        final String confidentialityCode = orCDDocumentMetaData.getConfidentiality() == null
                || orCDDocumentMetaData.getConfidentiality().getConfidentialityCode() == null ? "N"
                : orCDDocumentMetaData.getConfidentiality().getConfidentialityCode();
        final String confidentialityDisplay = orCDDocumentMetaData.getConfidentiality() == null
                || orCDDocumentMetaData.getConfidentiality().getConfidentialityDisplay() == null ? "Normal"
                : orCDDocumentMetaData.getConfidentiality().getConfidentialityDisplay();
        final String languageCode = orCDDocumentMetaData.getLanguage();
        String xmlUUID = prepareExtrinsicObjectOrCD(DocumentType.ORCD, orCDDocumentMetaData.getEffectiveTime(),
                orCDDocumentMetaData.getRepositoryId(), request, eotXML, orCDDocumentMetaData.getId(), confidentialityCode, confidentialityDisplay, languageCode, orCDDocumentMetaData.getClassCode(), orCDDocumentMetaData.getDocumentFileType(), orCDDocumentMetaData.getSize());
        response.getRegistryObjectList().getIdentifiable().add(ofRim.createExtrinsicObject(eotXML));
        //TODO Mathias - To be reviewed if this is ok for the OrCD, for the other services an association object with both the XML and PDF is returned.
        if (!StringUtils.isEmpty(xmlUUID)) {
            response.getRegistryObjectList().getIdentifiable().add(ofRim.createAssociation(makeAssociation(xmlUUID, xmlUUID)));
        }
    }

    private AdhocQueryResponse handleOrCDExceptionCases(AdhocQueryResponse response, RegistryErrorList rel, List<OrCDDocumentMetaData> orCDDocumentMetaDataList) {
        if (orCDDocumentMetaDataList == null) {
            rel.getRegistryError().add(createErrorMessage("4103", "orCD registry could not be accessed.", "", true));
            response.setRegistryErrorList(rel);
            response.setStatus(AdhocQueryResponseStatus.FAILURE);
        } else if (orCDDocumentMetaDataList.isEmpty()) {
            rel.getRegistryError().add(createErrorMessage("1103", "There is no original clinical data of the requested type registered for the given patient.", "", true));
            response.setRegistryErrorList(rel);
            response.setStatus(AdhocQueryResponseStatus.SUCCESS);
        }
        return response;
    }


    /**
     * @param doc
     * @param registryErrorList
     * @param registryResponseElement
     * @param isTranscode
     * @param eventLog
     * @return
     * @throws Exception
     */
    private Document transformDocument(Document doc, OMElement registryErrorList, OMElement registryResponseElement,
                                       boolean isTranscode, EventLog eventLog) {

        logger.debug("Transforming document, isTranscode: '{}' - Event Type: '{}'", isTranscode, eventLog.getEventType());
        if (eventLog.getReqM_PatricipantObjectDetail() != null) {
            String requester = new String(eventLog.getReqM_PatricipantObjectDetail());
            if (loggerClinical.isDebugEnabled() && !org.apache.commons.lang3.StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                loggerClinical.debug("Participant Requester: '{}'", requester);
            }
        }
        if (eventLog.getResM_PatricipantObjectDetail() != null) {
            String responder = new String(eventLog.getResM_PatricipantObjectDetail());
            if (loggerClinical.isDebugEnabled() && !org.apache.commons.lang3.StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                loggerClinical.debug("Participant Responder: '{}'", responder);
            }
        }

        Document returnDoc;
        try {
            TMResponseStructure tmResponse;
            String operationType;
            if (isTranscode) {
                operationType = "toEpSOSPivot";
                logger.debug("Transforming document to epSOS pivot...");
                tmResponse = transformationService.toEpSOSPivot(doc);
            } else {
                operationType = "translate";
                logger.debug("Translating document to '{}'", Constants.LANGUAGE_CODE);
                tmResponse = transformationService.translate(doc, Constants.LANGUAGE_CODE);
            }

            OMNamespace ns = registryResponseElement.getNamespace();
            OMNamespace ons = omFactory.createOMNamespace(ns.getNamespaceURI(), "a");

            for (int i = 0; i < tmResponse.getErrors().size(); i++) {
                ITMTSAMEror error = tmResponse.getErrors().get(i);

                registryErrorList.addChild(createErrorOMMessage(ons, error.getCode(), error.getDescription(),
                        "ECDATransformationHandler.Error." + operationType + "(" + error.getCode() + " / " + error.getDescription() + ")",
                        false));
            }

            for (int i = 0; i < tmResponse.getWarnings().size(); i++) {
                ITMTSAMEror error = tmResponse.getWarnings().get(i);

                registryErrorList.addChild(createErrorOMMessage(ons, error.getCode(), error.getDescription(),
                        "ECDATransformationHandler.Error." + operationType + "(" + error.getCode() + " / " + error.getDescription() + ")",
                        true));
            }

            returnDoc = tmResponse.getResponseCDA();
            if (registryErrorList.getChildElements().hasNext()) {
                registryResponseElement.addChild(registryErrorList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return returnDoc;
    }

    /**
     * @param request
     * @param soapHeader
     * @param eventLog
     * @param omElement
     * @throws Exception
     */
    private void retrieveDocumentSetBuilder(RetrieveDocumentSetRequestType request, SOAPHeader soapHeader,
                                            EventLog eventLog, OMElement omElement) throws Exception {

        OMNamespace ns = omFactory.createOMNamespace("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "");
        OMElement registryResponse = omFactory.createOMElement("RegistryResponse", ns);
        OMElement registryErrorList = omFactory.createOMElement("RegistryErrorList", ns);
        OMNamespace ns2 = omElement.getNamespace();
        OMElement documentResponse = omFactory.createOMElement("DocumentResponse", ns2);

        boolean documentReturned = false;
        boolean failure = false;

        Element soapHeaderElement;
        String classCodeValue = null;

        // Start processing within a labeled block, break on certain errors
        processLabel:
        {
            try {
                soapHeaderElement = XMLUtils.toDOM(soapHeader);
            } catch (Exception e) {
                logger.error(null, e);
                throw e;
            }

            documentSearchService.setSOAPHeader(soapHeaderElement);

            String documentId = request.getDocumentRequest().get(0).getDocumentUniqueId();
            String patientId = trimDocumentEntryPatientId(Helper.getDocumentEntryPatientIdFromTRCAssertion(soapHeaderElement));
            String repositoryId = getRepositoryUniqueId(request);
            if (OpenNCPConstants.NCP_SERVER_MODE != ServerMode.PRODUCTION && loggerClinical.isDebugEnabled()) {
                loggerClinical.debug("Retrieving clinical document by criteria:\nPatient ID: '{}'\nDocument ID: '{}'\nRepository ID: '{}'",
                        patientId, documentId, repositoryId);
            }
            //try getting country code from the certificate
            String countryCode = null;
            String distinguishedName = eventLog.getSC_UserID();
            logger.info("[Certificate] Distinguished Name: '{}'", distinguishedName);
            int cIndex = distinguishedName.indexOf("C=");
            if (cIndex > 0) {
                countryCode = distinguishedName.substring(cIndex + 2, cIndex + 4);
            }
            // Mustafa: This part is added for handling consents when the call is not https. In this case, we check
            // the country code of the signature certificate that ships within the HCP assertion
            // TODO: Might be necessary to remove later, although it does no harm in reality!
            if (countryCode == null) {
                logger.info("Could not get client country code from the service consumer certificate. " +
                        "The reason can be that the call was not via HTTPS. " +
                        "Will check the country code from the signature certificate now.");
                countryCode = SAML2Validator.getCountryCodeFromHCPAssertion(soapHeaderElement);
                if (countryCode != null) {
                    logger.info("Found the client country code via the signature certificate.");
                } else {
                    InsufficientRightsException e = new InsufficientRightsException();
                    registryErrorList.addChild(createErrorOMMessage(ns, e.getCode(), e.getMessage(), "", false));
                    break processLabel;
                }
            }

            logger.info("The client country code to be used by the PDP '{}' ", countryCode);

            // Then, it is the Policy Decision Point (PDP) that decides according to the consent of the patient
            if (!SAML2Validator.isConsentGiven(patientId, countryCode)) {
                InsufficientRightsException e = new InsufficientRightsException(4701);
                registryErrorList.addChild(createErrorOMMessage(ns, e.getCode(), e.getMessage(), "", false));
                break processLabel;
            }

            // Evidence for call to NI for XCA Retrieve
            /* Joao: we MUST generate NRO when NCPA sends to NI.This was throwing errors because we were not passing a XML document.
                We're passing data like:
                "SearchCriteria: {patientId = 12445ASD}"
                So we provided a XML representation of such data */
            try {
                EvidenceUtils.createEvidenceREMNRO(DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId).asXml(),
                        Constants.NCP_SIG_KEYSTORE_PATH, Constants.NCP_SIG_KEYSTORE_PASSWORD, Constants.NCP_SIG_PRIVATEKEY_ALIAS,
                        Constants.SP_KEYSTORE_PATH, Constants.SP_KEYSTORE_PASSWORD, Constants.SP_PRIVATEKEY_ALIAS,
                        Constants.NCP_SIG_KEYSTORE_PATH, Constants.NCP_SIG_KEYSTORE_PASSWORD, Constants.NCP_SIG_PRIVATEKEY_ALIAS,
                        IHEEventType.PATIENT_SERVICE_RETRIEVE.getCode(), new DateTime(),
                        EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(), "NI_XCA_RETRIEVE_REQ",
                        Helper.getTRCAssertion(soapHeaderElement).getID() + "__" + DateUtil.getCurrentTimeGMT());
            } catch (Exception e) {
                logger.error("createEvidenceREMNRO: '{}'", ExceptionUtils.getStackTrace(e), e);
            }

            //TODO: EHNCP-1271 - Shall we indicate a specific ERROR Code???
            EPSOSDocument epsosDoc;
            try {
                epsosDoc = documentSearchService.getDocument(DocumentFactory.createSearchCriteria()
                        .add(Criteria.DocumentId, documentId)
                        .add(Criteria.PatientId, patientId)
                        .add(Criteria.RepositoryId, repositoryId));
                //  TODO: EHNCP-2055 Inconsistency in handling patient id
                //  logger.info("[WS-Server] National Document:\n'{}'", epsosDoc.toString());
            } catch (NIException e) {
                logger.error("NIException: '{}'", e.getMessage(), e);
                registryErrorList.addChild(createErrorOMMessage(ns, e.getCode(), e.getMessage(), "", false));
                break processLabel;
            }

            if (epsosDoc == null) {

                //  Evidence for response from NI in case of failure
                //  This should be NRR of NCPA receiving from NI. This was throwing errors because we were not passing a XML document.
                //  We're passing data like: "SearchCriteria: {patientId = 12445ASD}"
                //  So we provided a XML representation of such data. Still, evidence is generated based on request data, not response.
                //  This NRR is optional as per the CP. So we leave this commented.
//                try {
//                    EvidenceUtils.createEvidenceREMNRR(DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId).asXml(),
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                            IHEEventType.epsosPatientServiceRetrieve.getCode(),
//                            new DateTime(),
//                            EventOutcomeIndicator.TEMPORAL_FAILURE.getCode().toString(),
//                            "NI_XCA_RETRIEVE_RES_FAIL",
//                            Helper.getTRCAssertion(soapHeaderElement).getID() + "__" + DateUtil.getCurrentTimeGMT());
//                } catch (Exception e) {
//                    logger.error(ExceptionUtils.getStackTrace(e));
//                }
                logger.error("[National Connector] No document returned by the National Infrastructure");
                registryErrorList.addChild(createErrorOMMessage(ns, "XDSMissingDocument", "Requested document not found.", "", false));
                break processLabel;
            }

            // Evidence for response from NI in case of success
            /* Joao: This should be NRR of NCPA receiving from NI.
                    This was throwing errors because we were not passing a XML document.
                    We're passing data like:
                    "SearchCriteria: {patientId = 12445ASD}"
                    So we provided a XML representation of such data. Still, evidence is generated based on request data, not response.
                    This NRR is optional as per the CP. So we leave this commented */
//            try {
//                EvidenceUtils.createEvidenceREMNRR(DocumentFactory.createSearchCriteria().add(Criteria.PatientId, patientId).asXml(),
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                        IHEEventType.epsosPatientServiceRetrieve.getCode(),
//                        new DateTime(),
//                        EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
//                        "NI_XCA_RETRIEVE_RES_SUCC",
//                        DateUtil.getCurrentTimeGMT());
//            } catch (Exception e) {
//                logger.error(ExceptionUtils.getStackTrace(e));
//            }

            classCodeValue = epsosDoc.getClassCode();

            try {
                SAML2Validator.validateXCAHeader(soapHeaderElement, classCodeValue);
            } catch (InsufficientRightsException e) {
                logger.error("InsufficientRightsException: '{}'", e.getMessage(), e);
                registryErrorList.addChild(createErrorOMMessage(ns, e.getCode(), e.getMessage(), "", false));
                break processLabel;
            } catch (AssertionValidationException e) {
                logger.error("AssertionValidationException: '{}'", e.getMessage(), e);
                registryErrorList.addChild(createErrorOMMessage(ns, e.getCode(), e.getMessage(), "", false));
                break processLabel;
            } catch (SMgrException e) {
                logger.error("SMgrException: '{}'", e.getMessage(), e);
                registryErrorList.addChild(createErrorOMMessage(ns, "", e.getMessage(), "", false));
                break processLabel;
            }

            logger.info("XCA Retrieve Request is valid.");
            OMElement homeCommunityId = omFactory.createOMElement("HomeCommunityId", ns2);
            homeCommunityId.setText(request.getDocumentRequest().get(0).getHomeCommunityId());
            documentResponse.addChild(homeCommunityId);

            OMElement repositoryUniqueId = omFactory.createOMElement("RepositoryUniqueId", ns2);
            repositoryUniqueId.setText(request.getDocumentRequest().get(0).getRepositoryUniqueId());
            documentResponse.addChild(repositoryUniqueId);

            OMElement documentUniqueId = omFactory.createOMElement("DocumentUniqueId", ns2);
            documentUniqueId.setText(documentId);
            documentResponse.addChild(documentUniqueId);

            OMElement mimeType = omFactory.createOMElement("mimeType", ns2);
            mimeType.setText(MediaType.TEXT_XML_VALUE);
            documentResponse.addChild(mimeType);

            OMElement document = omFactory.createOMElement("Document", omFactory.createOMNamespace("urn:ihe:iti:xds-b:2007", ""));
            logger.info("XCA Retrieve Response has been created.");
            try {
                Document doc = epsosDoc.getDocument();
                logger.info("Client userID: '{}'", eventLog.getSC_UserID());

                if (doc != null) {
                    logger.info("[National Infrastructure] CDA Document:\n'{}'", epsosDoc.getClassCode());
                    /* Validate CDA eHDSI Friendly */
                    if (OpenNCPValidation.isValidationEnable()) {

                        OpenNCPValidation.validateCdaDocument(XMLUtil.documentToString(epsosDoc.getDocument()),
                                NcpSide.NCP_A, epsosDoc.getClassCode(), false);
                    }
                    // Transcode to eHDSI Pivot
                    if(!Constants.getClassCodesOrCD().contains(classCodeValue)) {
                        doc = transformDocument(doc, registryErrorList, registryResponse, true, eventLog);
                    }
                    if (!checkIfOnlyWarnings(registryErrorList)) {

                        // If the transformation process has raised at least one FATAL Error, we should determine which
                        // XCAError code has to be provided according the corresponding TM Error Code
                        Iterator<OMElement> errors = registryErrorList.getChildElements();
                        while (errors.hasNext()) {

                            OMElement errorCode = errors.next();
                            logger.error("Error: '{}'-'{}'", errorCode.getText(), errorCode.getAttributeValue(QName.valueOf("errorCode")));
                            logger.error("TRANSCODING ERROR: '{}'-'{}'", TMError.ERROR_REQUIRED_CODED_ELEMENT_NOT_TRANSCODED.getCode(),
                                    errorCode.getAttributeValue(QName.valueOf("errorCode")));

                            if (StringUtils.startsWith(errorCode.getAttributeValue(QName.valueOf("errorCode")), "45")) {

                                registryErrorList.addChild(createErrorOMMessage(ns, XCAError.ERROR_4203.getCode(), XCAError.ERROR_4203.getMessage(), "", false));
                                // If the error is FATAL flag failure has been set to true
                                failure = true;
                                break;
                            }
                        }
                    }
                    /* Validate CDA eHDSI Pivot */
                    if (OpenNCPValidation.isValidationEnable()) {
                        OpenNCPValidation.validateCdaDocument(XMLUtils.toOM(doc.getDocumentElement()).toString(),
                                NcpSide.NCP_A, epsosDoc.getClassCode(), true);
                    }
                }

                // If there is no failure during the process, the CDA document has been attached to the response
                logger.info("Error Registry: Failure '{}'", failure);
                if (!failure) {
                    ByteArrayDataSource dataSource = null;
                    if (doc != null) {
                        dataSource = new ByteArrayDataSource(XMLUtils.toOM(doc.getDocumentElement()).toString().getBytes(),
                                "text/xml;charset=UTF-8");
                    }
                    DataHandler dataHandler = new DataHandler(dataSource);
                    OMText textData = omFactory.createOMText(dataHandler, true);
                    textData.setOptimize(true);
                    document.addChild(textData);

                    logger.debug("Returning document '{}'", documentId);
                    documentResponse.addChild(document);
                    documentReturned = true;
                }
            } catch (Exception e) {
                failure = true;
                logger.error("Exception: '{}'", e.getMessage(), e);
                registryErrorList.addChild(createErrorOMMessage(ns, "", e.getMessage(), "", false));
            }
        }

        // If the registryErrorList is empty or contains only Warning, the status of the request is SUCCESS
        if (!registryErrorList.getChildElements().hasNext()) {
            logger.info("XCA Retrieve Document - Transformation Status: '{}'\nDefault Case", AdhocQueryResponseStatus.SUCCESS);
            registryResponse.addAttribute(omFactory.createOMAttribute("status", null,
                    AdhocQueryResponseStatus.SUCCESS));
        } else {
            if (checkIfOnlyWarnings(registryErrorList)) {
                logger.info("XCA Retrieve Document - Transformation Status: '{}'\nCheck Warning", AdhocQueryResponseStatus.SUCCESS);
                registryResponse.addAttribute(omFactory.createOMAttribute("status", null,
                        AdhocQueryResponseStatus.SUCCESS));
            } else if (failure) {
                // If there is a failure during the request process, the status is FAILURE
                logger.info("XCA Retrieve Document - Transformation Status: '{}'\nCheck Warning Failure: '{}'", AdhocQueryResponseStatus.FAILURE, failure);
                registryResponse.addAttribute(omFactory.createOMAttribute("status", null,
                        AdhocQueryResponseStatus.FAILURE));
            } else {
                //  Otherwise the status is PARTIAL SUCCESS
                logger.info("XCA Retrieve Document - Transformation Status: '{}'\nOtherwise...", AdhocQueryResponseStatus.PARTIAL_SUCCESS);
                registryResponse.addAttribute(omFactory.createOMAttribute("status", null,
                        AdhocQueryResponseStatus.PARTIAL_SUCCESS));
            }
        }

        logger.info("Preparing Event Log of the Response:");
        try {
            boolean errorsDiscovered = registryErrorList.getChildElements().hasNext();
            if (errorsDiscovered) {
                registryResponse.addChild(registryErrorList);
            }
            omElement.addChild(registryResponse);
            if (documentReturned) {
                omElement.addChild(documentResponse);
            }
            prepareEventLogForRetrieve(eventLog, request, errorsDiscovered, documentReturned, registryErrorList, soapHeaderElement, classCodeValue);
        } catch (Exception ex) {
            logger.error("Prepare Audit log failed. '{}'", ex.getMessage(), ex);
            // Is this fatal?
        }

        //Once the response and the audit message have been prepared, the registryErrorList might be cleaned depending of the errors status.
        if (failure) {
            //Only XCA Error Code defined into the XCA Profile might be attached to the response in case of FAILURE.
            Iterator<OMElement> errors = registryErrorList.getChildElements();
            while (errors.hasNext()) {

                OMElement errorCode = errors.next();
                logger.error("Error: '{}'-'{}'", errorCode.getText(), errorCode.getAttributeValue(QName.valueOf("errorCode")));
                if (!StringUtils.equals(XCAError.ERROR_4203.getCode(), errorCode.getAttributeValue(QName.valueOf("errorCode")))) {
                    errors.remove();
                }
            }
        }
    }

    /**
     * This method will check if the Registry error list only contains Warnings.
     *
     * @return boolean value, indicating if the list only contains warnings.
     */
    private boolean checkIfOnlyWarnings(OMElement registryErrorList) {

        boolean onlyWarnings = true;
        OMElement element;
        Iterator it = registryErrorList.getChildElements();

        while (it.hasNext()) {

            element = (OMElement) it.next();
            if (StringUtils.equals(element.getAttribute(QName.valueOf("severity")).getAttributeValue(),
                    RegistryErrorSeverity.ERROR_SEVERITY_ERROR)) {
                logger.debug("Error has been detected for Element: '{}'", element.getText());
                onlyWarnings = false;
            }
        }
        return onlyWarnings;
    }

    /**
     * Method responsible of the AdhocQueryResponse message if the operation requested is not supported by the server.
     * RegistryError shall contain:
     * errorCode: required.
     * codeContext: required - Supplies additional detail for the errorCode.
     * severity: required - Indicates the severity of the error.
     * Shall be one of:
     * urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error
     * urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Warning
     * location: optional - Supplies the location of the error module name and line number or stack trace if appropriate.
     *
     * @param request - original AdhocQueryRequest
     * @param e       - Exception thrown by the system
     * @return response - populated with te specific Error Code according the document Class Code.
     */
    private AdhocQueryResponse handleUnsupportedOperationException(AdhocQueryRequest request, UnsupportedOperationException e) {

        AdhocQueryResponse response = ofQuery.createAdhocQueryResponse();
        RegistryErrorList registryErrorList = ofRs.createRegistryErrorList();
        RegistryError registryError = ofRs.createRegistryError();

        // Create Registry Object List
        response.setRegistryObjectList(ofRim.createRegistryObjectListType());

        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        registryError.setLocation(result.toString().trim());

        List<String> classCodeValues = getDocumentEntryClassCodes(request);

        for (String classCodeValue: classCodeValues) {
            switch (classCodeValue) {
                case Constants.EP_CLASSCODE:
                    registryError.setSeverity(RegistryErrorSeverity.ERROR_SEVERITY_WARNING);
                    registryError.setErrorCode("1101");
                    registryError.setValue("No ePrescriptions are registered for the given patient.");
                    registryError.setCodeContext("The XDS repository does not contain any ePrescription related to the current patient");
                    break;
                case Constants.PS_CLASSCODE:
                    registryError.setSeverity(RegistryErrorSeverity.ERROR_SEVERITY_WARNING);
                    registryError.setErrorCode("1102");
                    registryError.setValue("No patient summary is registered for the given patient.");
                    registryError.setCodeContext("The XDS repository does not contain any Patient Summary related to the current patient");
                    break;
                case Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                case Constants.ORCD_LABORATORY_RESULTS_CLASSCODE:
                case Constants.ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                case Constants.ORCD_MEDICAL_IMAGES_CLASSCODE:
                    registryError.setSeverity(RegistryErrorSeverity.ERROR_SEVERITY_WARNING);
                    registryError.setErrorCode("1104");
                    registryError.setValue("No original clinical document of the requested type is registered for the given patient.");
                    registryError.setCodeContext("The XDS repository does not contain any OrCD of the requested type related to the current patient");
                    break;
                default:
                    registryError.setSeverity(RegistryErrorSeverity.ERROR_SEVERITY_WARNING);
                    registryError.setErrorCode("1100");
                    registryError.setValue("No documents are registered for the given patient.");
                    registryError.setCodeContext("The XDS repository does not contain any documents related to the current patient");
                    break;
            }
            registryErrorList.getRegistryError().add(registryError);
        }
        response.setRegistryErrorList(registryErrorList);
        // Errors managed are only WARNING so the AdhocQueryResponse is considered as successful.
        response.setStatus(AdhocQueryResponseStatus.SUCCESS);

        return response;
    }

    /**
     * XCA list operation implementation, returns the list of patient summaries or ePrescriptions, depending on the query.
     */
    @Override
    public AdhocQueryResponse queryDocument(AdhocQueryRequest adhocQueryRequest, SOAPHeader sh, EventLog eventLog)
            throws Exception {

        AdhocQueryResponse result = ofQuery.createAdhocQueryResponse();
        try {
            adhocQueryResponseBuilder(adhocQueryRequest, result, sh, eventLog);
        } catch (UnsupportedOperationException uoe) {
            result = handleUnsupportedOperationException(adhocQueryRequest, uoe);
        }
        return result;
    }

    /**
     * XCA retrieve operation implementation, returns the particular document requested by the caller.
     * The response is placed in the OMElement
     */
    @Override
    public void retrieveDocument(RetrieveDocumentSetRequestType request, SOAPHeader sh, EventLog eventLog,
                                 OMElement response) throws Exception {

        retrieveDocumentSetBuilder(request, sh, eventLog, response);
    }

    /**
     * This auxiliary service returns the service name, based on a provided class code.
     */
    private String getDocumentName(final String classCodeValue) {

        if (classCodeValue.contains(Constants.PS_CLASSCODE)) {
            return Constants.PS_TITLE;
        } else if (classCodeValue.contains(Constants.ED_CLASSCODE)) {
            return Constants.ED_TITLE;
        } else if (classCodeValue.contains(Constants.MRO_CLASSCODE)) {
            return Constants.MRO_TITLE;
        } else {
            return null;
        }
    }
}
