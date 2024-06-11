package eu.europa.ec.sante.openncp.common.configuration;

import org.springframework.stereotype.Component;

/**
 * @deprecated Inject the {@link ConfigurationManager} bean directly in your application instead of calling this factory method.
 */
@Component
@Deprecated
public class ConfigurationManagerFactory {

    private static ConfigurationManager configurationManager;

    /**
     * @deprecated Inject the {@link ConfigurationManager} bean directly in your application instead of calling this factory method.
     */
    @Deprecated
    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public ConfigurationManagerFactory (ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }
}
