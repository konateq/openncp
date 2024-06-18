package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.storage.interceptor.balp.IBalpAuditEventSink;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FhirAuditService implements IBalpAuditEventSink {
    final Logger LOGGER = LoggerFactory.getLogger(FhirAuditService.class);

    @Override
    public void recordAuditEvent(final AuditEvent theAuditEvent) {
        //send to atna
        LOGGER.info("Sending event to ATNA: {}", theAuditEvent);
    }
}
