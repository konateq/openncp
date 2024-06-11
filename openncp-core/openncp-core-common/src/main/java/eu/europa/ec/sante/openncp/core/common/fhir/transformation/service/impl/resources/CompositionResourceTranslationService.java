package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.CompositionLabReportMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class CompositionResourceTranslationService extends AbstractResourceTranslationService<CompositionLabReportMyHealthEu> {

    public CompositionResourceTranslationService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public CompositionLabReportMyHealthEu translateTypedResource(final CompositionLabReportMyHealthEu compositionLabReportMyHealthEu, String targetLanguage) {

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

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Composition;
    }
}
