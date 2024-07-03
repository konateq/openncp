package eu.europa.ec.sante.openncp.core.client.ihe.dto;

import eu.europa.ec.sante.openncp.core.client.api.AssertionEnum;
import eu.europa.ec.sante.openncp.core.client.api.QueryDocumentRequest;
import org.immutables.value.Value;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.Map;

@Value.Immutable
public interface QueryDocumentOperation {

    Map<AssertionEnum, Assertion> getAssertions();

    QueryDocumentRequest getRequest();
}
