package eu.europa.ec.sante.openncp.application.client.connector.assertions;

import org.opensaml.saml.saml2.core.Assertion;

public interface AssertionService {

    Assertion request() throws STSClientException;
}
