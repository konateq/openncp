package eu.europa.ec.sante.openncp.core.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"eu.europa.ec.sante.openncp.core"})
public class TestApplication {

    public static void main(final String... args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
