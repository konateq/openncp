package eu.europa.ec.sante.openncp.audit.auditmessagebuilders;

import eu.europa.ec.sante.openncp.audit.EventLog;
import eu.europa.ec.sante.openncp.audit.AuditConstant;
import net.RFC3881.dicom.AuditMessage;

public class XCARetrieveAuditMessageBuilder extends AbstractAuditMessageBuilder implements AuditMessageBuilder {
    @Override
    public AuditMessage build(EventLog eventLog) {
        AuditMessage message = createAuditTrailForHCPAssurance(eventLog);
        if (message != null) {
            addParticipantObject(message, eventLog.getReqM_ParticipantObjectID(), Short.valueOf("2"), Short.valueOf("3"),
                        "Patient", "ITI-39", "IHE Transactions", "Patient Number",
                        "Cross Gateway Retrieve", eventLog.getQueryByParameter(), eventLog.getHciIdentifier());
            addEventTarget(message, eventLog.getEventTargetParticipantObjectIds(), Short.valueOf("2"), Short.valueOf("4"),
                    "12", "", Short.valueOf("0"), AuditConstant.DICOM, "Cross Gateway Retrieve");
        }
        return message;
    }
}
