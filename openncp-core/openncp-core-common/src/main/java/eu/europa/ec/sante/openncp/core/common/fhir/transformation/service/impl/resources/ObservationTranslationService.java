package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ObservationResultsLaboratoryMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class ObservationTranslationService extends AbstractResourceTranslationService<ObservationResultsLaboratoryMyHealthEu> {

    public ObservationTranslationService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ObservationResultsLaboratoryMyHealthEu translateTypedResource(final ObservationResultsLaboratoryMyHealthEu observationResultsLaboratoryMyHealthEu, final String targetLanguage) {

        translateCodeableConceptsList(observationResultsLaboratoryMyHealthEu.getCategory(), targetLanguage);
        translateCodeableConcept(observationResultsLaboratoryMyHealthEu.getCode(), targetLanguage);
        translateCodeableConcept(observationResultsLaboratoryMyHealthEu.getPerformerFunctionMyHealthEu().getValue(), targetLanguage);
        translateCodeableConcept(observationResultsLaboratoryMyHealthEu.getValueCodeableConcept(), targetLanguage);
        translateCodeableConceptsList(observationResultsLaboratoryMyHealthEu.getInterpretation(), targetLanguage);
        translateCodeableConcept(observationResultsLaboratoryMyHealthEu.getMethod(), targetLanguage);
        for (final Observation.ObservationReferenceRangeComponent observationReferenceRangeComponent : observationResultsLaboratoryMyHealthEu.getReferenceRange()) {
            translateCodeableConcept(observationReferenceRangeComponent.getType(), targetLanguage);
            translateCodeableConceptsList(observationReferenceRangeComponent.getAppliesTo(), targetLanguage);
        }
        for (final Observation.ObservationComponentComponent observationComponentComponent : observationResultsLaboratoryMyHealthEu.getComponent()) {
            translateCodeableConcept(observationComponentComponent.getCode(), targetLanguage);
            translateCodeableConcept(observationComponentComponent.getValueCodeableConcept(), targetLanguage);
            translateCodeableConceptsList(observationComponentComponent.getInterpretation(), targetLanguage);
            for (final Observation.ObservationReferenceRangeComponent observationReferenceRangeComponent : observationComponentComponent.getReferenceRange()) {
                translateCodeableConcept(observationReferenceRangeComponent.getType(), targetLanguage);
                translateCodeableConceptsList(observationReferenceRangeComponent.getAppliesTo(), targetLanguage);
            }
        }

        return observationResultsLaboratoryMyHealthEu;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Observation;
    }
}
