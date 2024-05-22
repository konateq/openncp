package eu.europa.ec.sante.openncp.core.common.property;


import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<PropertyEntity, String> {
}
