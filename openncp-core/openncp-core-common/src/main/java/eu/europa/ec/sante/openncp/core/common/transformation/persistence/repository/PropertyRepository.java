package eu.europa.ec.sante.openncp.core.common.transformation.persistence.repository;


import eu.europa.ec.sante.openncp.core.common.transformation.persistence.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, String> {
}