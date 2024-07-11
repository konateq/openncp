package eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = "webmanagerTransactionManager", readOnly = true)
public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {
}
