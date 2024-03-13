package eu.europa.ec.sante.openncp.core.client.ihe;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.validation.OpenNCPValidation;
import eu.europa.ec.sante.openncp.core.client.ClientConnectorService;
import eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType;
import eu.europa.ec.sante.openncp.core.client.DocumentId;
import eu.europa.ec.sante.openncp.core.client.EpsosDocument;
import eu.europa.ec.sante.openncp.core.client.FilterParams;
import eu.europa.ec.sante.openncp.core.client.GenericDocumentCode;
import eu.europa.ec.sante.openncp.core.client.ObjectFactory;
import eu.europa.ec.sante.openncp.core.client.PatientDemographics;
import eu.europa.ec.sante.openncp.core.client.PatientId;
import eu.europa.ec.sante.openncp.core.client.QueryDocumentRequest;
import eu.europa.ec.sante.openncp.core.client.QueryDocumentsResponse;
import eu.europa.ec.sante.openncp.core.client.QueryPatientRequest;
import eu.europa.ec.sante.openncp.core.client.QueryPatientResponse;
import eu.europa.ec.sante.openncp.core.client.RetrieveDocumentRequest;
import eu.europa.ec.sante.openncp.core.client.RetrieveDocumentResponse;
import eu.europa.ec.sante.openncp.core.client.SubmitDocumentRequest;
import eu.europa.ec.sante.openncp.core.client.SubmitDocumentResponse;
import eu.europa.ec.sante.openncp.core.client.ihe.cxf.interceptor.AssertionsInInterceptor;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.SubmitDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.DocumentDts;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.FilterParamsDts;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.GenericDocumentCodeDts;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.PatientDemographicsDts;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.PatientIdDts;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.RetrieveDocumentResponseDts;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.XdsDocumentDts;
import eu.europa.ec.sante.openncp.core.client.ihe.service.DispensationService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.IdentificationService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.OrCDService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.OrderService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.PatientService;
import eu.europa.ec.sante.openncp.core.client.ihe.xdr.XdrResponse;
import eu.europa.ec.sante.openncp.core.client.logging.LoggingSlf4j;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.constants.ihe.IheConstants;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.QueryResponse;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import eu.europa.ec.sante.openncp.core.common.exception.NoPatientIdDiscoveredException;
import eu.europa.ec.sante.openncp.core.common.exception.XCAException;
import eu.europa.ec.sante.openncp.core.common.exception.XDRException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ClientServiceImpl implements ClientService {

    private static final String UNSUPPORTED_CLASS_CODE_SCHEME_EXCEPTION = "Unsupported Class Code scheme: ";
    private static final String UNSUPPORTED_CLASS_CODE_EXCEPTION = "Unsupported Class Code: ";

    private final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private ObjectFactory objectFactory = new ObjectFactory();

    public ClientServiceImpl() {

    }


    @Override
    public String submitDocument(final SubmitDocumentOperation submitDocumentOperation) {
        Validate.notNull(submitDocumentOperation);

        final String methodName = "submitDocument";
        LoggingSlf4j.start(logger, methodName);
        SubmitDocumentResponse submitDocumentResponse = objectFactory.createSubmitDocumentResponse();
        try {
            /*  create XDR request */
            final SubmitDocumentRequest submitDocumentRequest = submitDocumentOperation.getRequest();
            final EpsosDocument submitDocument = submitDocumentRequest.getDocument();
            final PatientDemographics patientDemographics = submitDocumentRequest.getPatientDemographics();
            final String countryCode = submitDocumentRequest.getCountryCode();
            final GenericDocumentCode classCode = submitDocument.getClassCode();
            final Map<AssertionEnum, Assertion> assertionMap = submitDocumentOperation.getAssertions();
            if (!classCode.getSchema().equals(IheConstants.CLASSCODE_SCHEME)) {
                throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_SCHEME_EXCEPTION + classCode.getSchema());
            }
            String classCodeNode = classCode.getNodeRepresentation();
            String nodeRepresentation = submitDocument.getFormatCode().getNodeRepresentation();
            logger.info("[Document] ClassCode: '{}' NodeRepresentation: '{}'", classCodeNode, nodeRepresentation);
            //TODO: CDA as input needs to be validated according XSD, Schematron or Validators.
            XdrResponse response;
            var classCodeValue = ClassCode.getByCode(classCodeNode);
            switch (classCodeValue) {

                case ED_CLASSCODE:
                    if (StringUtils.equals(nodeRepresentation, "urn:eHDSI:ed:discard:2020")) {
                        response = DispensationService.discard(submitDocument, patientDemographics, countryCode, assertionMap);
                    } else {
                        response = DispensationService.initialize(submitDocument, patientDemographics, countryCode, assertionMap);
                    }
                    break;
                case EDD_CLASSCODE:
                    response = DispensationService.discard(submitDocument, patientDemographics, countryCode, assertionMap);
                    break;
                default:
                    throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_EXCEPTION + classCodeNode);
            }
            submitDocumentResponse.setResponseStatus(response.getResponseStatus());

        } catch (XDRException | ParseException | RuntimeException ex) {
            LoggingSlf4j.error(logger, methodName, ex);
            throw new ClientConnectorException(ex);
        }
        LoggingSlf4j.end(logger, methodName);
        return submitDocumentResponse.getResponseStatus();
    }
//
//    @Override
//    public List<EpsosDocument> queryDocuments(QueryDocumentRequest submitDocumentOperation.getRequest())
//
//    {
//        final String methodName = "queryDocuments";
//        LoggingSlf4j.start(logger, methodName);
//        QueryDocumentsResponse queryDocumentsResponse = objectFactory.createQueryDocumentsResponse();
//        try {
//            final PatientId patientId = submitDocumentOperation.getRequest().getPatientId();
//            final String countryCode = submitDocumentOperation.getRequest().getCountryCode();
//            final List<GenericDocumentCode> documentCodes = submitDocumentOperation.getRequest().getClassCode();
//            final FilterParams filterParams = submitDocumentOperation.getRequest().getFilterParams();
//            final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
//
//            if (OpenNCPValidation.isValidationEnable()) {
//                OpenNCPValidation.validateHCPAssertion(assertionMap.get(AssertionEnum.CLINICIAN), NcpSide.NCP_B);
//            }
//
//            QueryResponse response;
//            if (documentCodes.size() == 1) {
//                String classCode = documentCodes.get(0).getValue();
//                switch (ClassCode.getByCode(classCode)) {
//                    case PS_CLASSCODE:
//                        response = PatientService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes.get(0)), assertionMap);
//                        break;
//                    case EP_CLASSCODE:
//                        response = OrderService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes.get(0)), assertionMap);
//                        break;
//                    case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
//                    case ORCD_LABORATORY_RESULTS_CLASSCODE:
//                    case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
//                    case ORCD_MEDICAL_IMAGES_CLASSCODE:
//                        response = OrCDService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes), FilterParamsDts.newInstance(filterParams), assertionMap);
//                        break;
//                    default:
//                        throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_EXCEPTION + Arrays.toString(documentCodes.toArray()));
//                }
//            } else {
//                if (!documentCodes.contains(ClassCode.EP_CLASSCODE.getCode())
//                        && !documentCodes.contains(ClassCode.PS_CLASSCODE.getCode())) {
//                    response = OrCDService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes), FilterParamsDts.newInstance(filterParams), assertionMap);
//                } else {
//                    throw new ClientConnectorException("Invalid combination of document codes provided: only OrCD document codes can be combined.");
//                }
//            }
//
//
//            if (response.getDocumentAssociations() != null && !response.getDocumentAssociations().isEmpty()) {
//                queryDocumentsResponse.getReturn().addAll(Arrays.asList(DocumentDts.newInstance(response.getDocumentAssociations())));
//            }
//        } catch (XCAException | RuntimeException ex) {
//            LoggingSlf4j.error(logger, methodName, ex);
//            throw new ClientConnectorException(ex);
//        }
//        LoggingSlf4j.end(logger, methodName);
//        return queryDocumentsResponse.getReturn();
//    }
//
//    @Override
//    public EpsosDocument retrieveDocument(RetrieveDocumentRequest submitDocumentOperation.getRequest())
//
//    {
//
//        final String methodName = "retrieveDocument";
//        LoggingSlf4j.start(logger, methodName);
//        RetrieveDocumentResponse retrieveDocumentResponse;
//        try {
//            final String countryCode = submitDocumentOperation.getRequest().getCountryCode();
//            final DocumentId documentId = submitDocumentOperation.getRequest().getDocumentId();
//            final String homeCommunityId = submitDocumentOperation.getRequest().getHomeCommunityId();
//            final String targetLanguage = submitDocumentOperation.getRequest().getTargetLanguage();
//
//            GenericDocumentCode genericDocumentCode = submitDocumentOperation.getRequest().getClassCode();
//            eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode documentCode = GenericDocumentCodeDts.newInstance(genericDocumentCode);
//
//            if (!documentCode.getSchema().equals(IheConstants.CLASSCODE_SCHEME)) {
//                throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_SCHEME_EXCEPTION + documentCode.getSchema());
//            }
//
//            var xdsDocument = XdsDocumentDts.newInstance(documentId);
//            xdsDocument.setClassCode(documentCode);
//
//            logger.info("[ClientConnector retrieveDocument()] homeCommunityId: '{}' targetLanguage: '{}'", homeCommunityId, targetLanguage);
//            ClassCode classCode = ClassCode.getByCode(documentCode.getValue());
//            final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
//            RetrieveDocumentSetResponseType.DocumentResponse documentResponse;
//            switch (classCode) {
//                case PS_CLASSCODE:
//                    documentResponse = PatientService.retrieve(xdsDocument, homeCommunityId, countryCode, targetLanguage,
//                            assertionMap);
//                    break;
//                case EP_CLASSCODE:
//                    documentResponse = OrderService.retrieve(xdsDocument, homeCommunityId, countryCode, targetLanguage,
//                            assertionMap);
//                    break;
//                case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
//                case ORCD_LABORATORY_RESULTS_CLASSCODE:
//                case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
//                case ORCD_MEDICAL_IMAGES_CLASSCODE:
//                    documentResponse = OrCDService.retrieve(xdsDocument, homeCommunityId, countryCode, targetLanguage,
//                            assertionMap);
//                    break;
//                default:
//                    throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_EXCEPTION + documentCode.getValue());
//            }
//
//
//            retrieveDocumentResponse = RetrieveDocumentResponseDts.newInstance(documentResponse);
//
//        } catch (XCAException | RuntimeException ex) {
//            LoggingSlf4j.error(logger, methodName, ex);
//            throw new ClientConnectorException(ex);
//        }
//        return retrieveDocumentResponse.getReturn();
//    }
//
//    @Override
//    public List<PatientDemographics> queryPatient(QueryPatientRequest submitDocumentOperation.getRequest())
//
//    {
//        final var methodName = "queryPatient";
//        LoggingSlf4j.start(logger, methodName);
//        QueryPatientResponse queryPatientResponse = objectFactory.createQueryPatientResponse();
//
//        try {
//            final PatientDemographics patientDemographics = submitDocumentOperation.getRequest().getPatientDemographics();
//            final String countryCode = submitDocumentOperation.getRequest().getCountryCode();
//            final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
//
//            List<eu.europa.ec.sante.openncp.core.common.datamodel.PatientDemographics> patientDemographicsList = IdentificationService.findIdentityByTraits(PatientDemographicsDts.toDataModel(patientDemographics), assertionMap, countryCode);
//
//            //  Response
//            List<PatientDemographics> returnedPatientDemographics = PatientDemographicsDts.fromDataModel(patientDemographicsList);
//            queryPatientResponse.getReturn().addAll(returnedPatientDemographics);
//
//        } catch (NoPatientIdDiscoveredException | ParseException | RuntimeException ex) {
//            LoggingSlf4j.error(logger, methodName, ex);
//            throw new ClientConnectorException(ex);
//        }
//        LoggingSlf4j.end(logger, methodName);
//        return queryPatientResponse.getReturn();
//    }
//
//    @Override
//    public String sayHello(String submitDocumentOperation.getRequest())
//
//    {
//        return "sayHello";
//    }
}
