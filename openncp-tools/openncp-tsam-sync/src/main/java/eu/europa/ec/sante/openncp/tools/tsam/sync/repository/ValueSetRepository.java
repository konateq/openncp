package eu.europa.ec.sante.openncp.tools.tsam.sync.repository;

import eu.europa.ec.sante.openncp.tools.tsam.sync.domain.ValueSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValueSetRepository extends JpaRepository<ValueSet, Long> {
}
