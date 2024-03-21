package eu.europa.ec.sante.openncp.core.common.security.key;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import eu.europa.ec.sante.openncp.core.common.security.exception.SMgrException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultKeyStoreManager implements KeyStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKeyStoreManager.class);

    private final String KEYSTORE_LOCATION;
    private final String TRUSTSTORE_LOCATION;
    private final String KEYSTORE_PASSWORD;
    private final String TRUSTSTORE_PASSWORD;
    private final String PRIVATEKEY_ALIAS;
    private final String PRIVATEKEY_PASSWORD;
    private KeyStore keyStore;
    private KeyStore trustStore;

    public DefaultKeyStoreManager(final String keyStoreLocation, final String keystorePassword, final String truststoreLocation,
                                  final String truststorePassword, final String privateKeyAlias, final String privateKeyPassword) {

        // Constants Initialization
        KEYSTORE_LOCATION = Validate.notBlank(keyStoreLocation);
        TRUSTSTORE_LOCATION = Validate.notBlank(truststoreLocation);
        KEYSTORE_PASSWORD = Validate.notBlank(keystorePassword);
        TRUSTSTORE_PASSWORD = Validate.notBlank(truststorePassword);
        PRIVATEKEY_ALIAS = Validate.notBlank(privateKeyAlias);
        PRIVATEKEY_PASSWORD = Validate.notBlank(privateKeyPassword);

        keyStore = getKeyStore();
        trustStore = getTrustStore();
    }

    @Override
    public KeyPair getPrivateKey(final String alias, final char[] password) throws SMgrException {

        try {

            // Get private key
            final Key key = keyStore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                final Certificate cert = keyStore.getCertificate(alias);

                // Get public key
                final PublicKey publicKey = cert.getPublicKey();

                // Return a key pair
                return new KeyPair(publicKey, (PrivateKey) key);
            }
        } catch (final UnrecoverableKeyException e) {
            LOGGER.error(null, e);
            throw new SMgrException("Key with alias:" + alias + " is unrecoverable", e);
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error(null, e);
            throw new SMgrException("Key with alias:" + alias + " uses an incompatible algorithm", e);
        } catch (final KeyStoreException e) {
            LOGGER.error(null, e);
            throw new SMgrException("Key with alias:" + alias + " not found", e);
        }
        return null;
    }

    @Override
    public KeyStore getKeyStore() {

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            try (final InputStream keystoreStream = new FileInputStream(KEYSTORE_LOCATION)) {
                keyStore.load(keystoreStream, KEYSTORE_PASSWORD.toCharArray());

                return keyStore;
            }
        } catch (final IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException ex) {
            LOGGER.error(null, ex);
        }
        return null;
    }

    @Override
    public Certificate getCertificate(final String alias) throws SMgrException {

        try {
            return keyStore.getCertificate(alias);
        } catch (final KeyStoreException ex) {
            LOGGER.error(null, ex);
            throw new SMgrException("Certificate with alias: " + alias + " not found in keystore", ex);
        }
    }

    @Override
    public KeyStore getTrustStore() {

        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (final InputStream keystoreStream = new FileInputStream(TRUSTSTORE_LOCATION)) {
                trustStore.load(keystoreStream, TRUSTSTORE_PASSWORD.toCharArray());
                return trustStore;
            }
        } catch (final IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException ex) {
            LOGGER.error(null, ex);
        }
        return null;
    }

    @Override
    public KeyPair getDefaultPrivateKey() throws SMgrException {
        return getPrivateKey(PRIVATEKEY_ALIAS, PRIVATEKEY_PASSWORD.toCharArray());
    }

    @Override
    public Certificate getDefaultCertificate() throws SMgrException {
        return getCertificate(PRIVATEKEY_ALIAS);
    }
}
