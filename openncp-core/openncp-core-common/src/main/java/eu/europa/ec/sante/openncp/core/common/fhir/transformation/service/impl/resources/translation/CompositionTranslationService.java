package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import org.hl7.fhir.r4.model.Composition;
import org.springframework.stereotype.Service;

@Service
public class CompositionTranslationService extends AbstractTranslationService implements IDomainTranslationService<Composition>  {
    @Override
    public Composition translate(Composition composition, String targetLanguage) {

        //Composition type
        var coding = retrieveCoding(composition.getType());
        coding.ifPresent(value -> {
            var translatedValue = getTranslation(value, targetLanguage);
            ToolingExtensions.addLanguageTranslation(value.getDisplayElement(), targetLanguage, translatedValue);
        });
        return composition;
    }
}
