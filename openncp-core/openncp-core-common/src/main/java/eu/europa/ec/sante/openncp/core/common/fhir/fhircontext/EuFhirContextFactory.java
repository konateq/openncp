package eu.europa.ec.sante.openncp.core.common.fhir.fhircontext;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.ParserOptions;
import ca.uhn.fhir.parser.LenientErrorHandler;
import eu.europa.ec.sante.openncp.core.common.fhir.fhircontext.r4.resources.CompositionLabReportEu;

public class EuFhirContextFactory {

    static public FhirContext createFhirContext() {
        final ParserOptions parserOptions = new ParserOptions();
        parserOptions.setOverrideResourceIdWithBundleEntryFullUrl(false);

        final FhirContext ctx = FhirContext.forR4();
        ctx.setParserOptions(parserOptions);
        ctx.setParserErrorHandler(new LenientErrorHandler());

        ctx.setDefaultTypeForProfile(CompositionLabReportEu.PROFILE, CompositionLabReportEu.class);

        return ctx;
    }
}
