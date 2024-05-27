package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.logic;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

public interface TranscodingLogicService<R extends Resource> {

    ResourceType getResourceType();

    boolean accepts(final Resource resource);

    void transcode(final Resource resource);
}
