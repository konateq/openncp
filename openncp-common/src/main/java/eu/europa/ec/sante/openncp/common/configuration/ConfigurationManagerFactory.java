package eu.europa.ec.sante.openncp.common.configuration;

import org.springframework.stereotype.Component;

@Component
public class ConfigurationManagerFactory {

    private static ConfigurationManager configurationManager;

    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public ConfigurationManagerFactory (ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }
}
