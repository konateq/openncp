package eu.europa.eu.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.ValueSet;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.ValueSetVersion;
import eu.europa.ec.sante.openncp.core.common.tsam.repository.ValueSetRepository;
import eu.europa.eu.sante.openncp.core.common.DummyApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = DummyApplication.class)
@RunWith(SpringRunner.class)
public class ValueSetRepositoryTest {

    private static boolean setUpIsDone = false;

    private static ValueSet insertedValueSet;

    private static final String VALUE_SET_OID = "1.3.6.1.4.1.12559.11.10.1.3.1.42.46";

    @Autowired
    private ValueSetRepository valueSetRepository;

    @Test
    public void findByIdAndConceptsSuccess() {
        var valueSetVersions = valueSetRepository.findByOidAndConcepts(VALUE_SET_OID, insertedValueSet.getVersions().get(1).getConcepts().iterator().next().getId());
        Assert.assertFalse(valueSetVersions.isEmpty());
    }

    @Test
    public void findByIdAndConceptsEmptyResponse() {
        var valueSetVersions = valueSetRepository.findByOidAndConcepts(VALUE_SET_OID, 999L);
        Assert.assertTrue(valueSetVersions.isEmpty());
    }

    @Before
    public void prefillDatabase() {
        if (!setUpIsDone) {
            insertedValueSet = valueSetRepository.save(buildValueSet());
            setUpIsDone=true;
        }
    }

    private ValueSet buildValueSet() {
        var valueSet = new ValueSet();
        valueSet.setOid(VALUE_SET_OID);
        valueSet.addVersion(buildValueSetVersion("202210"));
        valueSet.addVersion(buildValueSetVersion("202310"));
        return valueSet;
    }

    private ValueSetVersion buildValueSetVersion(String description) {
        var valueSetVersion = new ValueSetVersion();
        valueSetVersion.setDescription(description);
        valueSetVersion.addConcept(buildCodeSystemConcept("1", "Active Ingredient"));
        return valueSetVersion;
    }

    private CodeSystemConcept buildCodeSystemConcept(String code, String definition) {
        var codeSystemConcept = new CodeSystemConcept();
        codeSystemConcept.setCode(code);
        codeSystemConcept.setDefinition(definition);
        return codeSystemConcept;
    }
}
