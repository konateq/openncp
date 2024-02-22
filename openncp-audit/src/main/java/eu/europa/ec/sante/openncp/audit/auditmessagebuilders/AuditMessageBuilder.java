package eu.europa.ec.sante.openncp.audit.auditmessagebuilders;

import eu.europa.ec.sante.openncp.audit.EventLog;
import net.RFC3881.AuditMessage;

public interface AuditMessageBuilder {

    AuditMessage build(EventLog eventLog);
}
