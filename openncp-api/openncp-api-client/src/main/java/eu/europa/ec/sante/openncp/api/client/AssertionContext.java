package eu.europa.ec.sante.openncp.api.client;

import java.util.Map;

import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.immutables.value.Value.Immutable;
import org.opensaml.saml.saml2.core.Assertion;

@Immutable
public interface AssertionContext {

    Map<AssertionEnum, Assertion> getAssertions();
}
