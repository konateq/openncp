package eu.europa.ec.openncp.api.transformation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import eu.europa.ec.sante.openncp.core.common.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.transformation.domain.TMStatus;
import eu.europa.ec.sante.openncp.core.common.transformation.domain.TranscodeRequest;
import eu.europa.ec.sante.openncp.core.common.transformation.domain.TranslateRequest;
import eu.europa.ec.sante.openncp.core.common.transformation.persistence.model.Property;
import eu.europa.ec.sante.openncp.core.common.transformation.service.ITransformationService;
import eu.europa.ec.sante.openncp.core.common.transformation.service.PropertyService;
import eu.europa.ec.sante.openncp.core.common.transformation.util.Base64Util;
import eu.europa.ec.sante.openncp.core.common.tsam.error.TMError;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import java.util.*;

@RestController
public class TranslationsAndMappingsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String AVAILABLE_TRANSLATION_LANGUAGES_PROPERTY_KEY = "AVAILABLE_TRANSLATION_LANGUAGES";

    private final PropertyService propertyService;

    private final ITransformationService transformationService;

    public TranslationsAndMappingsController(PropertyService propertyService, ITransformationService transformationService) {
        this.propertyService = propertyService;
        this.transformationService = transformationService;
    }

    @PostConstruct
    public void propertiesInit() {
        logger.info("propertiesInit");
    }

    @GetMapping("/languages")
    public Set<String> retrieveAvailableTranslationLanguages() {
        logger.info("Entering retrieveAvailableTranslationLanguages() method");
        Property property = propertyService.getProperty(AVAILABLE_TRANSLATION_LANGUAGES_PROPERTY_KEY);
        var availableLanguageCodes = new HashSet<String>();
        StringTokenizer st = new StringTokenizer(property.getValue(), ",");

        // checking tokens
        while (st.hasMoreTokens()) {
            availableLanguageCodes.add(st.nextToken().trim());
        }
        return availableLanguageCodes;
    }

    @GetMapping(value = "/translateVS", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> retrieveValueSet(@RequestParam String oid, String targetLanguage){
        var valueSet = transformationService.translateValueSet(oid, targetLanguage);

        //Create a FHIR context
        FhirContext ctx = FhirContext.forR4();

        //Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        return ResponseEntity.ok(Base64.getEncoder().encodeToString(parser.encodeResourceToString(valueSet).getBytes()));
    }

    @PostMapping(value = "/translate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<TMResponseStructure> translateDocument(@RequestBody TranslateRequest translateRequest) {
        logger.info("Entering translateDocument() method");
        Document pivotCDA;
        try {
            pivotCDA = Base64Util.decode(translateRequest.getPivotCDA());
        } catch (Exception e) {
            TMResponseStructure tmResponseStructure = new TMResponseStructure();
            tmResponseStructure.setErrors(Collections.singletonList(TMError.BASE64_DOM_DECODING_EXCEPTION));
            tmResponseStructure.setStatus(TMStatus.ERROR);
            return ResponseEntity.badRequest().body(tmResponseStructure);
        }
        String targetLanguageCode = translateRequest.getTargetLanguageCode();
        logger.info("Translating CDA document in language [{}]", targetLanguageCode);
        return ResponseEntity.ok(transformationService.translate(pivotCDA, targetLanguageCode));
    }

    @PostMapping(value = "/transcode", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<TMResponseStructure> transcodeDocument(@RequestBody TranscodeRequest transcodeRequest) {
        logger.info("Entering transcodeDocument() method");
        Document friendlyCDA;
        try {
            friendlyCDA = Base64Util.decode(transcodeRequest.getFriendlyCDA());
        } catch (Exception e) {
            TMResponseStructure tmResponseStructure = new TMResponseStructure();
            tmResponseStructure.setErrors(Collections.singletonList(TMError.BASE64_DOM_DECODING_EXCEPTION));
            tmResponseStructure.setStatus(TMStatus.ERROR);
            return ResponseEntity.badRequest().body(tmResponseStructure);
        }
        logger.info("Transcoding CDA document into PIVOT");
        return ResponseEntity.ok(transformationService.transcode(friendlyCDA));
    }
}
