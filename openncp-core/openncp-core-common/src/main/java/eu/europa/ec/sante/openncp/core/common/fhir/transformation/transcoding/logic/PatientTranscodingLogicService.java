package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class PatientTranscodingLogicService extends AbstractTranscodingLogicService<Patient> {

    public PatientTranscodingLogicService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Patient;
    }

    @Override
    public void transcodeTypedResource(final Patient typedResource) {

    }
}
