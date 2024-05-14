package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.service.IFHIRTerminologyService;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.response.TSAMResponse;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AbstractTranslationService {

    @Autowired
    private IFHIRTerminologyService fhirTerminologyService;

    String getTranslation(Coding coding, String targetLanguageCode) {
        TSAMResponse tsamResponse = fhirTerminologyService.getDesignationForConcept(coding, targetLanguageCode);
        return tsamResponse.getDesignation();
    }

    Optional<Coding> retrieveCoding(CodeableConcept codeableConcept) {

        return codeableConcept.getCoding()
                .stream()
                .filter(coding -> !coding.getUserSelected())
                .findFirst()
                .or(() -> codeableConcept.getCoding().stream().findFirst());
    }
}
