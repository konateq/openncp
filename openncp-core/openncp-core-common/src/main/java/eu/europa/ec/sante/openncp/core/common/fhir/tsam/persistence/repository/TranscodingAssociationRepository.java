package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.repository;

import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model.TranscodingAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscodingAssociationRepository extends JpaRepository<TranscodingAssociation, String> {

    List<TranscodingAssociation> findTranscodingAssociationsBySourceConcept(CodeSystemConcept sourceConcept);
}
