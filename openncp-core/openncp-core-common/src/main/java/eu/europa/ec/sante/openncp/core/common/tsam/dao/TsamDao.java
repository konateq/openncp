package eu.europa.ec.sante.openncp.core.common.tsam.dao;

import eu.europa.ec.sante.openncp.core.common.tsam.CodeConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.RetrievedConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMException;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemVersion;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.Designation;

import java.util.List;
import java.util.Optional;

public interface TsamDao {
    /**
     * Check if association between CodeSystemConcept and ValueSet with valueSetOid exist.
     * If valueSetVersion is provided, checks if
     *
     * @param concept
     * @param valueSetOid
     * @param valueSetVersion - name of ValueSetVersion
     * @return true if association exist else false
     */
    boolean valueSetMatches(CodeSystemConcept concept, String valueSetOid, String valueSetVersion);

    /**
     * Retrieve Designation for Target CodeSystemConcept and selected language.
     *
     * @param target
     * @param lang
     * @return
     * @throws TSAMException if designation is not found
     */
    List<Designation> getDesignation(CodeSystemConcept target, String lang) throws TSAMException;

    /**
     * Retrieve Target CodeSystemConcept from Transcoding association by Source Concept.
     *
     * @param sourceConcept
     * @return
     * @throws TSAMException if target concept is not found
     */
    CodeSystemConcept getTargetConcept(CodeSystemConcept sourceConcept) throws TSAMException;

    /**
     * Retrieve CodeSystemConcept by Code and CodeSystemVersion.
     * Method looks for concept in the provided CodeSystemVersion and all its previous versions.
     *
     * @param code
     * @param codeSystemVersion
     * @return
     * @throws TSAMException if concept is not found
     */
    Optional<CodeSystemConcept> getConcept(String code, CodeSystemVersion codeSystemVersion) throws TSAMException;

    /**
     * Retrieve CodeSystemConcept by Code and CodeSystemVersion IDs list.
     * Method looks for concept which are part of the CodeSystemVersion IDs list.
     *
     * @param code
     * @param codeSystemVersionIds
     * @return
     * @throws TSAMException
     */
    Optional<CodeSystemConcept> getConceptByCodeSystemVersionIds(String code, List<Long> codeSystemVersionIds) throws TSAMException;

    /**
     * Retrieve CodeSystemVersion by LocalName and parent CodeSystem. If no version is provided (version is null),
     * method looks for versions of CodeSystem with status "current".
     *
     * @param version
     * @param system
     * @return
     * @throws TSAMException if version is not found
     */
    CodeSystemVersion getVersion(String version, CodeSystem system) throws TSAMException;

    /**
     * Retrieve CodeSystem by OID
     *
     * @param codeConcept
     * @return CodeSystem
     * @throws TSAMException if CodeSystem is not found
     */
    CodeSystem getCodeSystem(CodeConcept codeConcept) throws TSAMException;

    /**
     * Retrieve CodeSystemVersion IDs list by OID
     *
     * @param oid
     * @return
     */
    List<Long> getCodeSystemVersionIds(CodeConcept codeConcept);

    /**
     * Method to retrieve all concepts and their current designations for selected ValueSet ana language.
     * If language is null, default (en) language is used.
     *
     * @param valueSetOid
     * @param valueSetVersionName
     * @param language
     * @return
     */
    List<RetrievedConcept> getConcepts(String valueSetOid, String valueSetVersionName, String language);

    /**
     * @param target
     * @return
     * @throws TSAMException
     */
    List<Designation> getSourceDesignation(CodeSystemConcept target) throws TSAMException;

    /**
     * This method will return all available languages in the LTR.
     *
     * @return the list of LTR available languages, as a String list, containing the language codes;
     */
    List<String> getLtrLanguages();
}
