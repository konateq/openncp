package eu.europa.ec.sante.openncp.trcsts;

import com.message.schemas.message.MessageBody;
import https.ehdsi_eu.ISecurityTokenService;
import org.springframework.stereotype.Service;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.jws.WebService;


@WebService(serviceName = "SecurityTokenService", portName = "ISecurityTokenService_Port",
        targetNamespace = "https://ehdsi.eu")
public class STSEndpoint implements ISecurityTokenService {
    private static final String NAMESPACE_URI = "https://ehdsi.eu/";

    @Override
    public MessageBody issueToken(MessageBody rstMessage) {
        return null;
    }
}
