package eu.europa.ec.sante.openncp.webmanager.backend.service;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Anomaly;

import java.util.List;

public interface AnomalyService {


    List<Anomaly> getAllAnomalies();

    Anomaly getAnomaly(Long id);
}
