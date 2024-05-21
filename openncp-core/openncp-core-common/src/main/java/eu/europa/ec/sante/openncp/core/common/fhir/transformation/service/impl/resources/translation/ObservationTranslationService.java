package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Service;

@Service
public class ObservationTranslationService implements IDomainTranslationService<Observation> {
    @Override
    public Observation translate(Observation patient, String targetLanguage) {
        return null;
    }
}
