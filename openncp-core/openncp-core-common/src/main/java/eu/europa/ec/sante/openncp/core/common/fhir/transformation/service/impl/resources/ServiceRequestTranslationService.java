package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ServiceRequestLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestTranslationService extends AbstractResourceTranslationService<ServiceRequestLabMyHealthEu> {

    public ServiceRequestTranslationService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ServiceRequestLabMyHealthEu translateTypedResource(final ServiceRequestLabMyHealthEu serviceRequest, final String targetLanguage) {

        translateCodeableConcept(serviceRequest.getCode(), targetLanguage);
        translateCodeableConceptsList(serviceRequest.getReasonCode(), targetLanguage);

        return serviceRequest;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Patient;
    }
}
