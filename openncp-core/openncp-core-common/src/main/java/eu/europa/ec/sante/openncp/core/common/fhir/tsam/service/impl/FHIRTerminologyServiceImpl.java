package eu.europa.ec.sante.openncp.core.common.fhir.tsam.service.impl;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.CodeSystemVersion;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.Designation;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.service.IFHIRTerminologyService;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.dao.ITsamDao;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.exception.TSAMException;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.response.TSAMResponse;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.util.TSAMError;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Coding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class FHIRTerminologyServiceImpl implements IFHIRTerminologyService {

    @Autowired
    private ITsamDao dao;

    private static final String CURRENT = "current";

    private static final String DEFAULT_TRANSLATION_LANGUAGE = "en-GB";

    private static final String DEFAULT_TRANSCODING_LANGUAGE = "en-GB";

    private final Logger logger = LoggerFactory.getLogger(FHIRTerminologyServiceImpl.class);
    @Override
    public TSAMResponse getConceptByCode(Coding coding) {
        if (logger.isDebugEnabled()) {
            logger.debug("getConceptByCode BEGIN ('{}')", coding);
        }
        TSAMResponse response = new TSAMResponse(coding);
        try {
            var oid = eu.europa.ec.sante.openncp.core.common.fhir.tsam.cs.CodeSystem.getOidBasedOnUrl(coding.getSystem());
            CodeSystem codeSystem = dao.getCodeSystem(oid);

            // obtain a Concept by CodeSystemVersion or if it exists in any others versions available.
            Optional<CodeSystemConcept> concept;
            if (StringUtils.isNotBlank(coding.getVersion())) {
                CodeSystemVersion codeSystemVersion = dao.getVersion(coding.getVersion(), codeSystem);
                concept = dao.getConcept(coding.getCode(), codeSystemVersion);
            } else {
                List<Long> codeSystemVersionIds = dao.getCodeSystemVersionIds(oid);
                concept = dao.getConceptByCodeSystemVersionIds(coding.getCode(), codeSystemVersionIds);
            }

            if (concept.isEmpty()) {
                response.addError(TSAMError.ERROR_CODE_SYSTEM_CONCEPT_NOTFOUND);
                return response;
            } else {

                checkConceptStatus(concept.get(), response);

                // obtain Target Concept and Designation
                CodeSystemConcept target = dao.getTargetConcept(concept.get());
                List<Designation> designations;
                if (target == null) {
                    // if target concept is null, get designation for source concept
                    target = concept.get();
                    designations = dao.getSourceDesignation(target);
                } else {
                    designations = dao.getDesignation(target, DEFAULT_TRANSCODING_LANGUAGE);
                }
                checkConceptStatus(target, response);

                Designation designation = designations.get(0);
                if (target != null) {

                    response.setCode(target.getCode());
                    response.setDesignation(designation.getDesignation());

                    // obtain Target Code System Version
                    CodeSystemVersion targetVersion = target.getCodeSystemVersion();
                    checkTargetVersion(targetVersion);
                    response.setCodeSystemVersion(targetVersion.getLocalName());

                    // obtain Target Code System
                    CodeSystem targetCodeSystem = targetVersion.getCodeSystem();
                    checkTargetCodeSystem(targetCodeSystem);
                    response.setCodeSystem(targetCodeSystem.getOid());
                    response.setCodeSystemName(targetCodeSystem.getName());

                    checkManyDesignations(response, designations);
                }
            }
        } catch (TSAMException e) {
            // TSAM Exception considered as Warning
            logger.error("TSAMException: '{}'", e.getMessage());
            response.addError(e.getReason(), coding.getCode());
            logger.error(coding.getCode() + ", " + e, e);
        } catch (Exception e) {
            // Other Exception considered as Error
            logger.error("Exception: '{}'", e.getMessage());
            response.addError(TSAMError.ERROR_PROCESSING_ERROR, coding.getCode());
            logger.error(coding.getCode(), e);
        }

        logger.debug("getConceptByCode END");
        return response;
    }

    @Override
    public TSAMResponse getDesignationForConcept(Coding coding, String targetLanguageCode) {
        logger.debug("getDesignationForConcept BEGIN ('{}', language: '{}')", coding, targetLanguageCode);
        TSAMResponse response = new TSAMResponse(coding);
        try {
            var oid = eu.europa.ec.sante.openncp.core.common.fhir.tsam.cs.CodeSystem.getOidBasedOnUrl(coding.getSystem());
            CodeSystem codeSystem = dao.getCodeSystem(oid);

            // obtain CodeSystemVersion
            Optional<CodeSystemConcept> concept;
            if (StringUtils.isNotBlank(coding.getVersion())) {
                CodeSystemVersion codeSystemVersion = dao.getVersion(coding.getVersion(), codeSystem);
                concept = dao.getConcept(coding.getCode(), codeSystemVersion);
            } else {
                List<Long> codeSystemVersionIds = dao.getCodeSystemVersionIds(oid);
                concept = dao.getConceptByCodeSystemVersionIds(coding.getCode(), codeSystemVersionIds);
            }

            // obtain Designation
            if (StringUtils.isEmpty(targetLanguageCode)) {
                targetLanguageCode = DEFAULT_TRANSLATION_LANGUAGE;
            }
            if (concept.isEmpty()) {
                response.addError(TSAMError.ERROR_CODE_SYSTEM_CONCEPT_NOTFOUND);
                return response;
            }
            List<Designation> designations = dao.getDesignation(concept.get(), targetLanguageCode);
            Designation designation = designations.get(0);
            response.setDesignation(designation.getDesignation());

            checkConceptStatus(concept.get(), response);
            checkManyDesignations(response, designations);

        } catch (TSAMException e) {
            response.addError(e.getReason(), coding.getCode());
            logger.debug("No '{}' Translation available - {}", targetLanguageCode, coding.getCode());
        } catch (Exception e) {
            response.addError(TSAMError.ERROR_PROCESSING_ERROR, coding.getCode());
            logger.error(coding.getCode(), e);
        }
        logger.debug("getDesignationForConcept END");
        return response;
    }

    /**
     * check if target version is null
     *
     * @param version
     * @throws TSAMException
     */
    private void checkTargetVersion(CodeSystemVersion version) throws TSAMException {

        if (version == null) {
            throw new TSAMException(TSAMError.ERROR_EPSOS_VERSION_NOTFOUND);
        }
    }

    /**
     * @param concept
     * @param response
     */
    private void checkConceptStatus(CodeSystemConcept concept, TSAMResponse response) {

        if (concept != null && !CURRENT.equalsIgnoreCase(concept.getStatus())) {
            TSAMError warning = TSAMError.WARNING_CONCEPT_STATUS_NOT_CURRENT;
            response.addWarning(warning, concept.getCode());
            logger.debug("'{}': '{}'", response.getCoding(), warning);
        }
    }

    /**
     * Check if there is more than one designation and append error to response
     * structure if true
     *
     * @param response
     * @param designations
     */
    private void checkManyDesignations(TSAMResponse response, List<Designation> designations) {

        if (designations.size() > 1) {
            int preferred = 0;
            for (Designation designation : designations) {
                if (Boolean.TRUE.equals(designation.isPreferred())) {
                    preferred++;
                }
            }
            if (preferred == 0) {
                TSAMError warning = TSAMError.WARNING_MANY_DESIGNATIONS;
                response.addWarning(warning, response.getCode());
                if (logger.isDebugEnabled()) {
                    logger.debug("'{}': '{}'", response.getCoding(), warning);
                }
            }
        }
    }

    /**
     * Check if target CodeSystem exists, and its OID is not null
     *
     * @param codeSystem
     * @throws TSAMException
     */
    private void checkTargetCodeSystem(CodeSystem codeSystem) throws TSAMException {

        if (codeSystem == null) {
            throw new TSAMException(TSAMError.ERROR_EPSOS_CODE_SYSTEM_NOTFOUND);
        }
        if (StringUtils.isEmpty(codeSystem.getOid())) {
            throw new TSAMException(TSAMError.ERROR_EPSOS_CS_OID_NOTFOUND);
        }
    }
}
