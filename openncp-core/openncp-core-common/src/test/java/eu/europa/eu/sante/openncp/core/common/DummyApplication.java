package eu.europa.eu.sante.openncp.core.common;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "eu.europa.ec.sante.openncp.core.common.tsam")
@EntityScan("eu.europa.ec.sante.openncp.core.common.tsam.domain")
public class DummyApplication {
}
