package eu.europa.ec.sante.openncp.sts;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;

/**
 * FIXME
 * Temporary class to be able to build the project.
 * Once the security is lift and shifted we can delete this class and use the actual implementation
 */
public class SamlNextOfKinIssuer {

    public Assertion issueNextOfKinToken(Assertion hcpIdAssertion, String doctorId, String id, List<Attribute> attributeList) throws SMgrException {
        return null;
    }

    public String getPointOfCare() {
        return StringUtils.EMPTY;
    }

    public String getHumanRequestorNameId() {
        return StringUtils.EMPTY;
    }

    public String getHumanRequestorSubjectId() {
        return StringUtils.EMPTY;
    }

    public String getFunctionalRole() {
        return StringUtils.EMPTY;
    }

    public String getFacilityType() {
        return StringUtils.EMPTY;
    }
}
