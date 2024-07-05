package eu.europa.ec.sante.openncp.webmanager.backend.service;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Property;
import eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(final PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getProperty(final String name) {
        return propertyRepository.findById(name).orElseThrow();
    }

    public Optional<String> getPropertyValue(final String propertyName) {
        return propertyRepository.findById(propertyName).map(Property::getName);
    }

    public String getPropertyValueMandatory(final String propertyName) {
        return getProperty(propertyName).getValue();
    }
}
