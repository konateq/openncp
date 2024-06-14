package eu.europa.ec.sante.openncp.core.common.fhir.context;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.ParserOptions;
import ca.uhn.fhir.parser.LenientErrorHandler;
import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.CompositionLabReportMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DiagnosticReportLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.PatientMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ServiceRequestLabMyHealthEu;

public class EuFhirContextFactory {

    static public FhirContext createFhirContext() {
        final ParserOptions parserOptions = new ParserOptions();
        parserOptions.setOverrideResourceIdWithBundleEntryFullUrl(false);

        final FhirContext ctx = FhirContext.forR4();
        ctx.setParserOptions(parserOptions);
        ctx.setParserErrorHandler(new LenientErrorHandler());

        ctx.setDefaultTypeForProfile(PatientMyHealthEu.PROFILE, PatientMyHealthEu.class);
        ctx.setDefaultTypeForProfile(CompositionLabReportMyHealthEu.PROFILE, CompositionLabReportMyHealthEu.class);
        ctx.setDefaultTypeForProfile(ServiceRequestLabMyHealthEu.PROFILE, ServiceRequestLabMyHealthEu.class);
        ctx.setDefaultTypeForProfile(DiagnosticReportLabMyHealthEu.PROFILE, DiagnosticReportLabMyHealthEu.class);

        return ctx;
    }
}
