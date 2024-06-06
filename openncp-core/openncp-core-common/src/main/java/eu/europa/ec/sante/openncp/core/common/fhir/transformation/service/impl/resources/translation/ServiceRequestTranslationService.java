package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ServiceRequestLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestTranslationService extends AbstractTranslationService implements IDomainTranslationService<ServiceRequestLabMyHealthEu> {

    public ServiceRequestTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ServiceRequestLabMyHealthEu translate(ServiceRequestLabMyHealthEu serviceRequest, String targetLanguage) {

        /** Code **/
        addTranslation(serviceRequest.getCode(), targetLanguage);

        /** Reason code **/
        for (CodeableConcept codeableConcept: serviceRequest.getReasonCode()) {
            addTranslation(codeableConcept, targetLanguage);
        }
        return serviceRequest;
    }
}
