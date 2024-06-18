package eu.europa.ec.sante.openncp.application.client.connector.assertion;

import eu.europa.ec.sante.openncp.application.client.connector.testutils.AssertionTestUtil;
import org.junit.jupiter.api.Test;
import org.opensaml.saml.saml2.core.Assertion;

import javax.xml.soap.SOAPMessage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrcAssertionRequestTest {
    @Test
    void test_getSoapBody() throws MalformedURLException {
/*
        final TrcAssertionRequest trcAssertionRequest = ImmutableTrcAssertionRequest.builder()
                .location(new URL("http://localhost:8080"))
                .assertion(createClinicalAssertion("kim", "wauters","email@test.com"))
                .checkForHostname(false)
                .validationEnabled(false)
                .purposeOfUse("pou")
                .patientId("123")
                .build();
        assertThat(trcAssertionRequest).isNotNull();
        final SOAPMessage soapMessage = trcAssertionRequest.getSoapMessage();
        assertThat(soapMessage).isNotNull();
 */
    }

    private Assertion createClinicalAssertion(final String username, final String fullName,
                                              final String email) {
        final List<String> permissions = new ArrayList<>();
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-003");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-004");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-005");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-006");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-010");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-016");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PPD-032");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PPD-033");
        permissions.add("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PPD-046");

        final AssertionTestUtil.Concept conceptRole = new AssertionTestUtil.Concept();
        conceptRole.setCode("221");
        conceptRole.setCodeSystemId("2.16.840.1.113883.2.9.6.2.7");
        conceptRole.setCodeSystemName("ISCO");
        conceptRole.setDisplayName("Medical Doctors");

        return AssertionTestUtil.createHCPAssertion(fullName, email, "BE", "BElgium", "homecommid", conceptRole,
                "eHealth OpenNCP EU Portal", "urn:hl7ii:1.2.3.4:ABCD", "Resident Physician", "TREATMENT",
                "eHDSI EU Testing MedCare Center", permissions, null);
    }
}