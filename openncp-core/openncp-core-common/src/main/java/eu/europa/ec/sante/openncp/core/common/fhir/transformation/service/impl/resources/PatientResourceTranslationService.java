package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class PatientResourceTranslationService extends AbstractResourceTranslationService<Patient> {


    public PatientResourceTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public Patient translateTypedResource(Patient patient, String targetLanguage) {

        /** Country **/
        for (Address address: patient.getAddress()) {
            if (address.getCountryElement() != null) {
//                ToolingExtensions.addLanguageTranslation(address.getCountryElement(), targetLanguage, getTranslation(address.getCountryElement().castToCoding(), targetLanguage));
            }
        }
        return patient;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Patient;
    }
}
