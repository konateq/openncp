package eu.europa.ec.sante.openncp.common.configuration;

import eu.europa.ec.sante.openncp.common.audit.handler.FailedLogsHandlerServiceImpl;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @deprecated Inject the {@link ConfigurationManager} bean directly in your application instead of calling this factory method.
 */
@Component
@Deprecated
public class ConfigurationManagerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagerFactory.class);
    private static ConfigurationManager configurationManager;

    /**
     * @deprecated Inject the {@link ConfigurationManager} bean directly in your application instead of calling this factory method.
     */
    @Deprecated
    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    @Autowired
    public void setConfigurationManager(final ConfigurationManager configurationManager) {
        LOGGER.info("Wiring a configuration manager.");
        ConfigurationManagerFactory.configurationManager = Validate.notNull(configurationManager);
    }
}
