package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.util;

import eu.europa.ec.sante.openncp.common.property.PropertyService;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.GatewayProperties;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.service.SimpleErrorHandler;
import org.apache.commons.lang3.Validate;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Component
public class SslUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SslUtil.class);
    private final PropertyService propertyService;

    public SslUtil(final PropertyService propertyService) {
        this.propertyService = Validate.notNull(propertyService, "propertyService must not be null");
    }

    public SSLContext createSSLContext() {


        final PrivateKeyStrategy privatek = (map, socket) -> propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TLS_CLIENT_CERT_ALIAS);

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = null;
        try {
            //must be the same as SC_KEYSTORE_PASSWORD
            sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(new File(propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TLS_CLIENT_KEYSTORE_PATH)),
                            propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TLS_CLIENT_KEYSTORE_PWD).toCharArray(),
                            propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TLS_CLIENT_CERT_PWD).toCharArray(),
                            privatek)
                    .loadTrustMaterial(new File(propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TRUSTSTORE_PATH)),
                            propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TRUSTSTORE_PWD).toCharArray(),
                            new TrustSelfSignedStrategy())
                    .build();
        } catch (final NoSuchAlgorithmException ex) {
            LOGGER.error("NoSuchAlgorithmException: '{}", SimpleErrorHandler.printExceptionStackTrace(ex));
        } catch (final KeyStoreException ex) {
            LOGGER.error("KeyStoreException: '{}", SimpleErrorHandler.printExceptionStackTrace(ex));
        } catch (final CertificateException ex) {
            LOGGER.error("CertificateException: '{}", SimpleErrorHandler.printExceptionStackTrace(ex));
        } catch (final IOException ex) {
            LOGGER.error("IOException: '{}", SimpleErrorHandler.printExceptionStackTrace(ex));
        } catch (final KeyManagementException ex) {
            LOGGER.error("KeyManagementException: '{}", SimpleErrorHandler.printExceptionStackTrace(ex));
        } catch (final UnrecoverableKeyException ex) {
            LOGGER.error("UnrecoverableKeyException: '{}", SimpleErrorHandler.printExceptionStackTrace(ex));
        }
        return sslcontext;
    }
}
