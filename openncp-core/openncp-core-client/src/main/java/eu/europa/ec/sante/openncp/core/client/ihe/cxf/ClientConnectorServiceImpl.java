package eu.europa.ec.sante.openncp.core.client.ihe.cxf;

import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.client.exception.ClientConnectorException;
import eu.europa.ec.sante.openncp.core.client.ihe.ClientConnectorServiceMessageReceiverInOut;
import eu.europa.ec.sante.openncp.core.client.ihe.service.OrCDService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.OrderService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.PatientService;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.saml.SAML2Validator;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.QueryResponse;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.xml.ws.Service;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ClientConnectorServiceImpl  implements ClientConnectorServicePortType{

    private static final String UNSUPPORTED_CLASS_CODE_SCHEME_EXCEPTION = "Unsupported Class Code scheme: ";
    private static final String UNSUPPORTED_CLASS_CODE_EXCEPTION = "Unsupported Class Code: ";

    private final Logger logger = LoggerFactory.getLogger(ClientConnectorServiceImpl.class);

    final ClientConnectorServicePortType port;

    final Client client;

    public ClientConnectorServiceImpl() {
        Service service = ClientConnectorService.create(ClientConnectorService.SERVICE);
        port = service.getPort(ClientConnectorServicePortType.class);
        client = ClientProxy.getClient(port);
    }


    @Override
    public String submitDocument(SubmitDocumentRequest arg0) {
        return null;
    }

    @Override
    public String sayHello(String arg0) {
        return null;
    }

    @Override
    public List<EpsosDocument> queryDocuments(QueryDocumentRequest arg0) {


        Element soapHeader = XMLUtils.toDOM(requestSoapEnvelope.getHeader());
        List<Assertion> assertions = SAML2Validator.getAssertions(soapHeader);
        Map<AssertionEnum, Assertion> assertionMap = processAssertionList(assertions);

        PatientId patientId = arg0.getPatientId();
        String countryCode = arg0.getCountryCode();
        List<GenericDocumentCode> documentCodes = arg0.getClassCode();
        FilterParams filterParams = arg0.getFilterParams();

        //Populate assertionMap

        QueryResponse response;
        if (documentCodes.size() == 1) {
            String classCode = documentCodes.get(0).getValue();
            switch (ClassCode.getByCode(classCode)) {
                case PS_CLASSCODE:
                    response = PatientService.list(patientId, countryCode, documentCodes.get(0), assertionMap);
                    break;
                case EP_CLASSCODE:
                    response = OrderService.list(patientId, countryCode, documentCodes.get(0), assertionMap);
                    break;
                case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                case ORCD_LABORATORY_RESULTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGES_CLASSCODE:
                    response = OrCDService.list(patientId, countryCode, documentCodes, filterParams, assertionMap);
                    break;
                default:
                    throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_EXCEPTION + Arrays.toString(documentCodes.toArray()));
            }
        } else {
            if (!documentCodes.contains(ClassCode.EP_CLASSCODE.getCode())
                    && !documentCodes.contains(ClassCode.PS_CLASSCODE.getCode())) {
                response = OrCDService.list(patientId, countryCode, documentCodes, filterParams, assertionMap);
            } else {
                throw new ClientConnectorException("Invalid combination of document codes provided: only OrCD document codes can be combined.");
            }
        }
        return response;
    }

    @Override
    public EpsosDocument retrieveDocument(RetrieveDocumentRequest arg0) {
        return null;
    }

    @Override
    public List<PatientDemographics> queryPatient(QueryPatientRequest arg0) {
        return null;
    }

    private Map<AssertionEnum, Assertion> processAssertionList(List<Assertion> assertionList) {

        logger.info("[ClientConnector] Processing Assertions list from SOAP Header:");
        Map<AssertionEnum, Assertion> assertionEnumMap = new EnumMap<>(AssertionEnum.class);
        for (Assertion assertion : assertionList) {
            switch (assertion.getIssuer().getNameQualifier()) {
                case "urn:ehdsi:assertions:hcp":
                    assertionEnumMap.put(AssertionEnum.CLINICIAN, assertion);
                    break;
                case "urn:ehdsi:assertions:nok":
                    assertionEnumMap.put(AssertionEnum.NEXT_OF_KIN, assertion);
                    break;
                case "urn:ehdsi:assertions:trc":
                    assertionEnumMap.put(AssertionEnum.TREATMENT, assertion);
                    break;
            }
        }
        return assertionEnumMap;
    }
}
