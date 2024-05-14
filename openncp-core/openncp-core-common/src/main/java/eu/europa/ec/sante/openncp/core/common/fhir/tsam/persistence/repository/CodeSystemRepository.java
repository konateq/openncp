package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.repository;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.CodeSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeSystemRepository extends JpaRepository<CodeSystem, String> {
    Optional<CodeSystem> findByOid(String oid);
}
