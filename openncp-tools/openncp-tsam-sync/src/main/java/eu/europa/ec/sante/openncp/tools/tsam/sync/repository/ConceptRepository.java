package eu.europa.ec.sante.openncp.tools.tsam.sync.repository;

import eu.europa.ec.sante.openncp.tools.tsam.sync.domain.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository extends JpaRepository<Concept, Long> {
}
