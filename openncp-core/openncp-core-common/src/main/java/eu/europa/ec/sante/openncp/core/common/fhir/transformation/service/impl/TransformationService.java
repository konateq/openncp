package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.FHIRTransformationService;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.FHIRTranslationService;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformationService implements FHIRTransformationService {

    @Autowired
    public FHIRTranslationService translationService;

    @Override
    public TMResponseStructure translate(final Bundle fhirDocument, final String targetLanguageCode) {
        final TMResponseStructure responseStructure = translationService.translate(fhirDocument, targetLanguageCode);
        return responseStructure;
    }

    @Override
    public TMResponseStructure transcode(final Bundle fhirDocument) {
        return new TMResponseStructure(fhirDocument, null, null, null);
    }
}
