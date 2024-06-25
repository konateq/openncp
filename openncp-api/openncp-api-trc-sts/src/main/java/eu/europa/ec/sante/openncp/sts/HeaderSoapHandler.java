package eu.europa.ec.sante.openncp.sts;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import java.util.HashSet;
import java.util.Set;

public class HeaderSoapHandler implements SOAPHandler {

    @Override
    public Set<QName> getHeaders() {
        QName securityHeader = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                "Security");
        HashSet<QName> headers = new HashSet<>();
        headers.add(securityHeader);
        return headers;
    }

    @Override
    public boolean handleMessage(MessageContext messageContext) {
        return false;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        return false;
    }

    @Override
    public void close(MessageContext messageContext) {
        // to be implemented
    }
}
