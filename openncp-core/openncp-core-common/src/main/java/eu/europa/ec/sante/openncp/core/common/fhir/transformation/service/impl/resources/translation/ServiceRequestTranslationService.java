package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestTranslationService implements IDomainTranslationService<ServiceRequest> {
    @Override
    public ServiceRequest translate(ServiceRequest patient, String targetLanguage) {
        return null;
    }
}
