package eu.europa.ec.sante.openncp.api.client;


import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.constants.AssertionEnum;
import org.immutables.value.Value.Immutable;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.Map;

@Immutable
public interface AssertionContext {

    Map<AssertionEnum, Assertion> getAssertions();
}
