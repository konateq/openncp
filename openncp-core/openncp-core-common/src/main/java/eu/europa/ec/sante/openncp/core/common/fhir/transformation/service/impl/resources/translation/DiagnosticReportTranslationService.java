package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.springframework.stereotype.Service;


@Service
public class DiagnosticReportTranslationService implements IDomainTranslationService<DiagnosticReport> {
    @Override
    public DiagnosticReport translate(DiagnosticReport diagnosticReport, String targetLanguage) {
        ToolingExtensions.addLanguageTranslation(diagnosticReport.getCode().getCoding().iterator().next().getDisplayElement(), targetLanguage, "Laboratoriumonderzoeken");
        return diagnosticReport;
    }
}
