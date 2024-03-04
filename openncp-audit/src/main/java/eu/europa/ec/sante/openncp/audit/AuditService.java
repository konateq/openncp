package eu.europa.ec.sante.openncp.audit;

import eu.europa.ec.sante.openncp.audit.handler.FailedLogsHandlerService;
import eu.europa.ec.sante.openncp.audit.handler.FailedLogsHandlerServiceImpl;
import eu.europa.ec.sante.openncp.audit.handler.MessageHandlerListener;
import eu.europa.ec.sante.openncp.audit.serialization.AuditLogSerializer;
import eu.europa.ec.sante.openncp.audit.serialization.AuditLogSerializerImpl;
import eu.europa.ec.sante.openncp.audit.utils.SerializableMessage;
import eu.europa.ec.sante.openncp.common.configuration.util.OpenNCPConstants;
import eu.europa.ec.sante.openncp.common.configuration.util.ServerMode;
import net.RFC3881.dicom.AuditMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * This service provides access to the system defined properties
 *
 * @author Kostas Karkaletsis
 * @see net.RFC3881 http://www.rfc3881.net/ generated classes using JAXB Library for populating audit trail entries
 */
public class AuditService implements MessageHandlerListener {

    private final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final Logger loggerClinical = LoggerFactory.getLogger("LOGGER_CLINICAL");
    private final FailedLogsHandlerService failedLogsHandlerService;
    private final AuditLogSerializer auditLogSerializer;

    protected AuditService() {

        logger.debug("Initializing Audit Service...");
        auditLogSerializer = new AuditLogSerializerImpl(AuditLogSerializer.Type.AUDIT_MANAGER);
        failedLogsHandlerService = new FailedLogsHandlerServiceImpl(this, AuditLogSerializer.Type.AUDIT_MANAGER);
        failedLogsHandlerService.start();
    }

    /**
     * Provides a method to write an Audit Log.
     *
     * @param eventObject Message to be submitted to the ATNA repository.
     * @param facility    the facility number according to log4j.
     * @param severity    the severity of the message.
     * @return true if auditLog is attempted to be sent.
     */
    public synchronized Boolean write(Object eventObject, String facility, String severity) {

        logger.info("[Audit Service] Writing Audit Message");

        try {
            if (eventObject instanceof EventLog) {
                if (OpenNCPConstants.NCP_SERVER_MODE != ServerMode.PRODUCTION && loggerClinical.isDebugEnabled()) {
                    loggerClinical.debug("Processing EventLog: '{}'", eventObject);
                }

                EventLog eventLog = (EventLog) eventObject;
                AuditMessage auditMessage = AuditTrailUtils.getInstance().createAuditMessage(eventLog);
                AuditTrailUtils.getInstance().sendATNASyslogMessage(auditLogSerializer, auditMessage, facility, severity);
            } else if (eventObject instanceof AuditMessage) {
                AuditMessage auditMessage = (AuditMessage) eventObject;
                AuditTrailUtils.getInstance().sendATNASyslogMessage(auditLogSerializer, auditMessage, facility, severity);
            } else {
                throw new IllegalArgumentException("Unsupported message format: " + eventObject.getClass().getCanonicalName());
            }
            return true;
        } catch (Exception e) {
            logger.warn("Exception: '{}'", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean handleMessage(Serializable message) {

        if (message instanceof SerializableMessage) {
            SerializableMessage sm = (SerializableMessage) message;
            boolean sent = write(sm.getMessage(), sm.getFacility(), sm.getSeverity());
            logger.info("Attempt to write message to OpenATNA server. Result '{}'", sent);
            return sent;
        } else {
            logger.warn("Message null or unknown type! Cannot handle message.");
            return false;
        }
    }

    protected void stopFailedHandler() {
        this.failedLogsHandlerService.stop();
    }
}
