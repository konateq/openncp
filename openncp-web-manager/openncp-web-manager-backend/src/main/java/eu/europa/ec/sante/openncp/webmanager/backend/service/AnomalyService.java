package eu.europa.ec.sante.openncp.webmanager.backend.service;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Anomaly;
import eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository.AnomalyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnomalyService {

    private final AnomalyRepository anomalyRepository;

    public AnomalyService(AnomalyRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
    }

    public List<Anomaly> getAllAnomalies() {
        return anomalyRepository.findAll();
    }

    public Anomaly getAnomaly(Long id) {
        return anomalyRepository.findById(id).orElseThrow();
    }
}
