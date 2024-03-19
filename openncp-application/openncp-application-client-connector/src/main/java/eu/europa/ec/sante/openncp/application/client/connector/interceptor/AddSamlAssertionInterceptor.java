package eu.europa.ec.sante.openncp.application.client.connector.interceptor;

import java.util.Map;

import eu.europa.ec.sante.openncp.application.client.connector.ClientConnectorException;
import eu.europa.ec.sante.openncp.application.client.connector.ClientConnectorServicePortTypeWrapper;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.soap.wssecurity.Security;
import org.opensaml.soap.wssecurity.impl.SecurityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Adding SOAP header with SAML assertion to request.
 */
public class AddSamlAssertionInterceptor extends AbstractSoapInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddSamlAssertionInterceptor.class);

    static {
        try {
            InitializationService.initialize();
        } catch (final InitializationException e) {
            LOGGER.error("InitializationException: '{}'", e.getMessage());
        }
    }

    public AddSamlAssertionInterceptor() {
        super(Phase.PREPARE_SEND);
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        try {

            final SecurityBuilder securityBuilder = new SecurityBuilder();
            final Security security = securityBuilder.buildObject();

            final Map<AssertionEnum, Assertion> assertions = (Map<AssertionEnum, Assertion>) message.get(
                    ClientConnectorServicePortTypeWrapper.REQUESTCONTEXT_ASSERTIONS_KEY);
            assertions.forEach((assertionEnum, assertion) -> {
                security.getUnknownXMLObjects().add(assertion);
            });

            final Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(security);
            final Element marshalledSecurity = marshaller.marshall(security);

            final SoapHeader header = new SoapHeader(security.getElementQName(), marshalledSecurity);
            header.setMustUnderstand(false);
            message.getHeaders().add(header);
        } catch (final Exception e) {
            throw new ClientConnectorException("Error adding the assertions to the security header", e);
        }
    }
}
