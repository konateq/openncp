package eu.europa.ec.sante.openncp.core.common.security.key;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;

import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import eu.europa.ec.sante.openncp.core.common.security.exception.SMgrException;

public final class DatabasePropertiesKeyStoreManager implements KeyStoreManager {

    private final DefaultKeyStoreManager defaultKeyStoreManager;

    public DatabasePropertiesKeyStoreManager() {

        // Constants Initialization
        final ConfigurationManager cm = ConfigurationManagerFactory.getConfigurationManager();
        final String keyStoreLocation = cm.getProperty("NCP_SIG_KEYSTORE_PATH");
        final String keystorePassword = cm.getProperty("NCP_SIG_KEYSTORE_PASSWORD");

        final String truststoreLocation = cm.getProperty("TRUSTSTORE_PATH");
        final String truststorePassword = cm.getProperty("TRUSTSTORE_PASSWORD");

        final String privateKeyAlias = cm.getProperty("NCP_SIG_PRIVATEKEY_ALIAS");
        final String privateKeyPassword = cm.getProperty("NCP_SIG_PRIVATEKEY_PASSWORD");

        defaultKeyStoreManager = new DefaultKeyStoreManager(keyStoreLocation, keystorePassword, truststoreLocation, truststorePassword,
                                                            privateKeyAlias, privateKeyPassword);
    }

    @Override
    public KeyPair getPrivateKey(final String alias, final char[] password) throws SMgrException {
        return defaultKeyStoreManager.getPrivateKey(alias, password);
    }

    @Override
    public KeyStore getKeyStore() {
        return defaultKeyStoreManager.getKeyStore();
    }

    @Override
    public Certificate getCertificate(final String alias) throws SMgrException {
        return defaultKeyStoreManager.getCertificate(alias);
    }

    @Override
    public KeyStore getTrustStore() {
        return defaultKeyStoreManager.getTrustStore();
    }

    @Override
    public KeyPair getDefaultPrivateKey() throws SMgrException {
        return defaultKeyStoreManager.getDefaultPrivateKey();
    }

    @Override
    public Certificate getDefaultCertificate() throws SMgrException {
        return defaultKeyStoreManager.getDefaultCertificate();
    }
}