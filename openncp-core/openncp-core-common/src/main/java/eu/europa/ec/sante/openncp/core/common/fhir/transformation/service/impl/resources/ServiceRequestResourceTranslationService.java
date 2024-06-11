package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ServiceRequestLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestResourceTranslationService extends AbstractResourceTranslationService<ServiceRequestLabMyHealthEu> {

    public ServiceRequestResourceTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ServiceRequestLabMyHealthEu translateTypedResource(ServiceRequestLabMyHealthEu serviceRequest, String targetLanguage) {

        /** Code **/
        addTranslation(serviceRequest.getCode(), targetLanguage);

        /** Reason code **/
        for (CodeableConcept codeableConcept: serviceRequest.getReasonCode()) {
            addTranslation(codeableConcept, targetLanguage);
        }
        return serviceRequest;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Patient;
    }
}
