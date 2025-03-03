package eu.europa.ec.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.DummyApplication;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemVersion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = DummyApplication.class)
@RunWith(SpringRunner.class)
public class CodeSystemVersionRepositoryTest {

    private static boolean setUpIsDone = false;

    private static CodeSystem codeSystem;

    private static final String ATC_OID = "2.16.840.1.113883.6.73";

    @Autowired
    private CodeSystemVersionRepository codeSystemVersionRepository;

    @Autowired
    private CodeSystemRepository codeSystemRepository;

    @Test
    public void testFindByLocalNameAndCodeSystemSuccess() {
        var result = codeSystemVersionRepository.findByLocalNameAndCodeSystem("202310", codeSystem);
        Assert.assertTrue(result.isPresent());
    }

    @Test
    public void testFindByLocalNameAndCodeSystemEmptyResponse() {
        var result = codeSystemVersionRepository.findByLocalNameAndCodeSystem("NON_EXISTING_VERSION", codeSystem);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testFindByOidSuccess() {
        var result = codeSystemVersionRepository.findByOid(ATC_OID);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByOidEmptyResponse() {
        var result = codeSystemVersionRepository.findByOid("123");
        Assert.assertTrue(result.isEmpty());
    }

    private CodeSystem buildCodeSystem() {
        var codeSystem = new CodeSystem();
        codeSystem.setName("ATC");
        codeSystem.setOid(ATC_OID);
        codeSystem.addVersion(buildCodeSystemVersion("202210"));
        codeSystem.addVersion(buildCodeSystemVersion("202310"));
        return codeSystem;
    }


    @Before
    public void prefillDatabase() {
        if (!setUpIsDone) {
            codeSystem = codeSystemRepository.save(buildCodeSystem());
            setUpIsDone=true;
        }
    }

    private CodeSystemVersion buildCodeSystemVersion(String localName) {
        var codeSystemVersion = new CodeSystemVersion();
        codeSystemVersion.setLocalName(localName);
        return codeSystemVersion;
    }
}
