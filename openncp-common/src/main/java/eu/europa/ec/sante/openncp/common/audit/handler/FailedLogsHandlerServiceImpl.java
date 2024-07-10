package eu.europa.ec.sante.openncp.common.audit.handler;

import eu.europa.ec.sante.openncp.common.audit.serialization.AuditLogSerializer;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Default time scheduler is fix to 1 hour
 */
public class FailedLogsHandlerServiceImpl implements FailedLogsHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedLogsHandlerServiceImpl.class);
    private static final String KEY_SCHEDULED_TIME_BETWEEN_FAILED_LOGS_HANDLING = "scheduled.time.between.failed.logs.handling.minutes";
    private static final int SCHEDULED_THREAD_POOL_SIZE = 1;
    private static final int WAIT_FOR_TERMINATION = 5000;
    private static final long DEFAULT_SCHEDULER_TIME_MINUTES = 60;
    private ScheduledExecutorService scheduledExecutorService = null;
    private final MessageHandlerListener listener;
    private final AuditLogSerializer.Type type;
    private ConfigurationManager configurationManager;

    /**
     * @param listener
     * @param type
     */
    public FailedLogsHandlerServiceImpl(final MessageHandlerListener listener, final AuditLogSerializer.Type type) {
        this.listener = listener;
        this.type = type;
    }

    /**
     * @param listener
     * @param type
     */
    public FailedLogsHandlerServiceImpl(final MessageHandlerListener listener, final AuditLogSerializer.Type type, final ConfigurationManager configurationManager) {
        this.listener = listener;
        this.type = type;
        this.configurationManager = Validate.notNull(configurationManager, "ConfigurationManager must not be null");
    }


    /**
     *
     */
    public synchronized void start() {

        LOGGER.info("Starting FailedLogsHandlerService...");
        if (scheduledExecutorService == null) {
            final FailedLogsHandler failedLogsHandlerCommand = new FailedLogsHandlerImpl(listener, type);
            scheduledExecutorService = new ScheduledThreadPoolExecutor(SCHEDULED_THREAD_POOL_SIZE);
            scheduledExecutorService.scheduleWithFixedDelay(failedLogsHandlerCommand, getTimeBetween(), getTimeBetween(), TimeUnit.MINUTES);
            LOGGER.info("Started FailedLogsHandlerService. Logs will be scanned every '{}' minutes.", getTimeBetween());
        } else {
            LOGGER.warn("Attempted to start FailedLogsHandlerService even already running.");
        }
    }

    /**
     *
     */
    public synchronized void stop() {

        LOGGER.info("Shutting down FailedLogsHandlerService");
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();

            boolean shutdownOk = false;
            try {
                shutdownOk = scheduledExecutorService.awaitTermination(WAIT_FOR_TERMINATION, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("InterruptedException: '{}'", e.getMessage(), e);
            }
            if (!shutdownOk) {
                LOGGER.error("Attempts to stop all actively executing tasks");
                scheduledExecutorService.shutdownNow();
            }

            scheduledExecutorService = null;
        } else {
            LOGGER.warn("Unable to stop FailedLogsHandlerService. Service is not running.");
        }
    }

    /**
     * Method that return the interval between Failed log retry in minute.
     *
     * @return Interval in minute
     */
    private long getTimeBetween() {
        return getConfigurationManager()
                .map(configurationManager -> configurationManager.getProperty(KEY_SCHEDULED_TIME_BETWEEN_FAILED_LOGS_HANDLING))
                .filter(StringUtils::isNotBlank)
                .map(Long::parseLong)
                .orElse(DEFAULT_SCHEDULER_TIME_MINUTES);

    }

    private Optional<ConfigurationManager> getConfigurationManager() {
        if (configurationManager == null) {
            configurationManager = ConfigurationManagerFactory.getConfigurationManager();
        }

        return Optional.ofNullable(configurationManager);
    }
}
