package eu.europa.ec.sante.openncp.common.audit;

import eu.europa.ec.sante.openncp.common.audit.transformer.AuditMessagePassThroughTransformer;
import eu.europa.ec.sante.openncp.common.audit.transformer.AuditMessageTransformer;
import eu.europa.ec.sante.openncp.common.audit.transformer.EventLogToAuditMessageTransformer;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @deprecated Use spring bean wiring instead of this factory method.
 */
@Component
@Deprecated
public class AuditServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceFactory.class);
    private static AuditService instance;

    private AuditServiceFactory() {
    }

    /**
     * Returns a thread safe instance of the AuditService class required for handling event log.
     *
     * @return thread safe AuditService instance initialized.
     * @deprecated Use spring bean wiring instead of this factory method.
     */
    @Deprecated
    public static synchronized AuditService getInstance() {
        return instance;
    }

    /**
     * This method will stop the FailedLogsHandler process instantiated by each OpenNCP component.
     * When the AuditService has been loaded into a webapp, this method will ensure that all the threads will be properly
     * stopped before undeploying the application.
     * Expected result will be a clean status of all the threads started for handling event log serialized.
     */
    public static synchronized void stopAuditService() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Stopping Failed Log Handler Service...");
        }
        getInstance().stopFailedHandler();
    }

    @Autowired
    public void setAuditService(final AuditService auditService) {
        LOGGER.info("Wiring a auditservice.");
        AuditServiceFactory.instance = Validate.notNull(auditService);
    }
}
