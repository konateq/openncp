package eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler;

import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FallbackAuditEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FallbackAuditEventProducer.class);

    public List<AuditEvent> produce(final AuditableEvent auditableEvent) {
        LOGGER.error("TODO: produce fallback audit events when the auditable event was not caught by other AuditEventProducers.");
        return Collections.emptyList();
    }
}
