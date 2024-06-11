package eu.europa.eu.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.PatientResourceTranslationService;
import eu.europa.eu.sante.openncp.core.common.DummyApplication;
import org.hl7.fhir.r4.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = DummyApplication.class)
@RunWith(SpringRunner.class)
public class PatientTranslationServiceTest {

    @Autowired
    PatientResourceTranslationService patientTranslationService;

    @Test
    public void testTranslate() {
        Patient patient = buildPatient();
        Patient translatedPatient = patientTranslationService.translate(patient, "nl-BE");
        Assert.assertEquals(translatedPatient, patient);
    }

    private Patient buildPatient() {
        Patient patient = new Patient();
        Identifier identifier = new Identifier();
        identifier.setValue("urn:oid:1.2.840.113556.1.9");
        patient.addIdentifier(identifier);
        patient.setGender(Enumerations.AdministrativeGender.MALE);
        HumanName humanName = new HumanName();
        humanName.setGiven(Collections.singletonList(new StringType("Firstname")));
        humanName.setFamily("Lastname");
        patient.setName(Collections.singletonList(humanName));
        return patient;
    }
}
