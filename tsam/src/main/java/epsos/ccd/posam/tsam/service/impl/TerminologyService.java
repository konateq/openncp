package epsos.ccd.posam.tsam.service.impl;

import epsos.ccd.posam.tsam.dao.ITsamDao;
import epsos.ccd.posam.tsam.exception.TSAMError;
import epsos.ccd.posam.tsam.exception.TSAMException;
import epsos.ccd.posam.tsam.model.CodeSystem;
import epsos.ccd.posam.tsam.model.CodeSystemConcept;
import epsos.ccd.posam.tsam.model.CodeSystemVersion;
import epsos.ccd.posam.tsam.model.Designation;
import epsos.ccd.posam.tsam.response.RetrievedConcept;
import epsos.ccd.posam.tsam.response.TSAMResponseStructure;
import epsos.ccd.posam.tsam.service.ITerminologyService;
import epsos.ccd.posam.tsam.util.CodedElement;
import epsos.ccd.posam.tsam.util.DebugUtils;
import epsos.ccd.posam.tsam.util.TsamConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Roman Repiscak
 * @author Organization: Posam
 * @author mail:roman.repiscak@posam.sk
 * @version 1.0, 2010, 11 August
 * @see ITerminologyService
 */
@Transactional(readOnly = true)
public class TerminologyService implements ITerminologyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminologyService.class);
    private static final String CURRENT = "current";
    private ITsamDao dao;
    private TsamConfiguration config;

    public TSAMResponseStructure getDesignationByEpSOSConcept(CodedElement epSOSRefConcept, String targetLanguageCode) {

        LOGGER.debug("getDesignationByEpSOSConcept BEGIN ('{}', lang: '{}')", epSOSRefConcept, targetLanguageCode);
        DebugUtils.showTransactionStatus("getDesignationByEpSOSConcept()");
        TSAMResponseStructure response = new TSAMResponseStructure(epSOSRefConcept);
        try {
            String code = epSOSRefConcept.getCode();
            String csOid = epSOSRefConcept.getOid();
            String csName = epSOSRefConcept.getCodeSystem();
            String version = epSOSRefConcept.getVersion();
            String vsOid = epSOSRefConcept.getVsOid();
            String vsVersion = epSOSRefConcept.getValueSetVersion();
            // obtain CodeSystem
            CodeSystem system = dao.getCodeSystem(csOid);
            checkCodeSystemName(system, csName, response);

            // obtain CodeSystemVersion
            CodeSystemVersion codeSystemVersion = dao.getVersion(version, system);

            // obtain Concept
            CodeSystemConcept concept = dao.getConcept(code, codeSystemVersion);

            // obtain Designation
            if (targetLanguageCode == null || "".equals(targetLanguageCode)) {
                targetLanguageCode = config.getTranslationLang();
            }
            List<Designation> designations = dao.getDesignation(concept, targetLanguageCode);
            Designation designation = designations.get(0);
            response.setDesignation(designation.getDesignation());

            checkConceptStatus(concept, response);
            checkManyDesignations(response, designations);
            checkValueSet(concept, vsOid, vsVersion, response);

        } catch (TSAMException e) {
            response.addError(e.getReason(), epSOSRefConcept.toString());
            LOGGER.warn(epSOSRefConcept + ", " + e.toString());
        } catch (Exception e) {
            response.addError(TSAMError.ERROR_PROCESSING_ERROR, epSOSRefConcept.toString());
            LOGGER.error(epSOSRefConcept.toString(), e);
        }
        LOGGER.debug("getDesignationByEpSOSConcept END");
        return response;
    }

    public TSAMResponseStructure getEpSOSConceptByCode(CodedElement localConcept) {

        LOGGER.debug("getEpSOSConceptByCode BEGIN ('{}')", localConcept);
        DebugUtils.showTransactionStatus("getEpSOSConceptByCode()");
        TSAMResponseStructure response = new TSAMResponseStructure(localConcept);

        try {
            String code = localConcept.getCode();
            String csName = localConcept.getCodeSystem();
            String csOid = localConcept.getOid();
            String csVersion = localConcept.getVersion();
            String vsOid = localConcept.getVsOid();
            String vsVersion = localConcept.getValueSetVersion();

            // obtain CodeSystem
            CodeSystem system = dao.getCodeSystem(csOid);
            checkCodeSystemName(system, csName, response);

            // optain CodeSystemVersion
            CodeSystemVersion codeSystemVersion = dao.getVersion(csVersion, system);

            // obtain Concept
            CodeSystemConcept concept = dao.getConcept(code, codeSystemVersion);
            checkConceptStatus(concept, response);

            // obtain Target Concept and Designation
            CodeSystemConcept target = dao.getTargetConcept(concept);
            List<Designation> designations;
            if (target == null) {
                // if target concept is null, get designation for source concept
                target = concept;
                designations = dao.getSourceDesignation(target);
            } else {
                designations = dao.getDesignation(target, config.getTranscodingLang());
            }
            checkConceptStatus(target, response);

            Designation designation = designations.get(0);
            if (target != null) {

                response.setCode(target.getCode());
                response.setDesignation(designation.getDesignation());

                // obtain Target Code System Version
                CodeSystemVersion targetVersion = target.getCodeSystemVersion();
                checkTargedVersion(targetVersion);
                response.setCodeSystemVersion(targetVersion.getLocalName());

                // obtain Target Code System
                CodeSystem targetCodeSystem = targetVersion.getCodeSystem();
                checkTargetCodeSystem(targetCodeSystem);
                response.setCodeSystem(targetCodeSystem.getOid());
                response.setCodeSystemName(targetCodeSystem.getName());

                checkValueSet(concept, vsOid, vsVersion, response);
                checkManyDesignations(response, designations);
            } else {
                //TODO: Review this method
            }
        } catch (TSAMException e) {
            response.addError(e.getReason(), localConcept.toString());
            LOGGER.warn(localConcept + ", " + e.toString(), e);
        } catch (Exception e) {
            response.addError(TSAMError.ERROR_PROCESSING_ERROR, localConcept.toString());
            LOGGER.error(localConcept.toString(), e);
        }

        LOGGER.debug("getEpSOSConceptByCode END");
        return response;
    }

    public List<RetrievedConcept> getValueSetConcepts(String valueSetOid, String valueSetVersionName, String language) {

        DebugUtils.showTransactionStatus("getValueSetConcepts()");
        return dao.getConcepts(valueSetOid, valueSetVersionName, language);
    }

    public List<String> getLtrLanguages() {

        DebugUtils.showTransactionStatus("getLtrLanguages()");
        return dao.getLtrLanguages();
    }

    private void checkCodeSystemName(CodeSystem codeSystem, String name, TSAMResponseStructure response) {

        DebugUtils.showTransactionStatus("checkCodeSystemName()");
        if (name == null || codeSystem == null || !name.equals(codeSystem.getName())) {
            String ctx;
            if (codeSystem != null) {
                ctx = codeSystem.getName() + " != " + name;
            } else {
                ctx = "Code System is null and  != " + name;
            }
            response.addWarning(TSAMError.WARNING_CODE_SYSETEM_NAME_DOESNT_MATCH, ctx);
            LOGGER.warn("[{}] '{}': '{}'", response.getInputCodedElement(), TSAMError.WARNING_CODE_SYSETEM_NAME_DOESNT_MATCH, ctx);
        }
    }

    /**
     * check if target version is null
     *
     * @param version
     * @throws TSAMException
     */
    private void checkTargedVersion(CodeSystemVersion version) throws TSAMException {

        DebugUtils.showTransactionStatus("checkTargedVersion()");
        if (version == null) {
            throw new TSAMException(TSAMError.ERROR_EPSOS_VERSION_NOTFOUND);
        }
    }

    /**
     * Checkif target CodeSystem exists, and its OID is not null
     *
     * @param codeSystem
     * @throws TSAMException
     */
    private void checkTargetCodeSystem(CodeSystem codeSystem) throws TSAMException {

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
    private void checkManyDesignations(TSAMResponseStructure response, List<Designation> designations) {

        DebugUtils.showTransactionStatus("checkManyDesignations()");

        if (designations.size() > 1) {
            int preferred = 0;
            for (Designation designation : designations) {
                if (Boolean.TRUE.equals(designation.isPreffered())) {
                    preferred++;
                }
            }
            if (preferred == 0) {
                TSAMError warning = TSAMError.WARNING_MANY_DESIGNATIONS;
                response.addWarning(warning, response.getCode());
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("'{}': '{}'", response.getInputCodedElement(), warning.toString());
                }
            }
        }
    }

    /**
     * Check if association between concept and ValueSetVersion with provided
     * oid exists and append error to response structure if not
     *
     * @param concept
     * @param vsOid
     * @param response
     */
    private void checkValueSet(CodeSystemConcept concept, String vsOid, String vsVersion, TSAMResponseStructure response) {

        if (vsOid != null) {
            boolean valueSetMatches = dao.valueSetMatches(concept, vsOid, vsVersion);
            if (!valueSetMatches) {
                String code = concept.getCode();
                String warnMsg = "CodeSystemConcept: " + code + ", ValueSetOid: " + vsOid;
                TSAMError warning = TSAMError.WARNING_VS_DOESNT_MATCH;
                response.addWarning(warning, warnMsg);
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("[{}]- '{}'", warning, warning.toString());
                }
            }
        }
    }

    /**
     * @param concept
     * @param response
     */
    private void checkConceptStatus(CodeSystemConcept concept, TSAMResponseStructure response) {

        if (concept != null && !CURRENT.equalsIgnoreCase(concept.getStatus())) {
            TSAMError warning = TSAMError.WARNING_CONCEPT_STATUS_NOT_CURRENT;
            response.addWarning(warning, concept.getCode());
            LOGGER.warn("'{}': '{}'", response.getInputCodedElement(), warning);
        }
    }

    public ITsamDao getDao() {
        return dao;
    }

    public void setDao(ITsamDao dao) {
        this.dao = dao;
    }

    public TsamConfiguration getConfig() {
        return config;
    }

    public void setConfig(TsamConfiguration config) {
        this.config = config;
    }
}
