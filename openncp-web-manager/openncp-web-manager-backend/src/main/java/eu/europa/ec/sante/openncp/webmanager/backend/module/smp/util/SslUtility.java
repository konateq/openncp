package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.util;

import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.common.security.ImmutableSslProperties;
import eu.europa.ec.sante.openncp.common.security.SslProperties;
import eu.europa.ec.sante.openncp.common.security.SslUtil;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.GatewayProperties;
import org.apache.commons.lang3.Validate;

import javax.net.ssl.SSLContext;

public class SslUtility {

    private static SSLContext sslContext;

    public static SSLContext getSslContext(final ConfigurationManager configurationManager) {
        Validate.notNull(configurationManager, "ConfigurationManager cannot be null");

        if (sslContext == null) {
            final SslProperties sslProperties = ImmutableSslProperties.builder()
                    .certificateAlias(configurationManager.getProperty(GatewayProperties.GTW_TLS_CLIENT_CERT_ALIAS))
                    .certificatePassword(configurationManager.getProperty(GatewayProperties.GTW_TLS_CLIENT_CERT_PWD))
                    .keystorePath(configurationManager.getProperty(GatewayProperties.GTW_TLS_CLIENT_KEYSTORE_PATH))
                    .keystorePassword(configurationManager.getProperty(GatewayProperties.GTW_TLS_CLIENT_KEYSTORE_PWD))
                    .truststorePath(configurationManager.getProperty(GatewayProperties.GTW_TRUSTSTORE_PATH))
                    .truststorePassword(configurationManager.getProperty(GatewayProperties.GTW_TRUSTSTORE_PWD))
                    .build();

            sslContext = SslUtil.createSSLContext(sslProperties);
        }

        return sslContext;
    }
}
