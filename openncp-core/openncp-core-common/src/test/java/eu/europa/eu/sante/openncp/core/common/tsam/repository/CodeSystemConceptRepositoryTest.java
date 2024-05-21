package eu.europa.eu.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemVersion;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.Designation;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.CodeSystemConceptRepository;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.CodeSystemRepository;
import eu.europa.eu.sante.openncp.core.common.DummyApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = DummyApplication.class)
@RunWith(SpringRunner.class)

public class CodeSystemConceptRepositoryTest {

    private static boolean setUpIsDone = false;

    private static final String ATC_OID = "2.16.840.1.113883.6.73";

    private static CodeSystem insertedCodeSystem;

    private static Long codeSystemVersionId;

    @Autowired
    private CodeSystemRepository codeSystemRepository;

    @Autowired
    private CodeSystemConceptRepository codeSystemConceptRepository;

    @Test
    public void testFindByCodeAndCodeSystemVersionSuccess() {
        Optional<CodeSystemConcept> result = codeSystemConceptRepository.findByCodeAndCodeSystemVersionId("123", codeSystemVersionId);
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals("123", result.get().getCode());
    }

    @Test
    public void testFindByCodeAndNonExistingCodeSystemVersion() {
        Optional<CodeSystemConcept> result = codeSystemConceptRepository.findByCodeAndCodeSystemVersionId("456", Long.MAX_VALUE);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetDesignations() {
        var designations = codeSystemConceptRepository.findDesignationByIdAndDesignationLanguageCode(insertedCodeSystem.getCodeSystemVersions().iterator().next().getConcepts().iterator().next().getId(), "en-GB");
        Assert.assertFalse(designations.isEmpty());
    }

    @Before
    public void prefillDatabase() {
        if (!setUpIsDone) {
            insertedCodeSystem = codeSystemRepository.save(buildCodeSystem());
            codeSystemVersionId = insertedCodeSystem.getCodeSystemVersions().get(0).getId();
            setUpIsDone=true;
        }

    }

    private CodeSystem buildCodeSystem() {
        var codeSystem = new CodeSystem();
        codeSystem.setName("ATC");
        codeSystem.setOid(ATC_OID);
        codeSystem.addVersion(buildCodeSystemVersion("202310"));
        return codeSystem;
    }

    private CodeSystemVersion buildCodeSystemVersion(String localName) {
        var codeSystemVersion = new CodeSystemVersion();
        codeSystemVersion.setLocalName(localName);
        codeSystemVersion.addConcept(buildCodeSystemConcept("123", "active"));
        return codeSystemVersion;
    }

    private CodeSystemConcept buildCodeSystemConcept(String code, String status) {
        var codeSystemConcept = new CodeSystemConcept();
        codeSystemConcept.setCode(code);
        codeSystemConcept.setStatus(status);
        codeSystemConcept.addDesignation(buildDesignation("en-GB", "translation"));
        return codeSystemConcept;
    }

    private Designation buildDesignation(String languageCode, String translation) {
        var designation = new Designation();
        designation.setLanguageCode(languageCode);
        designation.setDesignation(translation);
        return designation;
    }
}

