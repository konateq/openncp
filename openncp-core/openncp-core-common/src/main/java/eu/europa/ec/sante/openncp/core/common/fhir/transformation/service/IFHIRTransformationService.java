package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import org.hl7.fhir.r4.model.Bundle;

public interface IFHIRTransformationService {

    TMResponseStructure translate(Bundle FhirDocument, String targetLanguageCode);

    TMResponseStructure transcode(Bundle FhirDocument);
}
