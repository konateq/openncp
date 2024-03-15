package eu.europa.ec.sante.openncp.api.client.interceptor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssertionsInInterceptor extends AbstractSoapInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AssertionsInInterceptor.class);

    public AssertionsInInterceptor() {
        super(Phase.RECEIVE);
    }

    private Map<AssertionEnum, Assertion> processAssertionList(final List<Assertion> assertionList) {

        logger.info("[ClientConnector] Processing Assertions list from SOAP Header:");
        final Map<AssertionEnum, Assertion> assertionEnumMap = new EnumMap<>(AssertionEnum.class);
        for (final Assertion assertion : assertionList) {
            switch (assertion.getIssuer().getNameQualifier()) {
                case "urn:ehdsi:assertions:hcp":
                    assertionEnumMap.put(AssertionEnum.CLINICIAN, assertion);
                    break;
                case "urn:ehdsi:assertions:nok":
                    assertionEnumMap.put(AssertionEnum.NEXT_OF_KIN, assertion);
                    break;
                case "urn:ehdsi:assertions:trc":
                    assertionEnumMap.put(AssertionEnum.TREATMENT, assertion);
                    break;
            }
        }
        return assertionEnumMap;
    }

    @Override
    public void handleMessage(final SoapMessage soapMessage) throws Fault {
        try {
            final Map<String, String> envelopeNs = soapMessage.getEnvelopeNs();
            final AssertionInfoMap assertionInfoMap = soapMessage.get(AssertionInfoMap.class);

            final List<Assertion> assertions = new ArrayList<>();

            // Check if the message contains a SAML token principal

            // If not, manually parse the headers
            final List<Header> headers = soapMessage.getHeaders();

            for (final Header header : headers) {
                final QName name = header.getName();
                // Check if this header is a WS-Security header containing a SAML Assertion
                System.out.println("catch");
            }

            final SecurityContext sc = soapMessage.get(SecurityContext.class);

            //            soapHeader.getOwnerDocument().getDocumentElement();
            //            final List<Assertion> assertions = SAML2Validator.getAssertions(soapHeader);
            //            final Map<AssertionEnum, Assertion> assertionMap = processAssertionList(assertions);
            //            message.getExchange().put("assertionMap", assertionMap);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new Fault(e);
        }
    }
}
