package eu.europa.ec.sante.openncp.tools.tsam.sync.repository;

import eu.europa.ec.sante.openncp.tools.tsam.sync.domain.CodeSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeSystemRepository extends JpaRepository<CodeSystem, Long> {
}
