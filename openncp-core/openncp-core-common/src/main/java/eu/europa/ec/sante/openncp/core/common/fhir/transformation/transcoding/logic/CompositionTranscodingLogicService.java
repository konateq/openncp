package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompositionTranscodingLogicService extends AbstractTranscodingLogicService<Composition> {

    public CompositionTranscodingLogicService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Composition;
    }

    @Override
    public void transcodeTypedResource(final Composition typedResource) {
        retrieveCoding(typedResource.getType()).ifPresent(coding -> {
            final Optional<Coding> transcodedCoding = getTranscoding(coding);
            transcodedCoding.ifPresent(transcoded -> typedResource.getType().getCoding().add(transcoded));
        });
    }
}
