package eu.europa.ec.sante.openncp.application.client;

import eu.europa.ec.sante.openncp.common.configuration.util.OpenNCPConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "eu.europa.ec.sante")
public class ClientWsApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWsApplication.class);

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(ClientWsApplication.class, args);

        final String serverMode = System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE);
        LOGGER.info("Server Mode: '{}'", StringUtils.isNotBlank(serverMode) ? serverMode : "N/A");

        //        System.setProperty("javax.net.ssl.keyStore", Constants.SC_KEYSTORE_PATH);
        //        System.setProperty("javax.net.ssl.keyStorePassword", Constants.SC_KEYSTORE_PASSWORD);
        //        System.setProperty("javax.net.ssl.key.alias", Constants.SC_PRIVATEKEY_ALIAS);
        //        System.setProperty("javax.net.ssl.privateKeyPassword", Constants.SC_PRIVATEKEY_PASSWORD);
        //        System.setProperty("javax.net.ssl.trustStore", Constants.TRUSTSTORE_PATH);
        //        System.setProperty("javax.net.ssl.trustStorePassword", Constants.TRUSTSTORE_PASSWORD);
    }
}
