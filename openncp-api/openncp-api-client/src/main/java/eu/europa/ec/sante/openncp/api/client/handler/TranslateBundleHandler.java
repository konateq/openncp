package eu.europa.ec.sante.openncp.api.client.handler;

import eu.europa.ec.sante.openncp.api.common.handler.BundleHandler;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.TranslationService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;


@Component
public class TranslateBundleHandler implements BundleHandler {

    private final TranslationService translationService;

    public TranslateBundleHandler(final TranslationService translationService) {
        this.translationService = Validate.notNull(translationService);
    }

    @Override
    public Bundle handle(final Bundle bundle) {
        final TMResponseStructure translatedBundle = translationService.translate(bundle, "en-GB");
        //todo error handling
        return translatedBundle.getFhirDocument();
    }
}
