package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.service.IFHIRTerminologyService;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class PatientTranscodingLogicService extends AbstractTranscodingLogicService<Patient> {

    public PatientTranscodingLogicService(final IFHIRTerminologyService fhirTerminologyService) {
        super(fhirTerminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Patient;
    }

    @Override
    public void transcodeTypedResource(final Patient typedResource) {

    }
}
