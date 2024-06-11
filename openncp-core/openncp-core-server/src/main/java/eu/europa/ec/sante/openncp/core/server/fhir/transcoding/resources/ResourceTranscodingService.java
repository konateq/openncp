package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

public interface ResourceTranscodingService<R extends Resource> {

    ResourceType getResourceType();

    boolean accepts(final Resource resource);

    R transcode(final Resource resource);

}
