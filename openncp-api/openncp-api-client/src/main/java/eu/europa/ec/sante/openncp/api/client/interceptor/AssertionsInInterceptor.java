package eu.europa.ec.sante.openncp.api.client.interceptor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.namespace.QName;

import eu.europa.ec.sante.openncp.api.client.AssertionContextProvider;
import eu.europa.ec.sante.openncp.api.client.ImmutableAssertionContext;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.common.security.util.AssertionUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.wss4j.common.WSS4JConstants;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class AssertionsInInterceptor extends AbstractSoapInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AssertionsInInterceptor.class);

    public AssertionsInInterceptor() {
        super(Phase.PRE_INVOKE);
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
                default:
                    break;
            }
        }
        return assertionEnumMap;
    }

    @Override
    public void handleMessage(final SoapMessage soapMessage) throws Fault {
        Header securityHeader = null;
        for (final Header header : soapMessage.getHeaders()) {
            final QName n = header.getName();
            if ("Security".equals(n.getLocalPart()) &&
                (n.getNamespaceURI().equals(WSS4JConstants.WSSE_NS) || n.getNamespaceURI().equals(WSS4JConstants.WSSE11_NS))) {
                securityHeader = header;
            }
        }

        if (securityHeader == null) {
            throw new RuntimeException("No security header found");
        }

        final List<Assertion> assertions = new ArrayList<>();
        final Element el = (Element) securityHeader.getObject();
        Element child = DOMUtils.getFirstElement(el);
        while (child != null) {
            toAssertion(child).ifPresent(assertions::add);
            child = DOMUtils.getNextElement(child);
        }

        final Map<AssertionEnum, Assertion> assertionEnumAssertionMap = processAssertionList(assertions);
        AssertionContextProvider.setAssertionContext(ImmutableAssertionContext.builder().putAllAssertions(assertionEnumAssertionMap).build());
    }

    private Optional<Assertion> toAssertion(final Element element) {
        if ("Assertion".equals(element.getLocalName()) &&
            (WSS4JConstants.SAML_NS.equals(element.getNamespaceURI()) || WSS4JConstants.SAML2_NS.equals(element.getNamespaceURI()))) {
            return AssertionUtil.toAssertion(element);
        } else {
            return Optional.empty();
        }
    }
}
