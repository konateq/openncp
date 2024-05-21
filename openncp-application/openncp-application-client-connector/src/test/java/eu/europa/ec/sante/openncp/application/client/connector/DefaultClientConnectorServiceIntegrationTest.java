package eu.europa.ec.sante.openncp.application.client.connector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.sante.openncp.application.client.connector.assertions.STSClientException;
import eu.europa.ec.sante.openncp.application.client.connector.testutils.AssertionTestUtil;
import eu.europa.ec.sante.openncp.application.client.connector.testutils.AssertionTestUtil.Concept;
import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.core.common.constants.ihe.IheConstants;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.PurposeOfUse;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.security.key.DefaultKeyStoreManager;
import eu.europa.ec.sante.openncp.core.common.security.key.KeyStoreManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Mockito;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SetEnvironmentVariable(key = "EPSOS_PROPS_PATH", value = "")
@Disabled("Requires a complete environment")
class DefaultClientConnectorServiceIntegrationTest {

    private static DefaultClientConnectorService clientConnectorService;
    private static DefaultKeyStoreManager keyStoreManager;

    @BeforeAll
    static void setup() throws Exception {
        final ConfigurationManager mockedConfigurationManager = Mockito.mock(ConfigurationManager.class);
        when(mockedConfigurationManager.getProperty("SC_KEYSTORE_PATH")).thenReturn("src/test/resources/gazelle-service-consumer-keystore.jks");
        when(mockedConfigurationManager.getProperty("SC_KEYSTORE_PASSWORD")).thenReturn("gazelle");
        when(mockedConfigurationManager.getProperty("SC_PRIVATEKEY_PASSWORD")).thenReturn("gazelle");
        when(mockedConfigurationManager.getProperty("TRUSTSTORE_PATH")).thenReturn("src/test/resources/eu-truststore.jks");
        when(mockedConfigurationManager.getProperty("TRUSTSTORE_PASSWORD")).thenReturn("changeit");

        when(mockedConfigurationManager.getProperty("NCP_SIG_KEYSTORE_PATH")).thenReturn("src/test/resources/gazelle-signature-keystore.jks");
        when(mockedConfigurationManager.getProperty("NCP_SIG_KEYSTORE_PASSWORD")).thenReturn("gazelle");
        when(mockedConfigurationManager.getProperty("NCP_SIG_PRIVATEKEY_ALIAS")).thenReturn("gazelle.ncp-signature.openncp.dg-sante.eu");
        when(mockedConfigurationManager.getProperty("NCP_SIG_PRIVATEKEY_PASSWORD")).thenReturn("gazelle");

        when(mockedConfigurationManager.getProperty("secman.sts.url")).thenReturn("https://localhost:2443/TRC-STS/STSServiceService");
        when(mockedConfigurationManager.getProperty("secman.sts.checkHostname")).thenReturn("false");



        setFinalStatic(ConfigurationManagerFactory.class.getDeclaredField("configurationManager"), mockedConfigurationManager);

        clientConnectorService = new DefaultClientConnectorService("https://localhost:6443/openncp-client-connector/services/ClientConnectorService");
    }

    static void setFinalStatic(final Field field, final Object newValue) throws Exception {
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @Test
    void sayHello() {
        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        assertions.put(AssertionEnum.CLINICIAN, createClinicalAssertion(keyStoreManager, "Doctor House", "John House", "house@ehdsi.eu"));
        final String response = clientConnectorService.sayHello(assertions, "Kim");
        assertThat(response).isEqualTo("Hello Kim");
    }

    @Test
    void queryPatient() throws ClientConnectorException {
        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        assertions.put(AssertionEnum.CLINICIAN, createClinicalAssertion(keyStoreManager, "Doctor House", "John House", "house@ehdsi.eu"));

        final ObjectFactory objectFactory = new ObjectFactory();
        final PatientId patientId = objectFactory.createPatientId();
        patientId.setRoot("1.3.6.1.4.1.48336");
        patientId.setExtension("2-1234-W7");

        final PatientDemographics patientDemographics = objectFactory.createPatientDemographics();
        patientDemographics.getPatientId().add(patientId);

        final List<PatientDemographics> response = clientConnectorService.queryPatient(assertions, "BE", patientDemographics);
        assertThat(response).isNotNull();
    }

    @Test
    void queryDocuments() throws ClientConnectorException, STSClientException, MarshallingException {
        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        Assertion clinicalAssertion = createClinicalAssertion(keyStoreManager, "Doctor House", "John House", "house@ehdsi.eu");

        final ObjectFactory objectFactory = new ObjectFactory();
        final PatientId patientId = objectFactory.createPatientId();
        patientId.setRoot("1.3.6.1.4.1.48336");
        patientId.setExtension("2-1234-W7");

        assertions.put(AssertionEnum.CLINICIAN, clinicalAssertion);
        Assertion treatmentConfirmationAssertion = AssertionTestUtil.createPatientConfirmationPlain(clinicalAssertion, patientId, "TREATMENT");
        assertions.put(AssertionEnum.TREATMENT, treatmentConfirmationAssertion);

        GenericDocumentCode classCode = objectFactory.createGenericDocumentCode();
        classCode.setNodeRepresentation(ClassCode.PS_CLASSCODE.getCode());
        classCode.setSchema(IheConstants.CLASSCODE_SCHEME);
        classCode.setValue(Constants.PS_TITLE);

        List<EpsosDocument> documentList = clientConnectorService.queryDocuments(assertions, "BE", patientId, List.of(classCode), null);
        assertThat(documentList).isNotNull().hasSize(2);
    }

    @Test
    void retrieveDocument() throws ClientConnectorException, STSClientException, MarshallingException {
        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        Assertion clinicalAssertion = createClinicalAssertion(keyStoreManager, "Doctor House", "John House", "house@ehdsi.eu");

        final ObjectFactory objectFactory = new ObjectFactory();
        final PatientId patientId = objectFactory.createPatientId();
        patientId.setRoot("1.3.6.1.4.1.48336");
        patientId.setExtension("2-1234-W7");

        var documentId = objectFactory.createDocumentId();
        documentId.setDocumentUniqueId("1.2.752.129.2.1.2.1^PS_W7_EU.1");
        documentId.setRepositoryUniqueId("1.3.6.1.4.1.48336");

        assertions.put(AssertionEnum.CLINICIAN, clinicalAssertion);
        Assertion treatmentConfirmationAssertion = AssertionTestUtil.createPatientConfirmationPlain(clinicalAssertion, patientId, PurposeOfUse.TREATMENT.name());
        assertions.put(AssertionEnum.TREATMENT, treatmentConfirmationAssertion);

        GenericDocumentCode classCode = objectFactory.createGenericDocumentCode();
        classCode.setNodeRepresentation(ClassCode.PS_CLASSCODE.getCode());
        classCode.setSchema(IheConstants.CLASSCODE_SCHEME);
        classCode.setValue(Constants.PS_TITLE);

        EpsosDocument document = clientConnectorService.retrieveDocument(assertions, "BE", documentId, "1.3.6.1.4.1.48336", classCode, null);
        assertThat(document).isNotNull();
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
        
        return AssertionTestUtil.createHCPAssertion(username, fullName, email, "BE", "BElgium", "homecommid", conceptRole,
                                                    "eHealth OpenNCP EU Portal", "urn:hl7ii:1.2.3.4:ABCD", "Resident Physician", "TREATMENT",
                                                    "eHDSI EU Testing MedCare Center", permissions, null);
    }
}
