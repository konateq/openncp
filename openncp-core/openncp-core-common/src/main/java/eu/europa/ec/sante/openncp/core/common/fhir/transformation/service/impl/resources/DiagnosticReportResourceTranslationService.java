package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DiagnosticReportLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;


@Service
public class DiagnosticReportResourceTranslationService extends AbstractResourceTranslationService<DiagnosticReportLabMyHealthEu> {

    public DiagnosticReportResourceTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public DiagnosticReportLabMyHealthEu translateTypedResource(DiagnosticReportLabMyHealthEu diagnosticReportLabMyHealthEu, String targetLanguage) {

        /** Category - Study type **/
        for (CodeableConcept codeableConcept: diagnosticReportLabMyHealthEu.getCategory()) {
            addTranslation(codeableConcept, targetLanguage);
        }

        /** Code **/
        addTranslation(diagnosticReportLabMyHealthEu.getCode(), targetLanguage);

        return diagnosticReportLabMyHealthEu;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DiagnosticReport;
    }
}
