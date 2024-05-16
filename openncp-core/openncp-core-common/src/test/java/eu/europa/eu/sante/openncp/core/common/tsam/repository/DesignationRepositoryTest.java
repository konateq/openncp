package eu.europa.eu.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.Designation;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.ValueSetVersion;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.DesignationRepository;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.ValueSetVersionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootApplication(scanBasePackages={"eu.europa.ec.sante.openncp.core.common.tsam"})
@RunWith(SpringRunner.class)
@EntityScan("eu.europa.ec.sante.openncp.core.common.tsam.*")
public class DesignationRepositoryTest {

    private static boolean setUpIsDone = false;

    private static final String CURRENT_STATUS = "current";

    @Autowired
    private ValueSetVersionRepository valueSetVersionRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Test
    public void testFindByValueSetVersion() {
        List<Designation> designations = designationRepository.findByValueSetVersion("202310");
        Assert.assertFalse(designations.isEmpty());
        Assert.assertEquals(2, designations.size());
    }

    @Test
    public void testGetDistinctLanguageCode() {
        List<Designation> designations = designationRepository.getDistinctLanguageCode();
        Assert.assertFalse(designations.isEmpty());
        Assert.assertEquals(1, designations.size());
        Assert.assertEquals("en-GB", designations.get(0).getLanguageCode());
    }

    @Before
    public void prefillDatabase() {
        if (!setUpIsDone) {
            valueSetVersionRepository.save(buildValueSetVersion(CURRENT_STATUS, "202310"));
            valueSetVersionRepository.save(buildValueSetVersion(CURRENT_STATUS, "202210"));
            setUpIsDone=true;
        }
    }

    public ValueSetVersion buildValueSetVersion(String status, String description) {

        var valueSetVersion = new ValueSetVersion();
        valueSetVersion.setDescription(description);
        valueSetVersion.setStatus(status);
        valueSetVersion.addConcept(buildCodeSystemConcept("123", "active"));
        valueSetVersion.addConcept(buildCodeSystemConcept("456", "active"));
        return valueSetVersion;
    }

    private CodeSystemConcept buildCodeSystemConcept(String code, String status) {
        var codeSystemConcept = new CodeSystemConcept();
        codeSystemConcept.setCode(code);
        codeSystemConcept.setStatus(status);
        codeSystemConcept.addDesignation(buildDesignation("en-GB", code+"_translation"));
        return codeSystemConcept;
    }

    private Designation buildDesignation(String languageCode, String translation) {
        var designation = new Designation();
        designation.setLanguageCode(languageCode);
        designation.setDesignation(translation);
        return designation;
    }
}
