package eu.europa.ec.sante.openncp.configuration.utility;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, String> {
}
