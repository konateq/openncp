package eu.europa.ec.sante.openncp.core.common.tsam.repository;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.domain.TranscodingAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscodingAssociationRepository extends JpaRepository<TranscodingAssociation, String> {

    List<TranscodingAssociation> findTranscodingAssociationsBySourceConcept(CodeSystemConcept sourceConcept);
}
