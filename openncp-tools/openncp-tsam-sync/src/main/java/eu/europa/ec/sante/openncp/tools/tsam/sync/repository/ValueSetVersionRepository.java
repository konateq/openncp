package eu.europa.ec.sante.openncp.tools.tsam.sync.repository;

import eu.europa.ec.sante.openncp.tools.tsam.sync.domain.ValueSetVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValueSetVersionRepository extends JpaRepository<ValueSetVersion, Long> {
}
