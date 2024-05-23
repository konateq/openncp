package eu.europa.ec.sante.openncp.core.common.ihe.transformation.service;

import eu.europa.ec.sante.openncp.core.common.ihe.transformation.exception.PropertyNotFoundException;
import eu.europa.ec.sante.openncp.core.common.property.PropertyEntity;
import eu.europa.ec.sante.openncp.common.property.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public PropertyEntity getProperty(String name) {
        return propertyRepository.findById(name).orElseThrow(()-> new PropertyNotFoundException(name));
    }
}
