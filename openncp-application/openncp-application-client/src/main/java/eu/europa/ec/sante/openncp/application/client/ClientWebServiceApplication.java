package eu.europa.ec.sante.openncp.application.client;

import eu.europa.ec.sante.openncp.common.configuration.util.OpenNCPConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"eu.europa.ec.sante.openncp"})
public class ClientWebServiceApplication extends SpringBootServletInitializer {

    private static final Logger loggerApplication = LoggerFactory.getLogger(ClientWebServiceApplication.class);


    public static void main(final String... args) {
        SpringApplication.run(eu.europa.ec.sante.openncp.application.client.ClientWebServiceApplication.class, args);

        final String serverMode = System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE);
        loggerApplication.info("Server Mode: '{}'", StringUtils.isNotBlank(serverMode) ? serverMode : "N/A");
    }
}
