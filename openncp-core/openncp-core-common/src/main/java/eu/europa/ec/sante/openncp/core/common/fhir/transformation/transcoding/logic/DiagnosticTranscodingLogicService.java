package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class DiagnosticTranscodingLogicService extends AbstractTranscodingLogicService<DiagnosticReport> {

    public DiagnosticTranscodingLogicService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DiagnosticReport;
    }

    @Override
    public void transcodeTypedResource(final DiagnosticReport typedResource) {
    }
}
