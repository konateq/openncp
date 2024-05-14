package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.repository;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.ValueSet;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.ValueSetVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ValueSetRepository extends JpaRepository<ValueSet, String> {

    @Query(value = "SELECT vsv FROM ValueSet vs inner join vs.versions vsv inner join vsv.concepts c WHERE vs.oid = :valueSetOid AND  c.id = :conceptId")
    List<ValueSetVersion> findByOidAndConcepts(@Param("valueSetOid") String valueSetOid, @Param("conceptId") long conceptId);
}
