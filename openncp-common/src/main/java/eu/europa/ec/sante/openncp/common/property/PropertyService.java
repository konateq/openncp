package eu.europa.ec.sante.openncp.common.property;

import javax.annotation.PropertyKey;
import java.util.Optional;

public interface PropertyService {
    Optional<Property> findByKey(String key);
}
