package eu.europa.ec.sante.openncp.webmanager.backend.web.rest;

import eu.europa.ec.sante.openncp.common.property.Property;
import eu.europa.ec.sante.openncp.common.property.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class PropertyResource {

    private final PropertyService propertyService;

    public PropertyResource(final PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping(path = "/properties")
    public ResponseEntity<List<Property>> listProperties() {
        return ResponseEntity.ok(propertyService.getAll());
    }
}
