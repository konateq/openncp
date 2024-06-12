package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ObservationResultsLaboratoryMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class ObservationTranscodingService extends AbstractResourceTranscodingService<ObservationResultsLaboratoryMyHealthEu> {

    public ObservationTranscodingService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Observation;
    }

    @Override
    public ObservationResultsLaboratoryMyHealthEu transcodeTypedResource(final ObservationResultsLaboratoryMyHealthEu observationResultsLaboratoryMyHealthEu) {

        transcodeCodeableConceptsList(observationResultsLaboratoryMyHealthEu.getCategory());
        transcodeCodeableConcept(observationResultsLaboratoryMyHealthEu.getCode());
        transcodeCodeableConcept(observationResultsLaboratoryMyHealthEu.getPerformerFunctionMyHealthEu().getValue());
        transcodeCodeableConcept(observationResultsLaboratoryMyHealthEu.getValueCodeableConcept());
        transcodeCodeableConceptsList(observationResultsLaboratoryMyHealthEu.getInterpretation());
        transcodeCodeableConcept(observationResultsLaboratoryMyHealthEu.getMethod());
        for (final Observation.ObservationReferenceRangeComponent observationReferenceRangeComponent : observationResultsLaboratoryMyHealthEu.getReferenceRange()) {
            transcodeCodeableConcept(observationReferenceRangeComponent.getType());
            transcodeCodeableConceptsList(observationReferenceRangeComponent.getAppliesTo());
        }
        for (final Observation.ObservationComponentComponent observationComponentComponent : observationResultsLaboratoryMyHealthEu.getComponent()) {
            transcodeCodeableConcept(observationComponentComponent.getCode());
            transcodeCodeableConcept(observationComponentComponent.getValueCodeableConcept());
            transcodeCodeableConceptsList(observationComponentComponent.getInterpretation());
            for (final Observation.ObservationReferenceRangeComponent observationReferenceRangeComponent : observationComponentComponent.getReferenceRange()) {
                transcodeCodeableConcept(observationReferenceRangeComponent.getType());
                transcodeCodeableConceptsList(observationReferenceRangeComponent.getAppliesTo());
            }
        }
        return observationResultsLaboratoryMyHealthEu;
    }
}
