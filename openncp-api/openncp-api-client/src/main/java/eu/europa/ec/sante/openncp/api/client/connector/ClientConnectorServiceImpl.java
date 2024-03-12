package eu.europa.ec.sante.openncp.api.client.connector;

import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.validation.OpenNCPValidation;
import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.client.ihe.cxf.interceptor.AssertionsInInterceptor;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.*;
import eu.europa.ec.sante.openncp.core.client.ihe.service.*;
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
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Service;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class ClientConnectorServiceImpl  implements ClientConnectorServicePortType {

    private static final String UNSUPPORTED_CLASS_CODE_SCHEME_EXCEPTION = "Unsupported Class Code scheme: ";
    private static final String UNSUPPORTED_CLASS_CODE_EXCEPTION = "Unsupported Class Code: ";

    private final Logger logger = LoggerFactory.getLogger(ClientConnectorServiceImpl.class);

    private ObjectFactory objectFactory = new ObjectFactory();

    final ClientConnectorServicePortType port;

    final Client client;

    public ClientConnectorServiceImpl() {
        Service service = ClientConnectorService.create(ClientConnectorService.SERVICE);
        port = service.getPort(ClientConnectorServicePortType.class);
        client = ClientProxy.getClient(port);
        Endpoint endpoint = client.getEndpoint();
        List<Interceptor<? extends Message>> inInterceptors = endpoint.getInInterceptors();
        AssertionsInInterceptor assertionsInInterceptor = new AssertionsInInterceptor();
        inInterceptors.add(assertionsInInterceptor);
    }


    @Override
    public String submitDocument(SubmitDocumentRequest arg0) {
        final String methodName = "submitDocument";
        LoggingSlf4j.start(logger, methodName);
        SubmitDocumentResponse submitDocumentResponse = objectFactory.createSubmitDocumentResponse();
        try {
            /*  create XDR request */
            final EpsosDocument submitDocument = arg0.getDocument();
            final PatientDemographics patientDemographics = arg0.getPatientDemographics();
            final String countryCode = arg0.getCountryCode();
            final GenericDocumentCode classCode = submitDocument.getClassCode();
            final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
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

    @Override
    public List<EpsosDocument> queryDocuments(QueryDocumentRequest arg0) {
        final String methodName = "queryDocuments";
        LoggingSlf4j.start(logger, methodName);
        QueryDocumentsResponse queryDocumentsResponse = objectFactory.createQueryDocumentsResponse();
        try {
            final PatientId patientId = arg0.getPatientId();
            final String countryCode = arg0.getCountryCode();
            final List<GenericDocumentCode> documentCodes = arg0.getClassCode();
            final FilterParams filterParams = arg0.getFilterParams();
            final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");

            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validateHCPAssertion(assertionMap.get(AssertionEnum.CLINICIAN), NcpSide.NCP_B);
            }

            QueryResponse response;
            if (documentCodes.size() == 1) {
                String classCode = documentCodes.get(0).getValue();
                switch (ClassCode.getByCode(classCode)) {
                    case PS_CLASSCODE:
                        response = PatientService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes.get(0)), assertionMap);
                        break;
                    case EP_CLASSCODE:
                        response = OrderService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes.get(0)), assertionMap);
                        break;
                    case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                    case ORCD_LABORATORY_RESULTS_CLASSCODE:
                    case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                    case ORCD_MEDICAL_IMAGES_CLASSCODE:
                        response = OrCDService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes), FilterParamsDts.newInstance(filterParams), assertionMap);
                        break;
                    default:
                        throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_EXCEPTION + Arrays.toString(documentCodes.toArray()));
                }
            } else {
                if (!documentCodes.contains(ClassCode.EP_CLASSCODE.getCode())
                        && !documentCodes.contains(ClassCode.PS_CLASSCODE.getCode())) {
                    response = OrCDService.list(PatientIdDts.toDataModel(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes), FilterParamsDts.newInstance(filterParams), assertionMap);
                } else {
                    throw new ClientConnectorException("Invalid combination of document codes provided: only OrCD document codes can be combined.");
                }
            }


            if (response.getDocumentAssociations() != null && !response.getDocumentAssociations().isEmpty()) {
                queryDocumentsResponse.getReturn().addAll(Arrays.asList(DocumentDts.newInstance(response.getDocumentAssociations())));
            }
        } catch (XCAException | RuntimeException ex) {
            LoggingSlf4j.error(logger, methodName, ex);
            throw new ClientConnectorException(ex);
        }
        LoggingSlf4j.end(logger, methodName);
        return queryDocumentsResponse.getReturn();
    }

    @Override
    public EpsosDocument retrieveDocument(RetrieveDocumentRequest arg0) {

        final String methodName = "retrieveDocument";
        LoggingSlf4j.start(logger, methodName);
        RetrieveDocumentResponse retrieveDocumentResponse;
        try {
            final String countryCode = arg0.getCountryCode();
            final DocumentId documentId = arg0.getDocumentId();
            final String homeCommunityId = arg0.getHomeCommunityId();
            final String targetLanguage = arg0.getTargetLanguage();

            GenericDocumentCode genericDocumentCode = arg0.getClassCode();
            eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode documentCode = GenericDocumentCodeDts.newInstance(genericDocumentCode);

            if (!documentCode.getSchema().equals(IheConstants.CLASSCODE_SCHEME)) {
                throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_SCHEME_EXCEPTION + documentCode.getSchema());
            }

            var xdsDocument = XdsDocumentDts.newInstance(documentId);
            xdsDocument.setClassCode(documentCode);

            logger.info("[ClientConnector retrieveDocument()] homeCommunityId: '{}' targetLanguage: '{}'", homeCommunityId, targetLanguage);
            ClassCode classCode = ClassCode.getByCode(documentCode.getValue());
            final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
            RetrieveDocumentSetResponseType.DocumentResponse documentResponse;
            switch (classCode) {
                case PS_CLASSCODE:
                    documentResponse = PatientService.retrieve(xdsDocument, homeCommunityId, countryCode, targetLanguage,
                            assertionMap);
                    break;
                case EP_CLASSCODE:
                    documentResponse = OrderService.retrieve(xdsDocument, homeCommunityId, countryCode, targetLanguage,
                            assertionMap);
                    break;
                case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                case ORCD_LABORATORY_RESULTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGES_CLASSCODE:
                    documentResponse = OrCDService.retrieve(xdsDocument, homeCommunityId, countryCode, targetLanguage,
                            assertionMap);
                    break;
                default:
                    throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_EXCEPTION + documentCode.getValue());
            }


            retrieveDocumentResponse = RetrieveDocumentResponseDts.newInstance(documentResponse);

        } catch (XCAException | RuntimeException ex) {
            LoggingSlf4j.error(logger, methodName, ex);
            throw new ClientConnectorException(ex);
        }
        return retrieveDocumentResponse.getReturn();
    }

    @Override
    public List<PatientDemographics> queryPatient(QueryPatientRequest arg0) {
        final var methodName = "queryPatient";
        LoggingSlf4j.start(logger, methodName);
        QueryPatientResponse queryPatientResponse = objectFactory.createQueryPatientResponse();

        try {
            final PatientDemographics patientDemographics = arg0.getPatientDemographics();
            final String countryCode = arg0.getCountryCode();
            final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");

            List<eu.europa.ec.sante.openncp.core.common.datamodel.PatientDemographics> patientDemographicsList = IdentificationService.findIdentityByTraits(PatientDemographicsDts.toDataModel(patientDemographics), assertionMap, countryCode);

            //  Response
            List<PatientDemographics> returnedPatientDemographics = PatientDemographicsDts.fromDataModel(patientDemographicsList);
            queryPatientResponse.getReturn().addAll(returnedPatientDemographics);

        } catch (NoPatientIdDiscoveredException | ParseException | RuntimeException ex) {
            LoggingSlf4j.error(logger, methodName, ex);
            throw new ClientConnectorException(ex);
        }
        LoggingSlf4j.end(logger, methodName);
        return queryPatientResponse.getReturn();
    }

    @Override
    public String sayHello(String arg0) {
        return "sayHello";
    }
}
