package eu.europa.ec.sante.openncp.sts;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.saml2.core.Assertion;

/**
 * FIXME
 * Temporary class to be able to build the project.
 * Once the security is lift and shifted we can delete this class and use the actual implementation
 */
public class SamlTRCIssuer {

    public Assertion issueTrcToken(Assertion hcpIdAssertion, String patientID, String purposeOfUse, String dispensationPinCode, String prescriptionId,
                                   Object o) throws SMgrException {
        return null;
    }

    public String getPointOfCare() {
        return StringUtils.EMPTY;
    }

    public String getHumanRequestorNameId() {
        return StringUtils.EMPTY;
    }

    public String getFunctionalRole() {
        return StringUtils.EMPTY;
    }

    public String getFacilityType() {
        return StringUtils.EMPTY;
    }

    public String getHumanRequestorSubjectId() {
        return StringUtils.EMPTY;
    }
}
