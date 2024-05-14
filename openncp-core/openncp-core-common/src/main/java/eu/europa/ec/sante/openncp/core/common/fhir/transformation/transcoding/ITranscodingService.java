package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import org.hl7.fhir.r4.model.Bundle;

public interface ITranscodingService {

    TMResponseStructure transcode(Bundle FHIRDocument);
}
