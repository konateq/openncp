package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DiagnosticReportLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiagnosticReportTranscodingService extends AbstractResourceTranscodingService<DiagnosticReportLabMyHealthEu> {

    public DiagnosticReportTranscodingService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DiagnosticReport;
    }

    @Override
    public DiagnosticReportLabMyHealthEu transcodeTypedResource(final DiagnosticReportLabMyHealthEu diagnosticReportLabMyHealthEu) {

        // Category: studyType
        for (CodeableConcept codeableConcept : diagnosticReportLabMyHealthEu.getCategory()) {
            Optional<Coding> studyTypeTranscoding = getTranscoding(codeableConcept.getCoding().iterator().next());
            studyTypeTranscoding.ifPresent(transcoding -> codeableConcept.getCoding().add(transcoding));
        }

        // Code
        Optional<Coding> codeTranscoding = getTranscoding(diagnosticReportLabMyHealthEu.getCode().getCoding().iterator().next());
        codeTranscoding.ifPresent(transcoding -> diagnosticReportLabMyHealthEu.getCode().getCoding().add(transcoding));

        return diagnosticReportLabMyHealthEu;
    }
}
