package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestTranscodingLogicService extends AbstractTranscodingLogicService<ServiceRequest> {

    public ServiceRequestTranscodingLogicService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.ServiceRequest;
    }

    @Override
    public void transcodeTypedResource(final ServiceRequest typedResource) {

    }
}
