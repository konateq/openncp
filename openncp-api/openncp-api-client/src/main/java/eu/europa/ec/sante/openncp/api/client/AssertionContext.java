package eu.europa.ec.sante.openncp.api.client;


import eu.europa.ec.sante.openncp.common.immutables.Domain;
import eu.europa.ec.sante.openncp.core.client.api.AssertionEnum;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.Map;

@Domain
public interface AssertionContext {

    Map<AssertionEnum, Assertion> getAssertions();
}
