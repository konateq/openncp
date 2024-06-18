package eu.europa.ec.sante.openncp.api.server.handler;


import eu.europa.ec.sante.openncp.api.common.handler.BundleHandler;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.server.fhir.transcoding.TranscodingService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TranscodingBundleHandler implements BundleHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranscodingBundleHandler.class);

    private final TranscodingService transcodingService;

    public TranscodingBundleHandler(final TranscodingService transcodingService) {
        this.transcodingService = Validate.notNull(transcodingService);
    }

    @Override
    public Bundle handle(final Bundle bundle) {
        LOGGER.info("Transcoding FHIR bundle from national format to MyHealth@EU format");
        final TMResponseStructure transcodedBundle = transcodingService.transcode(bundle);
        return Validate.notNull(transcodedBundle.getFhirDocument());
    }
}
