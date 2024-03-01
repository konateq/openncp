package eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, String> {
}
