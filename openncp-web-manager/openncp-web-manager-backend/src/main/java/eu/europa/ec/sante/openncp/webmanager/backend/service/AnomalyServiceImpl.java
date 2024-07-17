package eu.europa.ec.sante.openncp.webmanager.backend.service;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Anomaly;
import eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository.AnomalyRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional("webmanagerTransactionManager")
public class AnomalyServiceImpl implements AnomalyService {
    private final AnomalyRepository anomalyRepository;

    public AnomalyServiceImpl(final AnomalyRepository anomalyRepository) {
        this.anomalyRepository = Validate.notNull(anomalyRepository);
    }

    @Override
    public List<Anomaly> getAllAnomalies() {
        return anomalyRepository.findAll();
    }

    @Override
    public Anomaly getAnomaly(Long id) {
        return anomalyRepository.findById(id).orElseThrow();
    }
}
