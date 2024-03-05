package eu.europa.ec.sante.openncp.common.audit.auditmessagebuilders;

import eu.europa.ec.sante.openncp.common.audit.EventLog;
import net.RFC3881.dicom.AuditMessage;

public interface AuditMessageBuilder {

    AuditMessage build(EventLog eventLog);
}
