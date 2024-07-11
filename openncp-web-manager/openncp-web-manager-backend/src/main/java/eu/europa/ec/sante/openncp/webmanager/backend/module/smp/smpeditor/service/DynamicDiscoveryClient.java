package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.service;

import eu.europa.ec.dynamicdiscovery.DynamicDiscovery;
import eu.europa.ec.dynamicdiscovery.DynamicDiscoveryBuilder;
import eu.europa.ec.dynamicdiscovery.core.locator.dns.impl.DefaultDNSLookup;
import eu.europa.ec.dynamicdiscovery.core.locator.impl.DefaultBDXRLocator;
import eu.europa.ec.dynamicdiscovery.core.reader.impl.DefaultBDXRReader;
import eu.europa.ec.dynamicdiscovery.core.security.impl.DefaultSignatureValidator;
import eu.europa.ec.dynamicdiscovery.exception.TechnicalException;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerImpl;
import eu.europa.ec.sante.openncp.common.configuration.ImmutableProxySettings;
import eu.europa.ec.sante.openncp.common.configuration.ProxySettings;
import eu.europa.ec.sante.openncp.common.configuration.StandardProperties;
import eu.europa.ec.sante.openncp.common.property.PropertyService;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.GatewayProperties;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Component
public class DynamicDiscoveryClient {

    private final Logger logger = LoggerFactory.getLogger(DynamicDiscoveryClient.class);
    private final PropertyService propertyService;
    private DynamicDiscovery instance = null;

    private DynamicDiscoveryClient(final PropertyService propertyService) {
        this.propertyService = Validate.notNull(propertyService, "PropertyService must not be null");
    }

    public synchronized DynamicDiscovery getInstance() throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException, TechnicalException {

        logger.info("[Gateway] DynamicDiscovery getInstance()");
        if (instance == null) {
            logger.debug("Instantiating new instance of DynamicDiscovery");
            final KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TRUSTSTORE_PATH)),
                    propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TRUSTSTORE_PWD).toCharArray());

            trustStore.load(new FileInputStream(propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TRUSTSTORE_PATH)),
                    propertyService.getPropertyValueMandatory(GatewayProperties.GTW_TRUSTSTORE_PWD).toCharArray());

            final Boolean isProxyEnabled = propertyService.getPropertyValue(StandardProperties.HTTP_PROXY_USED).map(Boolean::parseBoolean).orElse(false);

            final ProxySettings proxySettings;
            if (isProxyEnabled) {
                proxySettings = ImmutableProxySettings.builder()
                        .enabled(true)
                        .authenticated(propertyService.getPropertyValue(StandardProperties.HTTP_PROXY_USED).map(Boolean::parseBoolean).orElse(false))
                        .host(propertyService.getPropertyValue(StandardProperties.HTTP_PROXY_HOST))
                        .port(propertyService.getPropertyValue(StandardProperties.HTTP_PROXY_PORT).map(Integer::parseInt))
                        .username(propertyService.getPropertyValue(StandardProperties.HTTP_PROXY_USERNAME))
                        .password(propertyService.getPropertyValue(StandardProperties.HTTP_PROXY_PASSWORD))
                        .build();
            } else {
                proxySettings = ProxySettings.none();
            }

            final DynamicDiscoveryBuilder dynamicDiscoveryBuilder = ConfigurationManagerImpl.initializeDynamicDiscoveryFetcher(proxySettings)
                    .locator(new DefaultBDXRLocator(propertyService.getPropertyValueMandatory(StandardProperties.SMP_SML_DNS_DOMAIN), new DefaultDNSLookup()))
                    .reader(new DefaultBDXRReader(new DefaultSignatureValidator(trustStore)));
            instance = dynamicDiscoveryBuilder.build();
        }
        return instance;
    }
}
