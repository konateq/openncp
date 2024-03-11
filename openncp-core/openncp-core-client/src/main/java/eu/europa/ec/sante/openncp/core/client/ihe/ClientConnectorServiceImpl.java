package eu.europa.ec.sante.openncp.core.client.ihe;

import eu.europa.ec.sante.openncp.core.client.*;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

import javax.xml.ws.Service;
import java.util.List;
import java.util.Map;

public class ClientConnectorServiceImpl  implements ClientConnectorServicePortType{

    final ClientConnectorServicePortType port;

    public ClientConnectorServiceImpl() {
        Service service = ClientConnectorService.create(ClientConnectorService.SERVICE);
        port = service.getPort(ClientConnectorServicePortType.class);
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
        Client client = ClientProxy.getClient(port);
        Map<String, Object> requestContext = client.getRequestContext();
        Endpoint endpoint = client.getEndpoint();
        List<Interceptor<? extends Message>> inInterceptors = endpoint.getInInterceptors();
        ClientConnectorSoapHeaderInterceptor clientConnectorSoapHeaderInterceptor = new ClientConnectorSoapHeaderInterceptor();
        inInterceptors.add(clientConnectorSoapHeaderInterceptor);

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
