package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ObservationResultsLaboratoryMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObservationResourceTranslationService extends AbstractResourceTranslationService<ObservationResultsLaboratoryMyHealthEu> {

    public ObservationResourceTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ObservationResultsLaboratoryMyHealthEu translateTypedResource(ObservationResultsLaboratoryMyHealthEu observationResultsLaboratoryMyHealthEu, String targetLanguage) {

        /** Category:studyType **/
        if (observationResultsLaboratoryMyHealthEu.getCategory().size() > 1) {
            for (int i = 1; i < observationResultsLaboratoryMyHealthEu.getCategory().size(); i++) {
                addTranslation(observationResultsLaboratoryMyHealthEu.getCategory().get(i), targetLanguage);
            }
        }

        /** Code **/
        addTranslation(observationResultsLaboratoryMyHealthEu.getCode(), targetLanguage);

        /** Performer **/
//            for (Reference reference: observationResultsLaboratoryMyHealthEu.getPerformer()) {
//                for (PerformerFunction performerFunction: reference.getPerformerFunction()) {
//                  addTranslation(performerFunction.getValue(), targetLanguage);
//            }
//        }

        /** ValueCodeableConcept **/
//        addTranslation(observationResultsLaboratoryMyHealthEu.getValueCodeableConcept(), targetLanguage);

        /** Interpretation **/
        for (CodeableConcept codeableConcept: observationResultsLaboratoryMyHealthEu.getInterpretation()) {
            addTranslation(codeableConcept, targetLanguage);
        }

        /** Method **/
        addTranslation(observationResultsLaboratoryMyHealthEu.getMethod(), targetLanguage);

        /** ReferenceRange **/
        translateReferenceRange(observationResultsLaboratoryMyHealthEu.getReferenceRange(), targetLanguage);

        /** Component **/
        for (Observation.ObservationComponentComponent component: observationResultsLaboratoryMyHealthEu.getComponent()) {
            addTranslation(component.getCode(), targetLanguage);
            addTranslation(component.getValueCodeableConcept(), targetLanguage);
            for (CodeableConcept codeableConcept: component.getInterpretation()) {
                addTranslation(codeableConcept, targetLanguage);
            }
            translateReferenceRange(component.getReferenceRange(), targetLanguage);
        }
        return observationResultsLaboratoryMyHealthEu;
    }

    private void translateReferenceRange(List<Observation.ObservationReferenceRangeComponent> observationResultsLaboratoryMyHealthEu, String targetLanguage) {
        for (Observation.ObservationReferenceRangeComponent referenceRange : observationResultsLaboratoryMyHealthEu) {
            addTranslation(referenceRange.getType(), targetLanguage);
            for (CodeableConcept codeableConcept : referenceRange.getAppliesTo()) {
                addTranslation(codeableConcept, targetLanguage);
            }
        }
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Observation;
    }
}
