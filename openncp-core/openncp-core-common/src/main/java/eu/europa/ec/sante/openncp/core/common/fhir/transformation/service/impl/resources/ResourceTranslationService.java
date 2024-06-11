package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

public interface ResourceTranslationService<R extends Resource> {

        ResourceType getResourceType();

        boolean accepts(final Resource resource);

        R translate(final Resource resource, final String targetLanguage);
}
