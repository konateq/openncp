package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.service.IFHIRTerminologyService;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class ObservationTranscodingLogicService extends AbstractTranscodingLogicService<Observation> {

    public ObservationTranscodingLogicService(final IFHIRTerminologyService fhirTerminologyService) {
        super(fhirTerminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Observation;
    }

    @Override
    public void transcodeTypedResource(final Observation typedResource) {

    }
}
