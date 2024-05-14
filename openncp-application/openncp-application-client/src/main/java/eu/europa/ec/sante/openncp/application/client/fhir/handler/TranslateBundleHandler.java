package eu.europa.ec.sante.openncp.application.client.fhir.handler;

import eu.europa.ec.sante.openncp.core.common.fhir.handler.BundleHandler;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;


@Component
public class TranslateBundleHandler implements BundleHandler {

    private final IFHIRTransformationService fhirTransformationService;

    public TranslateBundleHandler(final IFHIRTransformationService fhirTransformationService) {
        this.fhirTransformationService = Validate.notNull(fhirTransformationService);
    }

    @Override
    public Bundle handle(final Bundle bundle) {
        final TMResponseStructure translatedBundle = fhirTransformationService.translate(bundle, "en-GB");
        //todo error handling
        return translatedBundle.getFhirDocument();
    }
}
