package eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
