package eu.europa.ec.sante.openncp.transformation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import eu.europa.ec.sante.openncp.transformation.persistence.repository.PropertyRepository;
import eu.europa.ec.sante.openncp.transformation.persistence.model.Property;
import eu.europa.ec.sante.openncp.transformation.exception.PropertyNotFoundException;

@Service
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property getProperty(String name) {
        return propertyRepository.findById(name).orElseThrow(()-> new PropertyNotFoundException(name));
    }
}
