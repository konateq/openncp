package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Assert;

public abstract class AbstractTranscodingLogicServiceTest {

    final protected FhirContext ctx = FhirContext.forR4();

    // Instantiate a new parser
    final protected IParser parser = ctx.newJsonParser();

    protected void assertFhirResourcesAreEqual(Resource resource1, Resource resource2) {
        Assert.assertEquals(parser.encodeResourceToString(resource1), parser.encodeResourceToString(resource2));

    }
}
