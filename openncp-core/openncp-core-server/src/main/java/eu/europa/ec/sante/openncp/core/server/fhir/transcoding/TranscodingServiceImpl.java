package eu.europa.ec.sante.openncp.core.server.fhir.transcoding;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources.ResourceTranscodingService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TranscodingServiceImpl implements TranscodingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranscodingServiceImpl.class);

    private final List<ResourceTranscodingService<? extends Resource>> resourceTranscodingServices;

    public TranscodingServiceImpl(final List<ResourceTranscodingService<? extends Resource>> resourceTranscodingServices) {
        this.resourceTranscodingServices = Validate.notNull(resourceTranscodingServices);
    }

    @Override
    public TMResponseStructure transcode(final Bundle fhirDocument) {
        fhirDocument.getEntry().forEach(bundleEntryComponent -> {
            final Resource resource = bundleEntryComponent.getResource();
            retrieveTranscodingLogic(resource).ifPresentOrElse(resourceTranscodingService -> resourceTranscodingService.transcode(resource),
                                                               () -> LOGGER.warn("No transcoding logic service found for resource [{}]", resource));
        });

        return new TMResponseStructure(fhirDocument, "success", Collections.emptyList(), Collections.emptyList());
    }

    private Optional<ResourceTranscodingService<?>> retrieveTranscodingLogic(final Resource resource) {
        return resourceTranscodingServices.stream().filter(resourceTranscodingService -> resourceTranscodingService.accepts(resource)).findFirst();
    }
}
