package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ServiceRequestLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceRequestTranscodingService extends AbstractResourceTranscodingService<ServiceRequestLabMyHealthEu> {

    public ServiceRequestTranscodingService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.ServiceRequest;
    }

    @Override
    public ServiceRequestLabMyHealthEu transcodeTypedResource(final ServiceRequestLabMyHealthEu serviceRequestLabMyHealthEu) {

        // Code
        Optional<Coding> codeTranscoding = getTranscoding(serviceRequestLabMyHealthEu.getCode().getCoding().iterator().next());
        codeTranscoding.ifPresent(transcoding -> serviceRequestLabMyHealthEu.getCode().getCoding().add(transcoding));

        // ReasonCode
        for (CodeableConcept codeableConcept : serviceRequestLabMyHealthEu.getReasonCode()) {
            Optional<Coding> reasonCodeTranscoding = getTranscoding(codeableConcept.getCoding().iterator().next());
            reasonCodeTranscoding.ifPresent(transcoding -> codeableConcept.getCoding().add(transcoding));
        }

        return serviceRequestLabMyHealthEu;
    }
}
