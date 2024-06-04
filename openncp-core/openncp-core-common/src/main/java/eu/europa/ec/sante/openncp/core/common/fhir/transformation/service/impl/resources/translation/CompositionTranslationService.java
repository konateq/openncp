package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.CompositionLabReportMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Composition;
import org.springframework.stereotype.Service;

@Service
public class CompositionTranslationService extends AbstractTranslationService implements IDomainTranslationService<CompositionLabReportMyHealthEu>  {
    public CompositionTranslationService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public CompositionLabReportMyHealthEu translate(CompositionLabReportMyHealthEu compositionLabReportMyHealthEu, String targetLanguage) {

        /** Type **/
        addTranslation(compositionLabReportMyHealthEu.getType(), targetLanguage);

        /** Category - Study type **/
        for (CodeableConcept codeableConcept: compositionLabReportMyHealthEu.getCategory())
            addTranslation(codeableConcept, targetLanguage);

        /** Section code **/
        for (Composition.SectionComponent sectionComponent: compositionLabReportMyHealthEu.getSection()) {
            translateSection(sectionComponent, targetLanguage);
        }

        return compositionLabReportMyHealthEu;
    }

    private void translateSection(Composition.SectionComponent sectionComponent, String targetLanguage) {
        addTranslation(sectionComponent.getCode(), targetLanguage);
        for (Composition.SectionComponent nestedSectionComponent: sectionComponent.getSection()) {
            translateSection(nestedSectionComponent, targetLanguage);
        }
    }
}
