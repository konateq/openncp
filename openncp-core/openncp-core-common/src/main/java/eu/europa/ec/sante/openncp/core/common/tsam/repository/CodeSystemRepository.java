package eu.europa.ec.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CodeSystemRepository extends JpaRepository<CodeSystem, String> {
    Optional<CodeSystem> findByOid(String oid);
}
