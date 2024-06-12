package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DiagnosticReportLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;


@Service
public class DiagnosticReportTranslationService extends AbstractResourceTranslationService<DiagnosticReportLabMyHealthEu> {

    public DiagnosticReportTranslationService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public DiagnosticReportLabMyHealthEu translateTypedResource(final DiagnosticReportLabMyHealthEu diagnosticReportLabMyHealthEu, final String targetLanguage) {

        translateCodeableConceptsList(diagnosticReportLabMyHealthEu.getCategory(), targetLanguage);
        translateCodeableConcept(diagnosticReportLabMyHealthEu.getCode(), targetLanguage);

        return diagnosticReportLabMyHealthEu;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DiagnosticReport;
    }
}
