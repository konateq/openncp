package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class ObservationTranscodingLogicService extends AbstractTranscodingLogicService<Observation> {

    public ObservationTranscodingLogicService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Observation;
    }

    @Override
    public void transcodeTypedResource(final Observation typedResource) {

    }
}
