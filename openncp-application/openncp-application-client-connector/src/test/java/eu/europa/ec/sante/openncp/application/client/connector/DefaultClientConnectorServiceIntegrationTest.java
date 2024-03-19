package eu.europa.ec.sante.openncp.application.client.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.sante.openncp.application.client.connector.testutils.AssertionTestUtil;
import eu.europa.ec.sante.openncp.application.client.connector.testutils.AssertionTestUtil.Concept;
import eu.europa.ec.sante.openncp.application.client.connector.testutils.AssertionTestUtil.SignatureKeystoreInfo;
import eu.europa.ec.sante.openncp.core.client.PatientDemographics;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.security.key.DefaultKeyStoreManager;
import eu.europa.ec.sante.openncp.core.common.security.key.KeyStoreManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.opensaml.saml.saml2.core.Assertion;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultClientConnectorServiceIntegrationTest {

    private static DefaultClientConnectorService clientConnectorService;
    private static DefaultKeyStoreManager keyStoreManager;

    @BeforeAll
    static void setup() {
        final String keyStoreLocation = "D:\\projects\\work\\dg_sante\\ehealth_bak\\openncp-docker\\certificates\\gazelle-signature-keystore.jks";
        final String keystorePassword = "gazelle";

        final String truststoreLocation = "D:\\projects\\work\\dg_sante\\ehealth_bak\\openncp-docker\\certificates\\eu-truststore.jks";
        final String truststorePassword = "changeit";

        final String privateKeyAlias = "gazelle.ncp-signature.openncp.dg-sante.eu";
        final String privateKeyPassword = "gazelle";

        keyStoreManager = new DefaultKeyStoreManager(keyStoreLocation, keystorePassword, truststoreLocation, truststorePassword, privateKeyAlias,
                                                     privateKeyPassword);

        clientConnectorService = new DefaultClientConnectorService("https://localhost:8080/services/ClientConnectorService", keyStoreManager);
    }

    @Test
    void sayHello() {
        final String response = clientConnectorService.sayHello("Kim");
        assertThat(response).isEqualTo("Hello Kim");
    }

    @Test
    @SetEnvironmentVariable(key = "EPSOS_PROPS_PATH", value = "D:\\projects\\data\\dg_sante\\openncp\\EPSOS_PROPS_PATH\\")
    void queryPatient() throws ClientConnectorException {
        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        assertions.put(AssertionEnum.CLINICIAN, createClinicalAssertion(keyStoreManager, "Doctor House", "John House", "house@ehdsi.eu"));

        final List<PatientDemographics> response = clientConnectorService.queryPatient(assertions, "BE", null);
        assertThat(response).isNotNull();
    }

    private Assertion createClinicalAssertion(final KeyStoreManager keyStoreManager, final String username, final String fullName,
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

        final Concept conceptRole = new Concept();
        conceptRole.setCode("221");
        conceptRole.setCodeSystemId("2.16.840.1.113883.2.9.6.2.7");
        conceptRole.setCodeSystemName("ISCO");
        conceptRole.setDisplayName("Medical Doctors");

        final SignatureKeystoreInfo signatureKeystoreInfo = new SignatureKeystoreInfo();
        signatureKeystoreInfo.setSignatureKeystorePath(
                "D:\\projects\\work\\dg_sante\\ehealth_bak\\openncp-docker\\certificates\\gazelle-signature-keystore.jks");
        signatureKeystoreInfo.setSignatureKeystorePassword("gazelle");

        signatureKeystoreInfo.setSignatureKeyAlias("gazelle.ncp-signature.openncp.dg-sante.eu");
        signatureKeystoreInfo.setSignatureKeyPassword("gazelle");

        return AssertionTestUtil.createHCPAssertion(keyStoreManager, signatureKeystoreInfo, username, fullName, email, "BE", "BElgium", "homecommid",
                                                    conceptRole, "eHealth OpenNCP EU Portal", "urn:hl7ii:1.2.3.4:ABCD", "Resident Physician",
                                                    "TREATMENT", "eHDSI EU Testing MedCare Center", permissions, null);
    }
}
