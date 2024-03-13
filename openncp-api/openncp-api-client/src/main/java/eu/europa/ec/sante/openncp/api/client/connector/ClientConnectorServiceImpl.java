package eu.europa.ec.sante.openncp.api.client.connector;

import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.client.ihe.ClientService;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.*;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableQueryPatientOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableRetrieveDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableSubmitDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableQueryDocumentOperation;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@javax.jws.WebService(
        serviceName = "ClientConnectorService",
        portName = "ClientConnectorServicePort",
        targetNamespace = "http://client.core.openncp.sante.ec.europa.eu",
        wsdlLocation = "classpath:wsdl/ClientConnectorService.wsdl",
        endpointInterface = "eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType")
public class ClientConnectorServiceImpl implements ClientConnectorServicePortType {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnectorServiceImpl.class);

    private ClientService clientService;


    public ClientConnectorServiceImpl(final ClientService clientService) {
        this.clientService = Validate.notNull(clientService);
    }


    @Override
    public String submitDocument(final SubmitDocumentRequest submitDocumentRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
        final SubmitDocumentOperation submitDocumentOperation = ImmutableSubmitDocumentOperation.builder()
                .assertions(assertionMap)
                .request(submitDocumentRequest)
                .build();
        return clientService.submitDocument(submitDocumentOperation);
    }

    @Override
    public List<EpsosDocument> queryDocuments(QueryDocumentRequest queryDocumentRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
        final QueryDocumentOperation queryDocumentOperation = ImmutableQueryDocumentOperation.builder()
                .assertions(assertionMap)
                .request(queryDocumentRequest)
                .build();
        return clientService.queryDocuments(queryDocumentOperation);
    }

    @Override
    public EpsosDocument retrieveDocument(RetrieveDocumentRequest retrieveDocumentRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
        final RetrieveDocumentOperation retrieveDocumentOperation = ImmutableRetrieveDocumentOperation.builder()
                .assertions(assertionMap)
                .request(retrieveDocumentRequest)
                .build();
        return clientService.retrieveDocument(retrieveDocumentOperation);
    }

    @Override
    public List<PatientDemographics> queryPatient(QueryPatientRequest queryPatientRequest) {
        final Map<AssertionEnum, Assertion> assertionMap = (Map<AssertionEnum, Assertion>) PhaseInterceptorChain.getCurrentMessage().getExchange().get("assertionMap");
        final QueryPatientOperation queryPatientOperation = ImmutableQueryPatientOperation.builder()
                .assertions(assertionMap)
                .request(queryPatientRequest)
                .build();
        return clientService.queryPatient(queryPatientOperation);
    }

    @Override
    public String sayHello(String name) {
        return clientService.sayHello(name);
    }
}
