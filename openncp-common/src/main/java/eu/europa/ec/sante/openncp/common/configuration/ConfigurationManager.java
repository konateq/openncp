package eu.europa.ec.sante.openncp.common.configuration;

import eu.europa.ec.dynamicdiscovery.DynamicDiscoveryBuilder;

import java.util.Map;

/**
 * OpenNCP ConfigurationManager interface defining all the services responsible for the application configuration and
 * properties used by all the components.
 */
public interface ConfigurationManager {


    DynamicDiscoveryBuilder initializeDynamicDiscoveryFetcher();

    /**
     * @param key
     * @return
     */
    String getProperty(String key);

    /**
     * @param key
     * @param checkMap
     * @return
     */
    String getProperty(String key, boolean checkMap);

    /**
     * @return
     */
    Map<String, String> getProperties();

    /**
     * @param key
     * @return
     */
    boolean getBooleanProperty(String key);

    /**
     * @param key
     * @return
     */
    int getIntegerProperty(String key);

    /**
     * @param key
     * @param value
     */
    void setProperty(String key, String value);
}
