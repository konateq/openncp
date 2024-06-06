package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DiagnosticReportLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.springframework.stereotype.Service;


@Service
public class DiagnosticReportTranslationService extends AbstractTranslationService implements IDomainTranslationService<DiagnosticReportLabMyHealthEu> {

    public DiagnosticReportTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public DiagnosticReportLabMyHealthEu translate(DiagnosticReportLabMyHealthEu diagnosticReportLabMyHealthEu, String targetLanguage) {

        /** Language **/

        /** Category - Study type **/
        for (CodeableConcept codeableConcept: diagnosticReportLabMyHealthEu.getCategory()) {
            addTranslation(codeableConcept, targetLanguage);
        }

        /** Code **/
        addTranslation(diagnosticReportLabMyHealthEu.getCode(), targetLanguage);

        return diagnosticReportLabMyHealthEu;
    }
}
