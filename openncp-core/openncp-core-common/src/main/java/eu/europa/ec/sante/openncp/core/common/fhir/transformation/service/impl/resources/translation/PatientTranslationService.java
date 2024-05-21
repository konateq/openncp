package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class PatientTranslationService implements IDomainTranslationService<Patient> {


    @Override
    public Patient translate(Patient patient, String targetLanguage) {
        Enumerations.AdministrativeGender administrativeGender = patient.getGender();
        return patient;
    }
}
