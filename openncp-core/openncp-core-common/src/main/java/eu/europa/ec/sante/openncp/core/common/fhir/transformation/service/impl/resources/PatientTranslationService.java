package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class PatientTranslationService extends AbstractResourceTranslationService<Patient> {


    public PatientTranslationService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public Patient translateTypedResource(final Patient patient, final String targetLanguage) {
        return patient;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Patient;
    }
}
