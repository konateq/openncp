package eu.europa.ec.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CodeSystemVersionRepository extends JpaRepository<CodeSystemVersion, String> {

    Optional<CodeSystemVersion> findByLocalNameAndCodeSystem(String localName, CodeSystem codeSystem);

    @Query("select csv.id from CodeSystemVersion csv, CodeSystem cs where cs.id = csv.codeSystem.id and cs.oid = :oid")
    List<Long> findByOid(@Param("oid") String oid);
}
