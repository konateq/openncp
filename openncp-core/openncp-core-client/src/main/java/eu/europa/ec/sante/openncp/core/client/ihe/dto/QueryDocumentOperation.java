package eu.europa.ec.sante.openncp.core.client.ihe.dto;

import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.immutables.value.Value;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.Map;

@Value.Immutable
public interface QueryDocumentOperation {

    Map<AssertionEnum, Assertion> getAssertions();
    eu.europa.ec.sante.openncp.core.client.QueryDocumentRequest getRequest();
}
