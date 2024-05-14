package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.repository;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.CodeSystemVersion;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DesignationRepository extends JpaRepository<CodeSystemVersion, String> {

    @Query("select d from Designation d " +
            "join d.codeSystemConcept csc " +
            "join csc.valueSetVersions vsv " +
            "where vsv.description = :valueSetVersion")
    List<Designation> findByValueSetVersion(@Param("valueSetVersion") String valueSetVersion);
}
