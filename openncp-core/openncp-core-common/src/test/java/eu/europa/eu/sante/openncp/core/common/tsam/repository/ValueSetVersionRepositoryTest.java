package eu.europa.eu.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.ValueSet;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.ValueSetVersion;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.ValueSetRepository;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.ValueSetVersionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootApplication(scanBasePackages={"eu.europa.ec.sante.openncp.core.common.tsam.repository"})
@RunWith(SpringRunner.class)
@EntityScan("eu.europa.ec.sante.openncp.core.common.tsam.*")
public class ValueSetVersionRepositoryTest {

    private static boolean setUpIsDone = false;

    private static final String CURRENT_STATUS = "current";

    private static final String VALUE_SET_OID = "1.3.6.1.4.1.12559.11.10.1.3.1.42.46";

    @Autowired
    private ValueSetVersionRepository valueSetVersionRepository;

    @Autowired
    private ValueSetRepository valueSetRepository;

    @Test
    public void testFindValueSetVersionByStatusAndValueSet() {
        ValueSetVersion valueSetVersion = valueSetVersionRepository.findValueSetVersionByStatusAndValueSetOid(CURRENT_STATUS, VALUE_SET_OID);
        Assert.assertNotNull(valueSetVersion);
        Assert.assertEquals("202310", valueSetVersion.getDescription());
    }

    @Test
    public void testFindValueSetVersionByDescriptionAndValueSet() {
        ValueSetVersion valueSetVersion = valueSetVersionRepository.findValueSetVersionByDescriptionAndValueSetOid("202210", VALUE_SET_OID);
        Assert.assertNotNull(valueSetVersion);
        Assert.assertEquals("202210", valueSetVersion.getDescription());
    }

    @Before
    public void prefillDatabase() {
        if (!setUpIsDone) {
            var insertedValueSet = valueSetRepository.save(buildValueSet());
            setUpIsDone = true;
        }
    }

    private ValueSet buildValueSet() {
        var valueSet = new ValueSet();
        valueSet.setOid(VALUE_SET_OID);
        valueSet.addVersion(buildValueSetVersion(null, "202210"));
        valueSet.addVersion(buildValueSetVersion(CURRENT_STATUS, "202310"));
        return valueSet;
    }

    public ValueSetVersion buildValueSetVersion(String status, String description) {
        var valueSetVersion = new ValueSetVersion();
        valueSetVersion.setDescription(description);
        valueSetVersion.setStatus(status);
        return valueSetVersion;
    }
}
