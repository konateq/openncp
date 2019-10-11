package eu.europa.ec.sante.ehdsi.openncp.assertionvalidator;

import org.apache.commons.lang3.StringUtils;

/**
 * Structural Role of the HCP - From eHDSI SAML Profile
 * FriendlyName:  XSPA Role
 * Name:  urn:oasis:names:tc:xacml:2.0:subject:role
 */
public enum XSPARole {

    LICENSED_HCP("Licensed Health Care Providers"),
    NOT_LICENSED_HCP("Non-Licensed Health Care Providers"),
    CLERICAL_ADMINISTRATIVE("Clerical and Administrative Personnel");

    private final String role;

    XSPARole(final String role) {
        this.role = role;
    }

    public static boolean containsLabel(String label) {
        for (XSPARole e : values()) {
            if (StringUtils.equalsIgnoreCase(e.role, label)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return role;
    }
}

