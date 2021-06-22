package tr.com.srdc.epsos.ws.xdr.client;

import ee.affecto.epsos.util.EventLogClientUtil;
import eu.epsos.exceptions.DocumentTransformationException;
import eu.epsos.pt.ws.client.xdr.transformation.TMServices;
import eu.epsos.util.IheConstants;
import eu.epsos.util.xca.XCAConstants;
import eu.epsos.util.xdr.XDRConstants;
import eu.epsos.validation.datamodel.common.NcpSide;
import eu.europa.ec.sante.ehdsi.gazelle.validation.OpenNCPValidation;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.RegisteredService;
import eu.europa.ec.sante.ehdsi.openncp.pt.common.DynamicDiscoveryService;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import tr.com.srdc.epsos.data.model.PatientDemographics;
import tr.com.srdc.epsos.data.model.PatientId;
import tr.com.srdc.epsos.data.model.XdrRequest;
import tr.com.srdc.epsos.util.Constants;
import tr.com.srdc.epsos.util.DateUtil;
import tr.com.srdc.epsos.util.XMLUtil;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * @author erdem
 */
public class XDSbRepositoryServiceInvoker {

    //TODO: this class implementation needs to be reviewed deeply
    private static final ObjectFactory ofRim = new ObjectFactory();
    private final Logger logger = LoggerFactory.getLogger(XDSbRepositoryServiceInvoker.class);

    /**
     * Provide And Register Document Set
     *
     * @param request
     * @param countryCode
     * @param docClassCode
     * @return
     * @throws RemoteException
     */
    public RegistryResponseType provideAndRegisterDocumentSet(final XdrRequest request, final String countryCode, String docClassCode)
            throws RemoteException {

        logger.info("[XDSb Repository] XDR Request: '{}', '{}', '{}'", request.getIdAssertion().getID(), countryCode, docClassCode);
        RegistryResponseType response;
        String submissionSetUuid = Constants.UUID_PREFIX + UUID.randomUUID();

        String endpointReference = null;
        DynamicDiscoveryService dynamicDiscoveryService = new DynamicDiscoveryService();

        switch (docClassCode) {
            case Constants.ED_CLASSCODE:
            case Constants.EDD_CLASSCODE:
                endpointReference = dynamicDiscoveryService.getEndpointUrl(countryCode.toLowerCase(Locale.ENGLISH), RegisteredService.DISPENSATION_SERVICE);
                break;
            case Constants.CONSENT_CLASSCODE:
            case Constants.HCER_CLASSCODE:
                endpointReference = dynamicDiscoveryService.getEndpointUrl(countryCode.toLowerCase(Locale.ENGLISH), RegisteredService.CONSENT_SERVICE);
                break;
            default:
                logger.warn("Document Class Code: '{}' not supported!!! Endpoint cannot be loaded", docClassCode);
                break;
        }
        // WebService Client stubs
        DocumentRecipient_ServiceStub axisClient = new DocumentRecipient_ServiceStub(endpointReference);
        axisClient._getServiceClient().getOptions().setTo(new EndpointReference(endpointReference));
        axisClient.setCountryCode(countryCode);
        axisClient.setClassCode(docClassCode);

        // Dummy handler for any mustUnderstand header within server response
        EventLogClientUtil.createDummyMustUnderstandHandler(axisClient);

        //  Retrieving XDR document from request
        Document document = null;
        try {
            document = XMLUtil.parseContent(request.getCda());
        } catch (Exception e) {
            logger.warn("Could not parse dispense", e);
        }
        String languageCode = document != null ? getLanguageCode(document) : XDRConstants.EXTRINSIC_OBJECT.LANGUAGE_CODE_DEFAULT_VALUE;

        // ProvideAndRegisterDocumentSetRequestType
        ihe.iti.xds_b._2007.ObjectFactory ofXds = new ihe.iti.xds_b._2007.ObjectFactory();
        oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory ofLcm = new oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory();

        ProvideAndRegisterDocumentSetRequestType registerDocumentSetRequest = ofXds.createProvideAndRegisterDocumentSetRequestType();
        registerDocumentSetRequest.setSubmitObjectsRequest(ofLcm.createSubmitObjectsRequest());
        registerDocumentSetRequest.getSubmitObjectsRequest().setRegistryObjectList(ofRim.createRegistryObjectListType());

        //  XDS Document
        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        ExtrinsicObjectType extrinsicObject = makeExtrinsicObject(request, uuid, docClassCode, languageCode);
        registerDocumentSetRequest.getSubmitObjectsRequest().getRegistryObjectList().getIdentifiable()
                .add(ofRim.createExtrinsicObject(extrinsicObject));

        RegistryPackageType registryPackage = prepareRegistryPackage(request, docClassCode, submissionSetUuid);
        registerDocumentSetRequest.getSubmitObjectsRequest().getRegistryObjectList().getIdentifiable()
                .add(ofRim.createRegistryPackage(registryPackage));

        ClassificationType classification = prepareClassification(submissionSetUuid);
        registerDocumentSetRequest.getSubmitObjectsRequest().getRegistryObjectList().getIdentifiable()
                .add(ofRim.createClassification(classification));

        AssociationType1 association = prepareAssociation(uuid, submissionSetUuid);
        registerDocumentSetRequest.getSubmitObjectsRequest().getRegistryObjectList().getIdentifiable()
                .add(ofRim.createAssociation(association));

        // XDR Document
        ProvideAndRegisterDocumentSetRequestType.Document xdrDocument = new ProvideAndRegisterDocumentSetRequestType.Document();
        xdrDocument.setId(uuid);

        byte[] cdaBytes = request.getCda().getBytes(StandardCharsets.UTF_8);
        try {
            /* Validate CDA epSOS Friendly */
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validateCdaDocument(request.getCda(), NcpSide.NCP_B, docClassCode, false);
            }
            if (!StringUtils.equals(docClassCode, Constants.EDD_CLASSCODE)) {
                byte[] transformedCda = TMServices.transformDocument(cdaBytes);
                xdrDocument.setValue(transformedCda);
            } else {
                xdrDocument.setValue(cdaBytes);
            }
            /* Validate CDA epSOS Pivot */
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validateCdaDocument(new String(xdrDocument.getValue(), StandardCharsets.UTF_8), NcpSide.NCP_B, docClassCode, true);
            }
        } catch (DocumentTransformationException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            xdrDocument.setValue(cdaBytes);
        } catch (Exception ex) {
            logger.error(null, ex);
        }
        registerDocumentSetRequest.getDocument().add(xdrDocument);

        response = axisClient.documentRecipient_ProvideAndRegisterDocumentSetB(registerDocumentSetRequest, request.getIdAssertion(), request.getTrcAssertion());

        return response;
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
    private ClassificationType makeClassification(String classificationScheme, String classifiedObject,
                                                  String nodeRepresentation, String value, String name) {

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
    private ClassificationType makeClassification0(String classificationScheme, String classifiedObject, String nodeRepresentation) {

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        ClassificationType rimClassification = ofRim.createClassificationType();
        rimClassification.setId(uuid);
        rimClassification.setNodeRepresentation(nodeRepresentation);
        rimClassification.setClassificationScheme(classificationScheme);
        rimClassification.setClassifiedObject(classifiedObject);
        return rimClassification;
    }

    /**
     * @param value
     * @return
     */
    private InternationalStringType makeInternationalString(String value) {

        InternationalStringType internationalString = new InternationalStringType();
        internationalString.getLocalizedString().add(ofRim.createLocalizedStringType());
        internationalString.getLocalizedString().get(0).setValue(value);
        return internationalString;
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
        ex.setObjectType(XDRConstants.REGREP_EXT_IDENT);
        ex.setRegistryObject(registryObject);
        ex.setValue(value);
        ex.setName(ofRim.createInternationalStringType());
        ex.getName().getLocalizedString().add(ofRim.createLocalizedStringType());
        ex.getName().getLocalizedString().get(0).setValue(name);
        return ex;
    }

    /**
     * @param request
     * @param uuid
     * @param docClassCode
     * @return
     */
    private ExtrinsicObjectType makeExtrinsicObject(XdrRequest request, String uuid, String docClassCode, String language) {
        return makeExtrinsicObject(request, uuid, docClassCode, language, Boolean.FALSE);
    }

    /**
     * @param request
     * @param uuid
     * @param docClassCode
     * @param isPDF
     * @return
     */
    private ExtrinsicObjectType makeExtrinsicObject(XdrRequest request, String uuid, String docClassCode, String language, Boolean isPDF) {

        if (Boolean.TRUE.equals(isPDF)) {
            // TODO A.R. isPDF unfinished...
            logger.warn("PDF document will be processed, but this is not fully supported by current implementation");
        }

        ExtrinsicObjectType result = ofRim.createExtrinsicObjectType();
        PatientDemographics patient = request.getPatient();
        PatientId patientId = patient.getIdList().get(0);

        // Attributes handling: identifier, mimeType, objectType and Status.
        result.setId(uuid);
        result.setMimeType(Constants.MIME_TYPE);
        result.setObjectType(XCAConstants.XDS_DOC_ENTRY_CLASSIFICATION_NODE);
        result.setStatus(IheConstants.REGREP_STATUSTYPE_APPROVED);

        // rim:Slot
        result.getSlot().add(makeSlot(XDRConstants.EXTRINSIC_OBJECT.CREATION_TIME,
                DateUtil.getCurrentTimeUTC(XDRConstants.EXTRINSIC_OBJECT.DATE_FORMAT)));
        result.getSlot().add(makeSlot(XDRConstants.EXTRINSIC_OBJECT.LANGUAGE_CODE_STR, language));
        result.getSlot().add(makeSlot(XDRConstants.EXTRINSIC_OBJECT.SOURCE_PATIENT_ID, patientId.toString()));

        /*
         * Classification
         */
        // Healthcare facility code
        result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.HEALTHCAREFACILITY_CODE_SCHEME, uuid,
                request.getCountryCode(), XDRConstants.EXTRINSIC_OBJECT.HEALTHCAREFACILITY_CODE_VALUE, request.getCountryName()));

        // Practice Setting code
        result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.PRACTICE_SETTING_CODE_SCHEME, uuid,
                Constants.NOT_USED_FIELD, XDRConstants.EXTRINSIC_OBJECT.PRACTICE_SETTING_CODE_NODEREPR, Constants.NOT_USED_FIELD));

        // Confidentiality Code
        result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.CONFIDENTIALITY_CODE_SCHEME, uuid,
                XDRConstants.EXTRINSIC_OBJECT.CONFIDENTIALITY_CODE_NODEREPR, XDRConstants.EXTRINSIC_OBJECT.CONFIDENTIALITY_CODE_VALUE,
                XDRConstants.EXTRINSIC_OBJECT.CONFIDENTIALITY_CODE_STR));

        // eHDSI Class Code
        switch (docClassCode) {
            case Constants.ED_CLASSCODE:
                //  urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_SCHEME, uuid,
                        Constants.ED_CLASSCODE, XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_VALUE, XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_ED_STR));
                break;
            case Constants.EDD_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_SCHEME, uuid,
                        Constants.EDD_CLASSCODE, XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_VALUE,
                        XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_EDD_STR));
                break;
            case Constants.CONSENT_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_SCHEME, uuid,
                        Constants.CONSENT_CLASSCODE, XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_VALUE, XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_CONS_STR));
                break;
            case Constants.HCER_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_SCHEME, uuid,
                        Constants.HCER_CLASSCODE, XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_VALUE, XDRConstants.EXTRINSIC_OBJECT.CLASS_CODE_HCER_STR));
                break;
            default:
                logger.warn("Document Class Code: '{}' not supported!!! ClassCodeScheme cannot be loaded", docClassCode);
                break;
        }

        //  eHDSI Format Code - urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d
        switch (docClassCode) {
            case Constants.CONSENT_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.FormatCode.FORMAT_CODE_SCHEME, uuid,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Consent.NotScannedDocument.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Consent.NotScannedDocument.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Consent.NotScannedDocument.DISPLAY_NAME));
                break;
            case Constants.ED_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.FormatCode.FORMAT_CODE_SCHEME, uuid,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Dispensation.EpsosPivotCoded.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Dispensation.EpsosPivotCoded.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Dispensation.EpsosPivotCoded.DISPLAY_NAME));
                break;
            case Constants.EDD_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.FormatCode.FORMAT_CODE_SCHEME, uuid,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.DispensationDiscard.PivotCoded.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.DispensationDiscard.PivotCoded.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.DispensationDiscard.PivotCoded.DISPLAY_NAME));
                break;
            case Constants.HCER_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.FormatCode.FORMAT_CODE_SCHEME, uuid,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Hcer.EpsosPivotCoded.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Hcer.EpsosPivotCoded.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.FormatCode.Hcer.EpsosPivotCoded.DISPLAY_NAME));
                break;
            default:
                logger.warn("Document Class Code: '{}' not supported!!! FormatCodeScheme cannot be loaded", docClassCode);
                break;
        }

        if (docClassCode.equals(Constants.CONSENT_CLASSCODE)) {
            result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_SCHEME, uuid,
                    getConsentOptCode(request.getCda()), XDRConstants.EXTRINSIC_OBJECT.EVENT_CODING_SCHEME,
                    getConsentOptName(request.getCda())));
        }

        //  External Identifiers
        //  XDSDocumentEntry.PatientId
        result.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.EXTRINSIC_OBJECT.XDSDOCENTRY_PATID_SCHEME,
                uuid, patientId.toString(), XDRConstants.EXTRINSIC_OBJECT.XDSDOCENTRY_PATID_STR));

        //  XDSDocument.EntryUUID
        if (docClassCode.equals(Constants.CONSENT_CLASSCODE)) {
            // TODO: missing XDSDocument.EntryUUID for Consent
            logger.warn("Patient Consent not supported!!!");
        }

        /* XDSDocument.UniqueId */
        result.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_SCHEME,
                uuid, request.getCdaId(), XDRConstants.EXTRINSIC_OBJECT.XDSDOC_UNIQUEID_STR));

        // Version Info not managed, since IHE simulator does not supports it, and the message is still valid at EVS.
        // result.setVersionInfo(ofRim.createVersionInfoType())
        // result.getVersionInfo().setVersionName(XDRConstants.EXTRINSIC_OBJECT.VERSION_INFO)

        // Other elements not defined in 3.4.2
        // result.setHome(Constants.HOME_COMM_ID) and result.setLid(uuid)

        // sourcePatientInfo
        SlotType1 slId = makeSlot(XDRConstants.EXTRINSIC_OBJECT.SRC_PATIENT_INFO_STR, "PID-3|" + patientId);
        slId.getValueList().getValue().add("PID-5|" + patient.getFamilyName() + "^" + patient.getGivenName());
        slId.getValueList().getValue().add("PID-7|" + new SimpleDateFormat("yyyyMMddkkmmss.SSSZZZZ", Locale.ENGLISH).format(patient.getBirthDate()));
        result.getSlot().add(slId);

        // eHDSI Type Code
        switch (docClassCode) {
            case Constants.CONSENT_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.TypeCode.TYPE_CODE_SCHEME,
                        uuid, XDRConstants.EXTRINSIC_OBJECT.TypeCode.Consent.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.Consent.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.Consent.DISPLAY_NAME));
                break;
            case Constants.ED_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.TypeCode.TYPE_CODE_SCHEME,
                        uuid, XDRConstants.EXTRINSIC_OBJECT.TypeCode.EDispensation.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.EDispensation.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.EDispensation.DISPLAY_NAME));
                break;
            case Constants.EDD_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.TypeCode.TYPE_CODE_SCHEME,
                        uuid, XDRConstants.EXTRINSIC_OBJECT.TypeCode.EDispensationDiscard.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.EDispensationDiscard.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.EDispensationDiscard.DISPLAY_NAME));
                break;
            case Constants.HCER_CLASSCODE:
                result.getClassification().add(makeClassification(XDRConstants.EXTRINSIC_OBJECT.TypeCode.TYPE_CODE_SCHEME,
                        uuid, XDRConstants.EXTRINSIC_OBJECT.TypeCode.Hcer.NODE_REPRESENTATION,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.Hcer.CODING_SCHEME,
                        XDRConstants.EXTRINSIC_OBJECT.TypeCode.Hcer.DISPLAY_NAME));
                break;
            default:
                logger.warn("Document Class Code: '{}' not supported!!! TypeCodeScheme cannot be loaded.", docClassCode);
                break;
        }
        return result;
    }

    /**
     * @param request
     * @param docClassCode
     * @param submissionSetUuid
     * @return
     */
    private RegistryPackageType prepareRegistryPackage(XdrRequest request, String docClassCode, String submissionSetUuid) {

        RegistryPackageType registryPackageType = ofRim.createRegistryPackageType();
        PatientId patientId = request.getPatient().getIdList().get(0);

        registryPackageType.setId(submissionSetUuid);
        registryPackageType.setObjectType(XDRConstants.REGISTRY_PACKAGE.OBJECT_TYPE_UUID);

        registryPackageType.getSlot().add(makeSlot(XDRConstants.REGISTRY_PACKAGE.SUBMISSION_TIME_STR,
                DateUtil.getDateByDateFormat(XDRConstants.REGISTRY_PACKAGE.SUBMISSION_TIME_FORMAT)));
        registryPackageType.setName(makeInternationalString(getNameFromClassCode(docClassCode)));
        registryPackageType.setDescription(makeInternationalString(getDescriptionFromClassCode(docClassCode)));

        ClassificationType classification;
        classification = makeClassification0(XDRConstants.REGISTRY_PACKAGE.AUTHOR_CLASSIFICATION_UUID, submissionSetUuid, "");
        classification.getSlot().add(makeSlot(IheConstants.AUTHOR_INSTITUTION_STR, getAuthorInstitution(request)));
        classification.getSlot().add(makeSlot(IheConstants.AUTHOR_PERSON_STR, getAuthorPerson(request)));
        registryPackageType.getClassification().add(classification);

        registryPackageType.getClassification().add(makeClassification(XDRConstants.REGISTRY_PACKAGE.CODING_SCHEME_UUID, submissionSetUuid,
                docClassCode, XDRConstants.REGISTRY_PACKAGE.CODING_SCHEME_VALUE, getSubmissionSetFromClassCode(docClassCode)));

        registryPackageType.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.REGISTRY_PACKAGE.XDSSUBMSET_UNIQUEID_SCHEME,
                submissionSetUuid, request.getSubmissionSetId(), XDRConstants.REGISTRY_PACKAGE.XDSSUBMSET_UNIQUEID_STR));

        registryPackageType.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.REGISTRY_PACKAGE.XDSSUBMSET_PATIENTID_SCHEME,
                submissionSetUuid, patientId.toString(), XDRConstants.REGISTRY_PACKAGE.XDSSUBMSET_PATIENTID_STR));

        // The XDSSubmissionSet.SourceId value should be the OID of the sender (e.g. HOME_COMMUNITY_ID),
        // according to ITI TF-3, Table 4.1-6, but not all OID prefixes are supported in IHE validator.
        registryPackageType.getExternalIdentifier().add(makeExternalIdentifier(XDRConstants.REGISTRY_PACKAGE.XDSSUBMSET_SOURCEID_SCHEME,
                submissionSetUuid, XDRConstants.REGISTRY_PACKAGE.XDSSUBMSET_SOURCEID_VALUE,
                XDRConstants.REGISTRY_PACKAGE.XDSSUBMSET_SOURCEID_STR));
        return registryPackageType;
    }

    /**
     * @param submissionSetUuid
     * @return
     */
    private ClassificationType prepareClassification(String submissionSetUuid) {

        ClassificationType classification = ofRim.createClassificationType();
        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        classification.setId(uuid);
        classification.setClassificationNode(XDRConstants.SUBMISSION_SET_CLASSIFICATION.CLASSIFICATION_NODE_UUID);
        classification.setClassifiedObject(submissionSetUuid);

        return classification;
    }

    /**
     * @param targetObject
     * @param submissionSetUuid
     * @return
     */
    private AssociationType1 prepareAssociation(String targetObject, String submissionSetUuid) {

        String uuid = Constants.UUID_PREFIX + UUID.randomUUID();
        AssociationType1 ast = ofRim.createAssociationType1();
        ast.setId(uuid);
        ast.setAssociationType(XDRConstants.REGREP_HAS_MEMBER);
        ast.setSourceObject(submissionSetUuid);
        ast.setTargetObject(targetObject);
        ast.getSlot().add(makeSlot(XDRConstants.SUBMISSION_SET_STATUS_STR, XDRConstants.ORIGINAL_STR));

        return ast;
    }

    /**
     * Obtains the AuthorInstitution information, namely Name and Id from the assertion.
     * And builds an HL7 V2.5 representation of the information.
     *
     * @param xdrRequest an XDR Request containing the assertion.
     * @return an HL7 V2.5 representation of the obtained information.
     */
    private String getAuthorInstitution(final XdrRequest xdrRequest) {

        String result;

        String organization = "Hospital";
        String organizationId = "1.2.3.4.5.6.7.8.9.1789.45";

        Assertion hcpAssertion;
        List<AttributeStatement> attrStatements;
        List<Attribute> attrs;

        hcpAssertion = xdrRequest.getIdAssertion();
        attrStatements = hcpAssertion.getAttributeStatements();

        if (attrStatements.size() != 1) {
            return null;
        }

        attrs = attrStatements.get(0).getAttributes();

        for (Attribute attr : attrs) {
            if (attr.getName().equals("urn:oasis:names:tc:xspa:1.0:subject:organization")) {
                organization = Objects.requireNonNull(attr.getAttributeValues().get(0).getDOM()).getTextContent();
            }
            if (attr.getName().equals("urn:oasis:names:tc:xspa:1.0:subject:organization-id")) {
                organizationId = Objects.requireNonNull(attr.getAttributeValues().get(0).getDOM()).getTextContent();
            }
        }

        if (organizationId.startsWith(Constants.OID_PREFIX)) {
            result = organization + "^^^^^^^^^" + organizationId.split(":")[2];
        } else {
            result = organization + "^^^^^^^^^" + organizationId;
        }

        return result;
    }

    /**
     * Obtains the AuthorPerson information, namely the Author Identifier and Assigning AuthorityId .
     * Then builds an HL7 V2.5 representation of the information.
     *
     * @param xdrRequest an XDR Request containing the assertion.
     * @return an HL7 V2.5 representation of the obtained information.
     */
    private String getAuthorPerson(final XdrRequest xdrRequest) {

        String result;

        String authorIdentifier = "Dr. Doctor";
        String assigningAuthorityId = "1.2.3.4.5.6.7.8.9.1789.45";

        Assertion hcpAssertion;
        List<AttributeStatement> attrStatements;
        List<Attribute> attrs;

        hcpAssertion = xdrRequest.getIdAssertion();
        attrStatements = hcpAssertion.getAttributeStatements();

        if (attrStatements.size() != 1) {
            return null;
        }
        attrs = attrStatements.get(0).getAttributes();

        for (Attribute attr : attrs) {
            if (attr.getName().equals("urn:oasis:names:tc:xacml:1.0:subject:subject-id")) {
                authorIdentifier = Objects.requireNonNull(attr.getAttributeValues().get(0).getDOM()).getTextContent();
            }
            if (attr.getName().equals("urn:oasis:names:tc:xspa:1.0:subject:organization-id")) {
                assigningAuthorityId = Objects.requireNonNull(attr.getAttributeValues().get(0).getDOM()).getTextContent();
            }
        }

        if (assigningAuthorityId.startsWith(Constants.OID_PREFIX)) {
            result = authorIdentifier + "&" + assigningAuthorityId.split(":")[2] + "&ISO";
        } else {
            result = authorIdentifier + "&" + assigningAuthorityId + "&ISO";
        }

        return result;
    }

    /**
     * This method will determine which EventCode (Name) is present in the Consent Document.
     *
     * @param document the consent CDA
     * @return the EventCode
     */
    private String getConsentOptName(String document) {

        if (document.contains(XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_REPRESENTATION_OPT_OUT)) {
            return XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_NAME_OPT_OUT;
        } else if (document.contains(XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_REPRESENTATION_OPT_IN)) {
            return XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_NAME_OPT_IN;
        } else {
            logger.error("Event Code not found in consent document!");
            return "Event Code not found in consent document!";
        }
    }

    /**
     * This method will determine which EventCode (Code) is present in the Consent Document.
     *
     * @param document the consent CDA
     * @return the EventCode
     */
    private String getConsentOptCode(String document) {

        if (document.contains(XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_REPRESENTATION_OPT_OUT)) {
            return XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_REPRESENTATION_OPT_OUT;
        } else if (document.contains(XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_REPRESENTATION_OPT_IN)) {
            return XDRConstants.EXTRINSIC_OBJECT.EVENT_CODE_NODE_REPRESENTATION_OPT_IN;
        } else {
            logger.error("Event Code not found in consent document!");
            return "Event Code not found in consent document!";
        }
    }

    /**
     * @param classCode
     * @return
     */
    private String getNameFromClassCode(String classCode) {

        switch (classCode) {
            case Constants.HCER_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.NAME_HCER;
            case Constants.CONSENT_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.NAME_CONSENT;
            case Constants.ED_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.NAME_ED;
            case Constants.EDD_CLASSCODE:
                return "Medication Dispensation Discard";
            default:
                logger.error("Class code does not have a matching name!");
                return null;
        }
    }

    /**
     * @param classCode
     * @return
     */
    private String getDescriptionFromClassCode(String classCode) {

        switch (classCode) {
            case Constants.HCER_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.DESCRIPTION_HCER;
            case Constants.CONSENT_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.DESCRIPTION_CONSENT;
            case Constants.ED_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.DESCRIPTION_ED;
            case Constants.EDD_CLASSCODE:
                return "Description of Medication Dispensation Discard";
            default:
                logger.error("Class code does not have a matching description!");
                return "Class Code does not have a matching description!";
        }
    }

    /**
     * @param classCode
     * @return
     */
    private String getSubmissionSetFromClassCode(String classCode) {

        switch (classCode) {
            case Constants.HCER_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.NAME_HCER;
            case Constants.CONSENT_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.CODING_SCHEME_CONS_STR;
            case Constants.ED_CLASSCODE:
                return XDRConstants.REGISTRY_PACKAGE.NAME_ED;
            case Constants.EDD_CLASSCODE:
                return "Medication Dispensation Discard";
            default:
                logger.error("Class Code does not have a matching submission set designation!");
                return "Class Code does not have a matching submission set designation!";
        }
    }

    /**
     * Util method retrieving the language code from a CDA document.
     *
     * @param doc - CDA document as DOM object.
     * @return ISO language code of the document.
     */
    private String getLanguageCode(Document doc) {
        List<Node> nodeList = XMLUtil.getNodeList(doc, "ClinicalDocument/languageCode");
        String languageCode = XDRConstants.EXTRINSIC_OBJECT.LANGUAGE_CODE_DEFAULT_VALUE;
        for (Node node : nodeList) {
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getAttributes().getNamedItem("code") != null) {
                languageCode = node.getAttributes().getNamedItem("code").getTextContent();
            }
        }
        return StringUtils.trim(languageCode);
    }
}
