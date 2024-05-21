package eu.europa.ec.sante.openncp.common.audit.auditmessagebuilders;

import eu.europa.ec.sante.openncp.common.audit.AuditConstant;
import eu.europa.ec.sante.openncp.common.audit.EventActionCode;
import eu.europa.ec.sante.openncp.common.audit.EventLog;
import net.RFC3881.dicom.AuditMessage;
import net.RFC3881.dicom.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HCPAuthenticationAuditMessageBuilder extends AbstractAuditMessageBuilder implements AuditMessageBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HCPAuthenticationAuditMessageBuilder.class);

    @Override
    public AuditMessage build(EventLog eventLog) {
        AuditMessage message = null;
        try {
            ObjectFactory of = new ObjectFactory();
            message = of.createAuditMessage();
            // Audit Source
            addAuditSource(message, eventLog.getAS_AuditSourceId());
            // Event Identification
            addEventIdentification(message, eventLog.getEventType(), eventLog.getEI_TransactionName(), EventActionCode.EXECUTE.getCode(),
                    eventLog.getEI_EventDateTime(), eventLog.getEI_EventOutcomeIndicator(), eventLog.getNcpSide());
            // Point Of Care
            addPointOfCare(message, eventLog.getPC_UserID(), eventLog.getPC_RoleID(), true,
                    "1.3.6.1.4.1.12559.11.10.1.3.2.2.2");
            // Human Requester
            addHumanRequestor(message, eventLog.getHR_UserID(), eventLog.getHR_AlternativeUserID(), eventLog.getHR_RoleID(),
                    true);
            addService(message, eventLog.getSC_UserID(), true, AuditConstant.SERVICE_CONSUMER, AuditConstant.CODE_SYSTEM_EHDSI,
                    "Service Consumer", eventLog.getSourceip());
            addService(message, eventLog.getSP_UserID(), false, AuditConstant.SERVICE_PROVIDER, AuditConstant.CODE_SYSTEM_EHDSI,
                    "Service Provider", eventLog.getTargetip());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        if (message != null) {
            // Event Target
            addEventTarget(message, eventLog.getEventTargetParticipantObjectIds(), Short.valueOf("2"), null,
                    "IdA", AuditConstant.CODE_SYSTEM_EHDSI_SECURITY, "HCP Identity Assertion");
        }
        return message;
    }
}