package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.storage.interceptor.balp.IBalpAuditEventSink;
import eu.europa.ec.sante.openncp.common.audit.AuditService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FhirAuditService implements IBalpAuditEventSink {
    final Logger LOGGER = LoggerFactory.getLogger(FhirAuditService.class);
    private final AuditService auditService;

    public FhirAuditService(final AuditService auditService) {
        this.auditService = Validate.notNull(auditService);
    }

    @Override
    public void recordAuditEvent(final AuditEvent theAuditEvent) {
        LOGGER.info("Sending event to ATNA: {}", theAuditEvent);
        //send to atna
        auditService.write(new Object(), "", "");
    }
}
