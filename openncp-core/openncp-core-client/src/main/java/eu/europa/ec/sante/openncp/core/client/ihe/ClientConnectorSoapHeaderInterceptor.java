package eu.europa.ec.sante.openncp.core.client.ihe;

import eu.europa.ec.sante.openncp.core.common.assertionvalidator.saml.SAML2Validator;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.handler.soap.SOAPHandlerInterceptor;
import org.opensaml.saml.saml2.core.Assertion;

import javax.xml.ws.Binding;
import java.util.List;

public class ClientConnectorSoapHeaderInterceptor extends SOAPHandlerInterceptor {

    public ClientConnectorSoapHeaderInterceptor(Binding binding) {
        super(binding);
    }
}
