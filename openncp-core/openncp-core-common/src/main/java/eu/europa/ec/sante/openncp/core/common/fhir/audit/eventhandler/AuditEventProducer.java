package eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler;

import org.hl7.fhir.r4.model.AuditEvent;

public interface AuditEventProducer {
    boolean accepts(AuditableEvent auditableEvent);

    AuditEvent produce(AuditableEvent auditableEvent);
}
