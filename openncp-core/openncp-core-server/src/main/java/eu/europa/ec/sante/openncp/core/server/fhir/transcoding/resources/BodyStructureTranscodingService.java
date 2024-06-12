package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.BodyStructureMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class BodyStructureTranscodingService extends AbstractResourceTranscodingService<BodyStructureMyHealthEu> {

    public BodyStructureTranscodingService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.BodyStructure;
    }

    @Override
    public BodyStructureMyHealthEu transcodeTypedResource(final BodyStructureMyHealthEu bodyStructureMyHealthEu) {

        transcodeCodeableConcept(bodyStructureMyHealthEu.getLaterality().getValue());
        transcodeCodeableConcept(bodyStructureMyHealthEu.getMorphology());
        transcodeCodeableConcept(bodyStructureMyHealthEu.getLocation());
        transcodeCodeableConceptsList(bodyStructureMyHealthEu.getLocationQualifier());

        return bodyStructureMyHealthEu;
    }
}
