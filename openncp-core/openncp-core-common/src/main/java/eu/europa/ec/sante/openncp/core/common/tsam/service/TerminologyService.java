package eu.europa.ec.sante.openncp.core.common.tsam.service;

import eu.europa.ec.sante.openncp.core.common.tsam.RetrievedConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.CodeConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;

import java.util.List;
import java.util.Map;

/**
 * Component responsibilities:<br>
 * <li>1. Translating a given concept designation into the requested target language using the information present
 * in the Terminology Repository.</li>
 * <li>
 * 2. Transcoding a given local coded concept into the appropriate epSOS coded concept using the information present
 * in the Terminology Repository.</li>
 *
 */
public interface TerminologyService {


    TSAMResponseStructure getTargetConcept(CodeConcept localConcept);

    TSAMResponseStructure getDesignation(CodeConcept codeConcept, String targetLanguageCode);

    /**
     * Additional method for providing of all concepts and their designations from a specified value set.
     *
     * @param valueSetOid
     * @param valueSetVersionName
     * @param language
     * @return
     */
    List<RetrievedConcept> getValueSetConcepts(String valueSetOid, String valueSetVersionName, String language);

    /**
     * Additional method for retrieving the list of all available languages in the LTR
     *
     * @return the list of LTR available languages, as a String list, containing the language codes;
     */
    List<String> getLtrLanguages();
    
    /**
     * Additional method that returns all concepts of a specific version of a national CodeSystem for which there's a mapping in some ValueSet
     * (i.e., it does not return the full national CodeSystem, since that is not available within the LTR database. What is available
     * is the set of national concepts from a national CodeSystem that are mapped to international concepts of a ValueSet) 
     * 
     * @param codeConcept The codeConcept of the national CodeSystem
     * @param version The version of the national CodeSystem. If no version is provided (version is null),
     * method looks for versions of CodeSystem with status "current".
     * @return The list of the national CodeSystem concepts 
     */
    Map<CodeSystemConcept,CodeSystemConcept> getNationalCodeSystemMappedConcepts(CodeConcept codeConcept, String version);
}
