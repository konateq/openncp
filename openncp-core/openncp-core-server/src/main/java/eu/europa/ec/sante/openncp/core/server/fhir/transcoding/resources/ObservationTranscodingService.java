package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ObservationResultsLaboratoryMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
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
        return observationResultsLaboratoryMyHealthEu;
    }
}
