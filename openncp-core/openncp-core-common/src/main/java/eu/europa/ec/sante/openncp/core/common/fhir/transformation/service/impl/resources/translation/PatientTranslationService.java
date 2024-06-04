package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Service;

@Service
public class PatientTranslationService extends AbstractTranslationService implements IDomainTranslationService<Patient>  {


    public PatientTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public Patient translate(Patient patient, String targetLanguage) {

        /** Country **/
        for (Address address: patient.getAddress()) {
            if (address.getCountryElement() != null) {
//                ToolingExtensions.addLanguageTranslation(address.getCountryElement(), targetLanguage, getTranslation(address.getCountryElement().castToCoding(), targetLanguage));
            }
        }
        return patient;
    }
}
