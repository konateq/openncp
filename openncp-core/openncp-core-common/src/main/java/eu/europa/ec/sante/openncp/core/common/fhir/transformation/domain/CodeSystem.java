package eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain;

import java.util.HashMap;
import java.util.Map;

public enum CodeSystem {

    ATC("ATC Classification", "2.16.840.1.113883.6.73", "http://www.whocc.no/atc"),
    EDQM("EDQM Standard Terms", "0.4.0.127.0.16.1.1.2.1", "urn:oid:0.4.0.127.0.16.1.1.2.1"),
    EHDSI_DISPLAY_LABEL("epSOSDisplayLabel", "1.3.6.1.4.1.12559.11.10.1.3.1.44.4", ""),
    EMA_SMS("EMA SMS Substances list", "2.16.840.1.113883.3.6905.2", ""),
    EMDN("EMDN", "1.3.6.1.4.1.12559.11.10.1.3.1.44.6", ""),
    FHIR_ALLERGY_INTOLERANCE_CLINICAL_STATUS_CODES("FHIR Allergy Intolerance Clinical Status Codes", "2.16.840.1.113883.4.642.4.1373", "http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical\n"),
    FHIR_ALLERGY_INTOLERANCE_CRITICALITY("FHIR Allergy Intolerance Criticality", "2.16.840.1.113883.4.642.4.130", "http://hl7.org/fhir/ValueSet/allergy-intolerance-criticality"),
    FHIR_ALLERGY_INTOLERANCE_VERIFICATION_STATUS_CODES("FHIR Allergy Intolerance Verification Status Codes", "2.16.840.1.113883.4.642.4.1371", "http://terminology.hl7.org/CodeSystem/allergyintolerance-verification"),
    FHIR_CONDITION_VERIFICATION_STATUS("FHIR Condition Verification Status", "2.16.840.1.113883.4.642.4.1075", "http://terminology.hl7.org/CodeSystem/condition-ver-status\n"),
    HL7_V3_ADDRESS_USE("HL7 v3 AddressUse", "2.16.840.1.113883.5.1119", ""),
    HL7_V3_ADMINISTRATIVE_GENDER("HL7 v3 AdministrativeGender", "2.16.840.1.113883.5.1", "http://hl7.org/fhir/administrative-gender"),
    HL7_V3_CONFIDENTIALITY("HL7 v3 Confidentiality", "2.16.840.1.113883.5.25", ""),
    HL7_V3_NULL_FLAVOR("HL7 v3 NullFlavor","2.16.840.1.113883.5.1008",""),
    HL7_V3_ROLE_CODE("HL7 v3 RoleCode", "2.16.840.1.113883.5.111", ""),
    HL7_V3_SUBSTANCE_ADMIN_SUBSTITUTION("HL7 v3 substanceAdminSubstitution", "2.16.840.1.113883.5.1070", ""),
    ICD_10("ICD_10", "1.3.6.1.4.1.12559.11.10.1.3.1.44.2", ""),
    IPS_ABSENT_AND_UNKNOWN_DATA("IPS Absent and Unknown Data", "2.16.840.1.113883.5.1150.1", ""),
    ISCO_08("ISCO_08", "2.16.840.1.113883.2.9.6.2.7", ""),
    ISO_3166_1("", "", ""),
    LOINC("LOINC","2.16.840.1.113883.6.1","http://loinc.org"),

    RARE_DISEASE("ORPHAnet", "1.3.6.1.4.1.12559.11.10.1.3.1.44.5", ""),
    SNOMED_CT("SNOMED CT", "2.16.840.1.113883.6.96", "http://snomed.info/sct"),
    UCUM("UCUM Unified Code for Units of Measure", "2.16.840.1.113883.6.8", "");



    private String name;

    private String oid;

    private String url;

    private static final Map<String, String> urlLookup = new HashMap();
    private static final Map<String, String> oidLookup = new HashMap();

    static {
        for (CodeSystem d : CodeSystem.values()) {
            oidLookup.put(d.getUrl(), d.getOid());
            urlLookup.put(d.getOid(), d.getUrl());
        }
    }

    CodeSystem(String name, String oid, String url) {
        this.name = name;
        this.oid = oid;
        this.url = url;
    }


    public static String getOidBasedOnUrl(String url) {
        return oidLookup.get(url);
    }

    public static String getUrlBasedOnOid(String oid) {
        return urlLookup.get(oid);
    }

    public String getName() {
        return name;
    }

    public String getOid() {
        return oid;
    }

    public String getUrl() {
        return url;
    }
}
