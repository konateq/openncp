package eu.europa.ec.sante.openncp.application.server.fhir;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@OpenAPIDefinition(
        info = @Info(
                title = "OpenNCP Server WS",
                description = "API for searching Patient resources and Laboratory Result Reports",
                contact = @Contact(name = "eHDSI Support", email = "SANTE-EHEALTH-DSI-SUPPORT@ec.europa.eu")
        )
)
@SpringBootApplication(scanBasePackages = {"eu.europa.ec.sante.myhealtheu"})
public class ServerWebServiceApplication extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ServerWebServiceApplication.class);

    public static void main(final String... args) {
        SpringApplication.run(ServerWebServiceApplication.class, args);
    }
}
