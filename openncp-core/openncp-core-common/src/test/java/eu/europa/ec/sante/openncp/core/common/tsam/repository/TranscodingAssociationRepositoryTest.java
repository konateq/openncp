package eu.europa.ec.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.DummyApplication;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.TranscodingAssociation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = DummyApplication.class)
@RunWith(SpringRunner.class)
public class TranscodingAssociationRepositoryTest {

    private static boolean setUpIsDone = false;

    private static CodeSystemConcept sourceCodeSystemConcept;

    private static CodeSystemConcept targetCodeSystemConcept;

    @Autowired
    private TranscodingAssociationRepository transcodingAssociationRepository;

    @Test
    public void testFindTranscodingAssociationsBySourceConcept() {
        List<TranscodingAssociation> transcodingAssociations = transcodingAssociationRepository.findTranscodingAssociationsBySourceConcept(sourceCodeSystemConcept);
        Assert.assertFalse(transcodingAssociations.isEmpty());
        Assert.assertEquals(targetCodeSystemConcept.getCode(), transcodingAssociations.get(0).getTargedConcept().getCode());
        Assert.assertEquals(targetCodeSystemConcept.getDefinition(), transcodingAssociations.get(0).getTargedConcept().getDefinition());
        Assert.assertEquals(sourceCodeSystemConcept.getCode(), transcodingAssociations.get(0).getSourceConcept().getCode());
        Assert.assertEquals(sourceCodeSystemConcept.getDefinition(), transcodingAssociations.get(0).getSourceConcept().getDefinition());
    }

    @Before
    public void prefillDatabase() {
        if (!setUpIsDone) {
            sourceCodeSystemConcept = buildCodeSystemConcept("123", "source concept");
            targetCodeSystemConcept = buildCodeSystemConcept("456", "target concept");
            TranscodingAssociation transcodingAssociation = new TranscodingAssociation();
            transcodingAssociation.setSourceConcept(sourceCodeSystemConcept);
            transcodingAssociation.setTargedConcept(targetCodeSystemConcept);
            transcodingAssociationRepository.save(transcodingAssociation);
            setUpIsDone=true;
        }

    }

    public CodeSystemConcept buildCodeSystemConcept(String code, String definition) {
        var codeSystemConcept = new CodeSystemConcept();
        codeSystemConcept.setCode(code);
        codeSystemConcept.setDefinition(definition);
        return codeSystemConcept;
    }
}
