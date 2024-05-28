package eu.europa.ec.sante.openncp.application.client.connector.assertion;

import org.opensaml.saml.saml2.core.Assertion;

public interface AssertionService {
    Assertion request(AssertionRequest assertionRequest) throws STSClientException;
}
