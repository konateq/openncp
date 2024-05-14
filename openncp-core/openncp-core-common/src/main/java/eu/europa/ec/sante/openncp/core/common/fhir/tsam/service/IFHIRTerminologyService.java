package eu.europa.ec.sante.openncp.core.common.fhir.tsam.service;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.response.TSAMResponse;
import org.hl7.fhir.r4.model.Coding;

public interface IFHIRTerminologyService {

    TSAMResponse getConceptByCode(Coding coding);

    TSAMResponse getDesignationForConcept(Coding coding, String targetLanguageCode);
}
