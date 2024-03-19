package eu.europa.ec.sante.openncp.api.client.connector;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;

import eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType;
import eu.europa.ec.sante.openncp.core.client.EpsosDocument;
import eu.europa.ec.sante.openncp.core.client.PatientDemographics;
import eu.europa.ec.sante.openncp.core.client.QueryDocumentRequest;
import eu.europa.ec.sante.openncp.core.client.QueryPatientRequest;
import eu.europa.ec.sante.openncp.core.client.RetrieveDocumentRequest;
import eu.europa.ec.sante.openncp.core.client.SubmitDocumentRequest;
import eu.europa.ec.sante.openncp.core.client.ihe.ClientService;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableQueryDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableQueryPatientOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableRetrieveDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableSubmitDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.QueryDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.QueryPatientOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.RetrieveDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.SubmitDocumentOperation;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@WebService(serviceName = "ClientConnectorService", portName = "ClientConnectorServicePort",
            targetNamespace = "http://client.core.openncp.sante.ec.europa.eu", wsdlLocation = "classpath:wsdl/ClientConnectorService.wsdl",
            endpointInterface = "eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType")
@Service
public class ClientConnectorServiceImpl implements ClientConnectorServicePortType {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnectorServiceImpl.class);

    private final ClientService clientService;

    public ClientConnectorServiceImpl(final ClientService clientService) {
        this.clientService = Validate.notNull(clientService);
    }

    @Override
    public String submitDocument(final SubmitDocumentRequest submitDocumentRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage()
                                                                                                                .getExchange()
                                                                                                                .get("assertionMap");
        final SubmitDocumentOperation submitDocumentOperation = ImmutableSubmitDocumentOperation.builder()
                                                                                                .assertions(assertionMap)
                                                                                                .request(submitDocumentRequest)
                                                                                                .build();
        return clientService.submitDocument(submitDocumentOperation);
    }

    @Override
    public List<EpsosDocument> queryDocuments(final QueryDocumentRequest queryDocumentRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage()
                                                                                                                .getExchange()
                                                                                                                .get("assertionMap");
        final QueryDocumentOperation queryDocumentOperation = ImmutableQueryDocumentOperation.builder()
                                                                                             .assertions(assertionMap)
                                                                                             .request(queryDocumentRequest)
                                                                                             .build();
        return clientService.queryDocuments(queryDocumentOperation);
    }

    @Override
    public EpsosDocument retrieveDocument(final RetrieveDocumentRequest retrieveDocumentRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage()
                                                                                                                .getExchange()
                                                                                                                .get("assertionMap");
        final RetrieveDocumentOperation retrieveDocumentOperation = ImmutableRetrieveDocumentOperation.builder()
                                                                                                      .assertions(assertionMap)
                                                                                                      .request(retrieveDocumentRequest)
                                                                                                      .build();
        return clientService.retrieveDocument(retrieveDocumentOperation);
    }

    @Override
    public List<PatientDemographics> queryPatient(final QueryPatientRequest queryPatientRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage()
                                                                                                                .getExchange()
                                                                                                                .get("assertionMap");
        final QueryPatientOperation queryPatientOperation = ImmutableQueryPatientOperation.builder()
                                                                                          .assertions(assertionMap)
                                                                                          .request(queryPatientRequest)
                                                                                          .build();
        return clientService.queryPatient(queryPatientOperation);
    }

    @Override
    public String sayHello(final String name) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage()
                                                                                                                .getExchange()
                                                                                                                .get("assertionMap");
        final AssertionInfoMap assertionInfoMap = PhaseInterceptorChain.getCurrentMessage().get(AssertionInfoMap.class);

        return clientService.sayHello(name);
    }
}
