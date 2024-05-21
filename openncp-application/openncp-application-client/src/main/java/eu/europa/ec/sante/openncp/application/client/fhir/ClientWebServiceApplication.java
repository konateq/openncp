package eu.europa.ec.sante.openncp.application.client.fhir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"eu.europa.ec.sante.openncp"})
public class ClientWebServiceApplication extends SpringBootServletInitializer {

    public static void main(final String... args) {
        SpringApplication.run(ClientWebServiceApplication.class, args);
    }
}