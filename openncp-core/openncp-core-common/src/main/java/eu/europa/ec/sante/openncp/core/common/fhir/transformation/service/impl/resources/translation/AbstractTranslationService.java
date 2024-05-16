package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;


import eu.europa.ec.sante.openncp.core.common.tsam.CodeConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Service;

import java.util.Optional;


public class AbstractTranslationService {
    private final TerminologyService terminologyService;

    public AbstractTranslationService(final TerminologyService terminologyService) {
        this.terminologyService = Validate.notNull(terminologyService);
    }

    String getTranslation(Coding coding, String targetLanguageCode) {
        final CodeConcept codeConcept = CodeConcept.from(coding);
        final TSAMResponseStructure tsamResponse = terminologyService.getDesignation(codeConcept, targetLanguageCode);
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
