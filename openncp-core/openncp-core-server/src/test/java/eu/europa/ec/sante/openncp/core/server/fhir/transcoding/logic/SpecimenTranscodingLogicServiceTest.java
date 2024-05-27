package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.logic;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK, classes= TestApplication.class)
public class SpecimenTranscodingLogicServiceTest extends AbstractTranscodingLogicServiceTest {

//    @Autowired
//    SpecimenTranscodingLogicService specimenTranscodingLogicService;

    @Test
    public void testTranscode() throws IOException {

        Specimen input = parser.parseResource(Specimen.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("in/specimen-in.json"),
                StandardCharsets.UTF_8));

        Specimen expectedOutput = parser.parseResource(Specimen.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("out/specimen-out.json"),
                StandardCharsets.UTF_8));
        assertFhirResourcesAreEqual(expectedOutput, input);

        //diagnosticReportTranscodingLogicService.transcode(diagnosticReport);
    }
}
