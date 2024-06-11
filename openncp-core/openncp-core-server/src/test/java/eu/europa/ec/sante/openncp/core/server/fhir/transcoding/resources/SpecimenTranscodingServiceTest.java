package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SpecimenTranscodingServiceTest extends AbstractTranscodingServiceTest {


    @Test
    public void testTranscode() throws IOException {

        Specimen input = parser.parseResource(Specimen.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("in/specimen-in.json"),
                StandardCharsets.UTF_8));

        Specimen expectedOutput = parser.parseResource(Specimen.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("out/specimen-out.json"),
                StandardCharsets.UTF_8));
        assertFhirResourcesAreEqual(expectedOutput, input);

    }
}
