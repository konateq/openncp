package eu.europa.ec.sante.ehdsi.openncp.assertionvalidator;

import org.apache.commons.lang3.StringUtils;

/**
 * Structural Role of the Healthcare Provide HCP specified into MyHealth@EU SAML Profile
 * FriendlyName: XSPA Role
 * Name: urn:oasis:names:tc:xacml:2.0:subject:role
 */
public enum XSPARole {

    AUDIOLOGIST("2266", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Audiologists and speech therapists"),
    DENTIST("2261", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Dentists"),
    MEDICAL_DOCTORS("221", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Medical Doctors"),
    MEDICAL_TECHNICIAN("3211", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Medical imaging and therapeutic equipment technicians"),
    MIDWIFE("2222", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Midwifery professionals"),
    NURSE("2221", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Nursing professionals"),
    NUTRITIONIST("2265", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Dieticians and nutritionists"),
    OPTICIAN("2267", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Optometrists and ophthalmic opticians"),
    OTHER_CLINICIAN("2269", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Health professionals not elsewhere classified"),
    OTHER_CLERICAL("44", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Other Clerical Support Workers"),
    PHARMACIST("2262", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Pharmacists"),
    PHARMACIST_ASSISTANT("3213", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Pharmaceutical technicians and assistants"),
    PHYSIOTHERAPIST("2264", "2.16.840.1.113883.2.9.6.2.7", "ISCO", "Physiotherapists");

    private final String code;
    private final String codeSystem;
    private final String codeSystemName;
    private final String displayName;

    XSPARole(String code, String codeSystem, String codeSystemName, String displayName) {
        this.code = code;
        this.codeSystem = codeSystem;
        this.codeSystemName = codeSystemName;
        this.displayName = displayName;
    }

    public static boolean containsCode(String label) {

        for (XSPARole role : values()) {
            if (StringUtils.equalsIgnoreCase(role.code, label)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateRole(String code, String name) {
        for (XSPARole role : values()) {
            if (StringUtils.equalsIgnoreCase(role.code, code) && StringUtils.equalsIgnoreCase(role.displayName, name)) {
                return true;
            }
        }
        return false;
    }

    public String getCode() {
        return code;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "XSPARole{" +
                "code='" + code + '\'' +
                ", codeSystem='" + codeSystem + '\'' +
                ", codeSystemName='" + codeSystemName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
