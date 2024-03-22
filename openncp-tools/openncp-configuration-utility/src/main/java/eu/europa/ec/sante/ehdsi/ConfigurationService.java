package eu.europa.ec.sante.ehdsi;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    private final Environment environment;

    private final PropertyRepository propertyRepository;

    @Autowired
    public ConfigurationService(final Environment environment, final PropertyRepository propertyRepository) {

        Assert.notNull(environment, "environment must not be null");
        Assert.notNull(propertyRepository, "jdbcTemplate must not be null");
        this.environment = environment;
        this.propertyRepository = propertyRepository;
    }

    /**
     * @throws ConfigurationException
     */
    public void loadProperties() throws ConfigurationException {

        try {
            final String propertiesFile = environment.getRequiredProperty("openncp.property-file.path");
            final FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(
                    PropertiesConfiguration.class).configure(new Parameters().properties().setFileName(propertiesFile));
            final PropertiesConfiguration config = builder.getConfiguration();

            LOGGER.info("FILLING DATABASE WITH PROPERTIES...");

            final List<Property> list = propertyRepository.findAll();
            LOGGER.info("Database contains '{}' entry(ies)", list.size());
            if (list.isEmpty()) {

                final Iterator it = config.getKeys();

                while (it.hasNext()) {

                    final String key = (String) it.next();
                    LOGGER.info("Key: '{}'-'{}'", key, config.getString(key));
                    final Property property = new Property();
                    property.setKey(key);
                    property.setSmp(false);
                    property.setValue(config.getString(key));
                    propertyRepository.save(property);
                }
                LOGGER.info("{} value sets retrieved from the database", propertyRepository.findAll().size());
            } else {
                LOGGER.info("No need to initialize...");
            }
        } catch (final IllegalStateException e) {
            LOGGER.error("IllegalStateException: Cannot retrieve OpenNCP configuration file and property: '{}'", e.getMessage(), e);
        }
    }
}
