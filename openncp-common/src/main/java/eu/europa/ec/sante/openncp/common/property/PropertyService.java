package eu.europa.ec.sante.openncp.common.property;

import eu.europa.ec.sante.openncp.common.configuration.PropertyNotFoundException;

import javax.annotation.PropertyKey;
import java.util.Optional;

public interface PropertyService {
    Optional<Property> findByKey(String key);

    default Property findByKeyMandatory(String key) {
        return findByKey(key).orElseThrow(()-> new PropertyNotFoundException(key));
    }

    void createOrUpdate(Property property);
}
