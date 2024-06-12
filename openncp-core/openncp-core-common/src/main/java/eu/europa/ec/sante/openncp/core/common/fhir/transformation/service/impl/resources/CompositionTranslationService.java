package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.CompositionLabReportMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class CompositionTranslationService extends AbstractResourceTranslationService<CompositionLabReportMyHealthEu> {

    public CompositionTranslationService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public CompositionLabReportMyHealthEu translateTypedResource(final CompositionLabReportMyHealthEu compositionLabReportMyHealthEu, final String targetLanguage) {

        translateCodeableConcept(compositionLabReportMyHealthEu.getType(), targetLanguage);
        translateCodeableConceptsList(compositionLabReportMyHealthEu.getCategory(), targetLanguage);
        for (final Composition.SectionComponent sectionComponent : compositionLabReportMyHealthEu.getSection()) {
            translateSection(sectionComponent, targetLanguage);
        }
        return compositionLabReportMyHealthEu;
    }

    private void translateSection(final Composition.SectionComponent sectionComponent, final String targetLanguage) {
        translateCodeableConcept(sectionComponent.getCode(), targetLanguage);
        for (final Composition.SectionComponent nestedSectionComponent : sectionComponent.getSection()) {
            translateSection(nestedSectionComponent, targetLanguage);
        }
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Composition;
    }
}
