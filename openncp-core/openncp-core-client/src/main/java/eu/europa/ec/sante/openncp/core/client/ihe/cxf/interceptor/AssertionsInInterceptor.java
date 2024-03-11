package eu.europa.ec.sante.openncp.core.client.ihe.cxf.interceptor;

import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.saml.SAML2Validator;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AssertionsInInterceptor extends AbstractPhaseInterceptor<Message> {

    private final Logger logger = LoggerFactory.getLogger(AssertionsInInterceptor.class);
    public AssertionsInInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {

        try {
            final SOAPMessage soapMessage = message.getContent(SOAPMessage.class);
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            soapHeader.getOwnerDocument().getDocumentElement();
            List<Assertion> assertions = SAML2Validator.getAssertions(soapHeader);
            Map<AssertionEnum, Assertion> assertionMap = processAssertionList(assertions);
            message.getExchange().put("assertionMap", assertionMap);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new Fault(e);
        }
    }

    public void handleFault(Message messageParam) {
    }

    private Map<AssertionEnum, Assertion> processAssertionList(List<Assertion> assertionList) {

        logger.info("[ClientConnector] Processing Assertions list from SOAP Header:");
        Map<AssertionEnum, Assertion> assertionEnumMap = new EnumMap<>(AssertionEnum.class);
        for (Assertion assertion : assertionList) {
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
}
