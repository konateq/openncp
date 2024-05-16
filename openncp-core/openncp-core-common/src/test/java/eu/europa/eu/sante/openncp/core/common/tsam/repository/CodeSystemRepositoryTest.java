package eu.europa.eu.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.CodeSystemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootApplication(scanBasePackages={"eu.europa.ec.sante.openncp.core.common.tsam"})
@RunWith(SpringRunner.class)
@EntityScan("eu.europa.ec.sante.openncp.core.common.tsam.*")
public class CodeSystemRepositoryTest {

    private static boolean setUpIsDone = false;

    private static CodeSystem insertedCodeSystem;

    private static final String ATC_OID = "2.16.840.1.113883.6.73";

    @Autowired
    private CodeSystemRepository codeSystemRepository;

    @Test
    public void testFindByOidSuccess() {
        Optional<CodeSystem> result = codeSystemRepository.findByOid(ATC_OID);
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(insertedCodeSystem, result.get());
    }

    @Test
    public void testFindByOidEmptyResponse() {
        Optional<CodeSystem> result = codeSystemRepository.findByOid("1.2.3");
        Assert.assertFalse(result.isPresent());
    }

    @Before
    public void prefillDatabase() {
        if (!setUpIsDone) {
            insertedCodeSystem = codeSystemRepository.save(buildCodeSystem());
        }
        setUpIsDone=true;
    }

    private CodeSystem buildCodeSystem() {
        var codeSystem = new CodeSystem();
        codeSystem.setName("ATC");
        codeSystem.setOid(ATC_OID);
        return codeSystem;
    }

}
