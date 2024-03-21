package eu.europa.ec.sante.openncp.application.client.connector;

import java.util.Map;
import javax.xml.ws.BindingProvider;

import eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.opensaml.saml.saml2.core.Assertion;

public class ClientConnectorServicePortTypeWrapper {

    public static final String REQUESTCONTEXT_ASSERTIONS_KEY = "oncp.assertions";
    private final ClientConnectorServicePortType clientConnectorServicePortType;

    public ClientConnectorServicePortTypeWrapper(final ClientConnectorServicePortType clientConnectorServicePortType) {
        this.clientConnectorServicePortType = clientConnectorServicePortType;
    }

    public void setAssertions(final Map<AssertionEnum, Assertion> assertions) {
        getRequestContext().put(REQUESTCONTEXT_ASSERTIONS_KEY, assertions);
    }

    public void getAssertions() {
        getRequestContext().get(REQUESTCONTEXT_ASSERTIONS_KEY);
    }

    public ClientConnectorServicePortType getClientConnectorServicePortType() {
        return clientConnectorServicePortType;
    }

    public BindingProvider getBindingProvider() {
        return (BindingProvider) getClientConnectorServicePortType();
    }

    public Map<String, Object> getRequestContext() {
        return getBindingProvider().getRequestContext();
    }
}
