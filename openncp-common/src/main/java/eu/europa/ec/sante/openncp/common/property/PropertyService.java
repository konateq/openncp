package eu.europa.ec.sante.openncp.common.property;

import eu.europa.ec.sante.openncp.common.configuration.PropertyNotFoundException;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    Optional<Property> findByKey(String key);

    default Optional<String> getPropertyValue(final String key) {
        return findByKey(key).map(Property::getValue);
    }

    default Property findByKeyMandatory(final String key) {
        return findByKey(key).orElseThrow(() -> new PropertyNotFoundException(key));
    }

    default String getPropertyValueMandatory(final String key) {
        return findByKeyMandatory(key).getValue();
    }

    List<Property> getAll();

    void createOrUpdate(Property property);
    
}
