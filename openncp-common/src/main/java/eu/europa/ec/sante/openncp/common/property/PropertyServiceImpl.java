package eu.europa.ec.sante.openncp.common.property;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional("propertiesTransactionManager" )
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = Validate.notNull(propertyRepository);
    }

    @Override
    public Optional<Property> findByKey(String key) {
        return propertyRepository.findById(key).map(PropertyEntity::asProperty);

    }

    @Override
    public void createOrUpdate(Property property) {
        propertyRepository.save(new PropertyEntity(property));
    }
}
