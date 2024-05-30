package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.common.io.Resources;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.org.hl7.v3.Charset;
import eu.europa.ec.sante.openncp.core.server.TestApplication;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK, classes= TestApplication.class)
public class DiagnosticReportTranscodingLogicServiceTest extends AbstractTranscodingLogicServiceTest {

//    @Autowired
//    DiagnosticReportTranscodingLogicService diagnosticReportTranscodingLogicService;

    @Test
    public void testTranscode() throws IOException {

        DiagnosticReport input = parser.parseResource(DiagnosticReport.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("in/diagnosticReport-in.json"),
                StandardCharsets.UTF_8));

        DiagnosticReport expectedOutput = parser.parseResource(DiagnosticReport.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("out/diagnosticReport-out.json"),
                StandardCharsets.UTF_8));
        assertFhirResourcesAreEqual(expectedOutput, input);

        //diagnosticReportTranscodingLogicService.transcode(diagnosticReport);
    }
}
