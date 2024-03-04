package eu.europa.ec.sante.openncp.webmanager.backend.web.rest;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Anomaly;
import eu.europa.ec.sante.openncp.webmanager.backend.service.AnomalyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/anomaly")
public class AnomalyResource {
    private AnomalyService anomalyService;

    public AnomalyResource(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @GetMapping(path="/anomalies")
    public ResponseEntity<List<Anomaly>> getAllAnomalies() {
    // This returns a JSON or XML with the users
        return ResponseEntity.ok(anomalyService.getAllAnomalies());
    }

    @GetMapping(path = "/anomalies/{id}")
    public ResponseEntity<Anomaly> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(anomalyService.getAnomaly(id));
    }
}
