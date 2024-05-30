package eu.europa.ec.sante.openncp.common.security.key;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;

import eu.europa.ec.sante.openncp.common.Constant;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.common.security.exception.SMgrException;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

@Component("databasePropertiesKeyStoreManager")
public final class DatabasePropertiesKeyStoreManager implements KeyStoreManager {

    private DefaultKeyStoreManager defaultKeyStoreManager;

    public DatabasePropertiesKeyStoreManager(ConfigurationManager configurationManager) {
        Validate.notNull(configurationManager);
        final String keyStoreLocation = configurationManager.getProperty(Constant.NCP_SIG_KEYSTORE_PATH);
        final String keystorePassword = configurationManager.getProperty(Constant.NCP_SIG_KEYSTORE_PASSWORD);

        final String truststoreLocation = configurationManager.getProperty(Constant.TRUSTSTORE_PATH);
        final String truststorePassword = configurationManager.getProperty(Constant.TRUSTSTORE_PASSWORD);

        final String privateKeyAlias = configurationManager.getProperty(Constant.NCP_SIG_PRIVATEKEY_ALIAS);
        final String privateKeyPassword = configurationManager.getProperty(Constant.NCP_SIG_PRIVATEKEY_PASSWORD);

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
