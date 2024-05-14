package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.repository;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.ValueSetVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValueSetVersionRepository extends JpaRepository<ValueSetVersion, String> {

    ValueSetVersion findValueSetVersionByDescriptionAndValueSetOid(String valueSetName, String valueSetOid);

    ValueSetVersion findValueSetVersionByStatusAndValueSetOid(String currentStatus, String valueSetOid);
}
