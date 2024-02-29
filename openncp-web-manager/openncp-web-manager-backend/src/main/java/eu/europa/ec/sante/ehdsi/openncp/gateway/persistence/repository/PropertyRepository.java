package src.main.java.eu.europa.ec.sante.ehdsi.openncp.gateway.persistence.repository;

import eu.europa.ec.sante.ehdsi.openncp.gateway.persistence.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, String> {
}
