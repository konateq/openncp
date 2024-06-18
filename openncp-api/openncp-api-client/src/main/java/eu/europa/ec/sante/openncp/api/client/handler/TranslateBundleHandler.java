package eu.europa.ec.sante.openncp.api.client.handler;

import eu.europa.ec.sante.openncp.api.common.handler.BundleHandler;
import eu.europa.ec.sante.openncp.common.Constant;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.TranslationService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class TranslateBundleHandler implements BundleHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslateBundleHandler.class);

    private final TranslationService translationService;
    private final ConfigurationManager configurationManager;

    public TranslateBundleHandler(final TranslationService translationService, final ConfigurationManager configurationManager) {
        this.translationService = Validate.notNull(translationService);
        this.configurationManager = configurationManager;

    }

    @Override
    public Bundle handle(final Bundle bundle) {
        final String targetLanguage = configurationManager.getProperty(Constant.LANGUAGE_CODE);
        LOGGER.info("Translating FHIR bundle from MyHealth@EU format into national language [{}]", targetLanguage);
        final TMResponseStructure translatedBundle = translationService.translate(bundle, targetLanguage);
        Validate.notNull(translatedBundle);
        logErrors(translatedBundle);
        logWarnings(translatedBundle);

        return translatedBundle.getFhirDocument();
    }

    private static void logErrors(final TMResponseStructure translatedBundle) {
        if (!translatedBundle.getErrors().isEmpty()) {
            for (final String error : translatedBundle.getErrors()) {
                LOGGER.error(error);
            }
        }
    }

    private static void logWarnings(final TMResponseStructure translatedBundle) {
        if (!translatedBundle.getWarnings().isEmpty()) {
            for (final String warning : translatedBundle.getWarnings()) {
                LOGGER.warn(warning);
            }
        }
    }
}
