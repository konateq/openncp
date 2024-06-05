package eu.europa.ec.sante.openncp.common.configuration;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationManagerFactory {

    private static ConfigurationManager configurationManager;

    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    @Autowired
    public void setConfigurationManager(final ConfigurationManager configurationManager) {
        ConfigurationManagerFactory.configurationManager = Validate.notNull(configurationManager);
    }
}
