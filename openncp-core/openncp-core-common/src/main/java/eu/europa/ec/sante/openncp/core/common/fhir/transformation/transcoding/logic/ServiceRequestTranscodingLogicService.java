package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.service.IFHIRTerminologyService;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestTranscodingLogicService extends AbstractTranscodingLogicService<ServiceRequest> {

    public ServiceRequestTranscodingLogicService(final IFHIRTerminologyService fhirTerminologyService) {
        super(fhirTerminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.ServiceRequest;
    }

    @Override
    public void transcodeTypedResource(final ServiceRequest typedResource) {

    }
}
