package eu.europa.eu.sante.openncp.security;

import eu.europa.ec.sante.openncp.core.common.security.key.KeyStoreManager;
import eu.europa.eu.sante.openncp.security.key.impl.TianiTestKeyStoreManager;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Jerry Dimitriou <jerouris at netsmart.gr>
 */
public class CertificatePathTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificatePathTest.class);

    public CertificatePathTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void TianiCertPathTest() {
        try {

            KeyStoreManager ksm = new TianiTestKeyStoreManager();

            // instantiate a KeyStore with type JKS
            KeyStore ks = ksm.getKeyStore();

            Certificate cert = ks.getCertificate("server1");
            X509CertSelector target = new X509CertSelector();
            target.setCertificate((X509Certificate) cert);
            LOGGER.info("Certificate: '{}'", cert);
            PKIXBuilderParameters builderParams = new PKIXBuilderParameters(
                    ksm.getTrustStore(), target);
            builderParams.setRevocationEnabled(false);

            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");

            CertPathBuilderResult build = builder.build(builderParams);

            CertPath cp = build.getCertPath();

            List<? extends Certificate> certs = cp.getCertificates();
            LOGGER.info("--------------------------- Certificates as built ----------------------------");
            for (Certificate crt : certs) {
                LOGGER.info("Certificate: '{}'", crt);
            }

            LOGGER.info("--------------------------- END ----------------------------------------------");

            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            PKIXParameters params = new PKIXParameters(ksm.getTrustStore());
            params.setRevocationEnabled(false);

            PKIXCertPathValidatorResult validationResult = (PKIXCertPathValidatorResult) cpv
                    .validate(cp, params);
            LOGGER.info("PKIXCertPathValidatorResult: '{}'", validationResult);

        } catch (CertPathBuilderException | KeyStoreException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertPathValidatorException ex) {
            LOGGER.error(null, ex);
            fail(ex.getMessage());
        }
    }
}
