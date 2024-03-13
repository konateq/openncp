package eu.europa.ec.sante.openncp.api.client.connector;

import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.validation.OpenNCPValidation;
import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.client.ihe.ClientService;
import eu.europa.ec.sante.openncp.core.client.ihe.ClientServiceImpl;
import eu.europa.ec.sante.openncp.core.client.ihe.cxf.interceptor.AssertionsInInterceptor;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.ImmutableSubmitDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.SubmitDocumentOperation;
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
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.HandlerChain;
import javax.xml.ws.Service;
import java.text.ParseException;
import java.util.Arrays;
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
    public String sayHello(String name) {
        return "Hello " + name;
    }

    @Override
    public List<EpsosDocument> queryDocuments(QueryDocumentRequest arg0) {
        return null;
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
