package eu.europa.ec.sante.openncp.core.common.tsam.service;

import eu.europa.ec.sante.openncp.core.common.ihe.tsam.util.DebugUtils;
import eu.europa.ec.sante.openncp.core.common.tsam.CodeConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.RetrievedConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMException;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.tsam.config.TsamConfiguration;
import eu.europa.ec.sante.openncp.core.common.tsam.dao.TsamDao;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemVersion;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.Designation;
import eu.europa.ec.sante.openncp.core.common.tsam.error.TSAMError;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TerminologyServiceImpl implements TerminologyService {

    private static final String CURRENT = "current";
    private final Logger logger = LoggerFactory.getLogger(TerminologyServiceImpl.class);
    private TsamDao dao;
    private TsamConfiguration config;

    public TerminologyServiceImpl(final TsamDao dao, final TsamConfiguration config) {
        this.dao = Validate.notNull(dao);
        this.config = Validate.notNull(config);
    }

    public TSAMResponseStructure getDesignation(final CodeConcept codeConcept, final String targetLanguageCode) {

        logger.debug("getDesignation BEGIN ('{}', lang: '{}')", codeConcept, targetLanguageCode);
        DebugUtils.showTransactionStatus("getDesignationByEpSOSConcept()");
        final TSAMResponseStructure response = new TSAMResponseStructure(codeConcept);
        try {
            final Optional<CodeSystemConcept> concept = retrieveCodeSystemConcept(codeConcept, response);

            if (concept.isEmpty()) {
                return response;
            } else {
                final String targetLanguageCodeToUse = StringUtils.isNotEmpty(targetLanguageCode) ? targetLanguageCode : config.getTranslationLang();
                final CodeSystemConcept conceptToTranslate = concept.get();
                final List<Designation> designations = dao.getDesignation(conceptToTranslate, targetLanguageCodeToUse);
                final Designation designation = designations.get(0);
                response.setDesignation(designation.getDesignation());

                checkConceptStatus(conceptToTranslate, response);
                checkManyDesignations(response, designations);
                checkValueSet(conceptToTranslate, codeConcept, response);
            }


        } catch (final TSAMException e) {
            response.addError(e.getReason(), codeConcept.toString());
            logger.debug("No '{}' Translation available - {}", targetLanguageCode, codeConcept);
        } catch (final Exception e) {
            response.addError(TSAMError.ERROR_PROCESSING_ERROR, codeConcept.toString());
            logger.error(codeConcept.toString(), e);
        }
        logger.debug("getDesignationByEpSOSConcept END");
        return response;
    }

    private Optional<CodeSystemConcept> retrieveCodeSystemConcept(final CodeConcept codeConcept, final TSAMResponseStructure response) throws TSAMException {
        final CodeSystem codeSystem = dao.getCodeSystem(codeConcept);
        codeConcept.getCodeSystemName().ifPresent(codeSystemName -> checkCodeSystemName(codeSystem, codeSystemName, response));

        // obtain CodeSystemVersion
        final Optional<CodeSystemConcept> concept;
        if (codeConcept.getCodeSystemVersion().isPresent()) {
            final CodeSystemVersion codeSystemVersion = dao.getVersion(codeConcept.getCodeSystemVersion().get(), codeSystem);
            concept = dao.getConcept(codeConcept.getCode(), codeSystemVersion);
        } else {
            final List<Long> codeSystemVersionIds = dao.getCodeSystemVersionIds(codeConcept);
            concept = dao.getConceptByCodeSystemVersionIds(codeConcept.getCode(), codeSystemVersionIds);
        }
        return concept;
    }

    public TSAMResponseStructure getTargetConcept(final CodeConcept codeConcept) {

        logger.debug("getTargetConcept BEGIN ('{}')", codeConcept);
        DebugUtils.showTransactionStatus("getEpSOSConceptByCode()");
        final TSAMResponseStructure response = new TSAMResponseStructure(codeConcept);

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Searching Concept [{}]", codeConcept);
            }
            final Optional<CodeSystemConcept> concept = retrieveCodeSystemConcept(codeConcept, response);

            if (concept.isEmpty()) {
                return response;
            }
            final CodeSystemConcept codeSystemConcept = concept.get();
            checkConceptStatus(codeSystemConcept, response);

            // obtain Target Concept and Designation
            CodeSystemConcept target = dao.getTargetConcept(codeSystemConcept);
            final List<Designation> designations;
            if (target == null) {
                logger.debug("No target concept found. Retrieve designation for source concept.");
                target = codeSystemConcept;
                designations = dao.getSourceDesignation(target);
            } else {
                designations = dao.getDesignation(target, config.getTranscodingLang());
            }
            checkConceptStatus(target, response);

            final Designation designation = designations.get(0);
            if (target != null) {

                response.setCode(target.getCode());
                response.setDesignation(designation.getDesignation());

                // obtain Target Code System Version
                final CodeSystemVersion targetVersion = target.getCodeSystemVersion();
                checkTargetVersion(targetVersion);
                response.setCodeSystemVersion(targetVersion.getLocalName());

                // obtain Target Code System
                final CodeSystem targetCodeSystem = targetVersion.getCodeSystem();
                checkTargetCodeSystem(targetCodeSystem);
                response.setCodeSystem(targetCodeSystem.getOid());
                response.setCodeSystemName(targetCodeSystem.getName());

                checkValueSet(codeSystemConcept, codeConcept, response);
                checkManyDesignations(response, designations);
            }
        } catch (final TSAMException e) {
            // TSAM Exception considered as Warning
            logger.error("TSAMException: '{}'", e.getReason().getDescription());
            response.addError(e.getReason(), codeConcept.toString());
            logger.error(codeConcept + ", " + e, e);
        } catch (final Exception e) {
            // Other Exception considered as Error
            logger.error("Exception: '{}'", e.getMessage());
            response.addError(TSAMError.ERROR_PROCESSING_ERROR, codeConcept.toString());
            logger.error(codeConcept.toString(), e);
        }

        logger.debug("getEpSOSConceptByCode END");
        return response;

    }


    public List<RetrievedConcept> getValueSetConcepts(final String valueSetOid, final String valueSetVersionName, final String language) {

        DebugUtils.showTransactionStatus("getValueSetConcepts()");
        return dao.getConcepts(valueSetOid, valueSetVersionName, language);
    }

    public List<String> getLtrLanguages() {

        DebugUtils.showTransactionStatus("getLtrLanguages()");
        return dao.getLtrLanguages();
    }

    private void checkCodeSystemName(final CodeSystem codeSystem, final String name, final TSAMResponseStructure response) {

        DebugUtils.showTransactionStatus("checkCodeSystemName()");
        if (name == null || codeSystem == null || !name.equals(codeSystem.getName())) {
            final String ctx;
            if (codeSystem != null) {
                ctx = codeSystem.getName() + " != " + name;
            } else {
                ctx = "Code System is null and  != " + name;
            }
            response.addWarning(TSAMError.WARNING_CODE_SYSTEM_NAME_DOESNT_MATCH, ctx);
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] '{}': '{}'", response.getCodeConcept(), TSAMError.WARNING_CODE_SYSTEM_NAME_DOESNT_MATCH, ctx);
            }
        }
    }

    /**
     * check if target version is null
     *
     * @param version
     * @throws TSAMException
     */
    private void checkTargetVersion(final CodeSystemVersion version) throws TSAMException {

        DebugUtils.showTransactionStatus("checkTargetVersion()");
        if (version == null) {
            throw new TSAMException(TSAMError.ERROR_EPSOS_VERSION_NOTFOUND);
        }
    }

    /**
     * Check if target CodeSystem exists, and its OID is not null
     *
     * @param codeSystem
     * @throws TSAMException
     */
    private void checkTargetCodeSystem(final CodeSystem codeSystem) throws TSAMException {

        DebugUtils.showTransactionStatus("checkTargetCodeSystem()");
        if (codeSystem == null) {
            throw new TSAMException(TSAMError.ERROR_EPSOS_CODE_SYSTEM_NOTFOUND);
        }
        if (codeSystem.getOid() == null || "".equals(codeSystem.getOid())) {
            throw new TSAMException(TSAMError.ERROR_EPSOS_CS_OID_NOTFOUND);
        }
    }

    /**
     * Check if there is more than one designation and append error to response
     * structure if true
     *
     * @param response
     * @param designations
     */
    private void checkManyDesignations(final TSAMResponseStructure response, final List<Designation> designations) {

        DebugUtils.showTransactionStatus("checkManyDesignations()");

        if (designations.size() > 1) {
            int preferred = 0;
            for (final Designation designation : designations) {
                if (designation.isPreferred()) {
                    preferred++;
                }
            }
            if (preferred == 0) {
                final TSAMError warning = TSAMError.WARNING_MANY_DESIGNATIONS;
                response.addWarning(warning, response.getCode());
                if (logger.isDebugEnabled()) {
                    logger.debug("'{}': '{}'", response.getCodeConcept(), warning);
                }
            }
        }
    }

    /**
     * Check if association between concept and ValueSetVersion with provided
     * oid exists and append error to response structure if not
     *
     * @param concept
     * @param codeConcept
     * @param response
     */
    private void checkValueSet(final CodeSystemConcept concept, final CodeConcept codeConcept, final TSAMResponseStructure response) {

        if (codeConcept.canCheckValueSet()) {
            final boolean valueSetMatches = dao.valueSetMatches(concept, codeConcept.getValueSetOid().get(), codeConcept.getValueSetVersion().get());
            if (!valueSetMatches) {
                final String code = concept.getCode();
                final String warnMsg = "CodeSystemConcept: " + code + ", ValueSetOid: " + codeConcept.getValueSetOid().get();
                final TSAMError warning = TSAMError.WARNING_VS_DOESNT_MATCH;
                response.addWarning(warning, warnMsg);
                if (logger.isDebugEnabled()) {
                    logger.debug("[{}]- '{}'", warning, warning);
                }
            }
        }
    }

    /**
     * @param concept
     * @param response
     */
    private void checkConceptStatus(final CodeSystemConcept concept, final TSAMResponseStructure response) {

        if (concept != null && !CURRENT.equalsIgnoreCase(concept.getStatus())) {
            final TSAMError warning = TSAMError.WARNING_CONCEPT_STATUS_NOT_CURRENT;
            response.addWarning(warning, concept.getCode());
            if (logger.isDebugEnabled()) {
                logger.debug("'{}': '{}'", response.getCodeConcept(), warning);
            }
        }
    }

    public TsamDao getDao() {
        return dao;
    }

    public void setDao(final TsamDao dao) {
        this.dao = dao;
    }

    public TsamConfiguration getConfig() {
        return config;
    }

    public void setConfig(final TsamConfiguration config) {
        this.config = config;
    }

    public Map<CodeSystemConcept, CodeSystemConcept> getNationalCodeSystemMappedConcepts(final CodeConcept codeConcept, final String version) {
        logger.debug("OID: {} | Version: {}", codeConcept.getCodeSystemOid(), version);
        Map<CodeSystemConcept, CodeSystemConcept> mappedConcepts = null;

        try {
            // First we get the national CodeSystem from its OID
            final CodeSystem nationalCodeSystem = dao.getCodeSystem(codeConcept);
            logger.debug("National CodeSystem: ID: {} | OID: {} | Name: {}", nationalCodeSystem.getId(), nationalCodeSystem.getOid(), nationalCodeSystem.getName());

            // Then we get the national CodeSystem version
            final CodeSystemVersion nationalCodeSystemVersion = dao.getVersion(version, nationalCodeSystem);
            logger.debug("National CodeSystem version: ID: {} | FullName: {} | LocalName: {} | Status: {}", nationalCodeSystemVersion.getId(),
                    nationalCodeSystemVersion.getFullName(), nationalCodeSystemVersion.getLocalName(), nationalCodeSystemVersion.getStatus());

            // Finally we get the concepts of that CodeSystem version
            final List<CodeSystemConcept> concepts = nationalCodeSystemVersion.getConcepts();
            concepts.forEach(concept -> logger.debug("code: {} | definition: {}", concept.getCode(), concept.getDefinition()));

            // Then we get their mapped concepts
            mappedConcepts = new HashMap<>();
            for (final CodeSystemConcept sourceConcept : concepts) {
                final CodeSystemConcept targetConcept = dao.getTargetConcept(sourceConcept);
                mappedConcepts.put(sourceConcept, targetConcept);
            }
            mappedConcepts.forEach((sourceConcept, targetConcept) -> logger.debug("Mapping: {} -> {}", sourceConcept.getDefinition(), targetConcept.getCode()));
        } catch (final TSAMException e) {
            logger.error("TSAMException: '{}'", e.getMessage());
        }
        return mappedConcepts;
    }
}
