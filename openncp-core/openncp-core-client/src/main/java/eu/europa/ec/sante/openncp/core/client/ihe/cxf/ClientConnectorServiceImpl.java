package eu.europa.ec.sante.openncp.core.client.ihe.cxf;

import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.validation.OpenNCPValidation;
import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.client.exception.ClientConnectorException;
import eu.europa.ec.sante.openncp.core.client.ihe.cxf.interceptor.AssertionsInInterceptor;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.GenericDocumentCodeDts;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.PatientIdDts;
import eu.europa.ec.sante.openncp.core.client.ihe.service.OrCDService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.OrderService;
import eu.europa.ec.sante.openncp.core.client.ihe.service.PatientService;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.QueryResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
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
        Endpoint endpoint = client.getEndpoint();
        List<Interceptor<? extends Message>> inInterceptors = endpoint.getInInterceptors();
        AssertionsInInterceptor assertionsInInterceptor = new AssertionsInInterceptor();
        inInterceptors.add(assertionsInInterceptor);
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
                    response = PatientService.list(PatientIdDts.newInstance(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes.get(0)), assertionMap);
                    break;
                case EP_CLASSCODE:
                    response = OrderService.list(PatientIdDts.newInstance(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes.get(0)), assertionMap);
                    break;
                case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                case ORCD_LABORATORY_RESULTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGES_CLASSCODE:
                    response = OrCDService.list(PatientIdDts.newInstance(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes), filterParams, assertionMap);
                    break;
                default:
                    throw new ClientConnectorException(UNSUPPORTED_CLASS_CODE_EXCEPTION + Arrays.toString(documentCodes.toArray()));
            }
        } else {
            if (!documentCodes.contains(ClassCode.EP_CLASSCODE.getCode())
                    && !documentCodes.contains(ClassCode.PS_CLASSCODE.getCode())) {
                response = OrCDService.list(PatientIdDts.newInstance(patientId), countryCode, GenericDocumentCodeDts.newInstance(documentCodes), filterParams, assertionMap);
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
}
