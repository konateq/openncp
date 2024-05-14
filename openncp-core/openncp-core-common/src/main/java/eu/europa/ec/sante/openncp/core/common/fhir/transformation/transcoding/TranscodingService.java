package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic.TranscodingLogicService;
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
public class TranscodingService implements ITranscodingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranscodingService.class);

    private final List<TranscodingLogicService<?>> transcodingLogicServices;

    public TranscodingService(final List<TranscodingLogicService<?>> transcodingLogicServices) {
        this.transcodingLogicServices = Validate.notNull(transcodingLogicServices);
    }

    @Override
    public TMResponseStructure transcode(final Bundle fhirDocument) {
        fhirDocument.getEntry().forEach(bundleEntryComponent -> {
            final Resource resource = bundleEntryComponent.getResource();
            retrieveTranscodingLogic(resource).ifPresentOrElse(transcodingLogicService -> transcodingLogicService.transcode(resource),
                                                               () -> LOGGER.warn("No transcoding logic service found for resource [{}]", resource));
        });

        return new TMResponseStructure(fhirDocument, "success", Collections.emptyList(), Collections.emptyList());
    }

    private Optional<TranscodingLogicService<?>> retrieveTranscodingLogic(final Resource resource) {
        return transcodingLogicServices.stream().filter(transcodingLogicService -> transcodingLogicService.accepts(resource)).findFirst();
    }
}
