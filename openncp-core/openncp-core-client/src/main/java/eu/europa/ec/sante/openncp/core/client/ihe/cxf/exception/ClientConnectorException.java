package eu.europa.ec.sante.openncp.core.client.ihe.cxf.exception;

import javax.xml.ws.WebFault;

@WebFault(name = "ClientConnectorFault")
public class ClientConnectorException extends Exception {

    public ClientConnectorException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
