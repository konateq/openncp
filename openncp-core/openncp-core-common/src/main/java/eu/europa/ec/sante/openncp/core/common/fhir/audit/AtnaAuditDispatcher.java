package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AtnaAuditDispatcher implements AuditDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AtnaAuditDispatcher.class);
    private final FhirContext fhirContext;

    public AtnaAuditDispatcher(final FhirContext fhirContext) {
        this.fhirContext = Validate.notNull(fhirContext, "fhirContext must not be null");
    }

    @Override
    public DispatchResult dispatch(final AuditEvent auditEvent) {
        final IParser jsonParser = fhirContext.newJsonParser();
        LOGGER.info("Dispatching event [{}]", jsonParser.encodeResourceToString(auditEvent));

        return DispatchResult.success();
    }
}
