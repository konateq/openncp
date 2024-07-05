package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.service;

import eu.europa.ec.dynamicdiscovery.DynamicDiscovery;
import eu.europa.ec.dynamicdiscovery.exception.TechnicalException;
import eu.europa.ec.dynamicdiscovery.model.DocumentIdentifier;
import eu.europa.ec.dynamicdiscovery.model.ParticipantIdentifier;
import eu.europa.ec.dynamicdiscovery.model.ServiceMetadata;
import eu.europa.ec.sante.openncp.common.configuration.*;
import eu.europa.ec.sante.openncp.common.util.XMLUtil;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.service.DynamicDiscoveryClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.xerces.dom.ElementNSImpl;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.EndpointType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.net.ssl.SSLContext;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@Service
public class DynamicDiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDiscoveryService.class);
    //  Static constants for SMP identifiers
    private static final String PARTICIPANT_IDENTIFIER_SCHEME = "ehealth-participantid-qns";
    private static final String PARTICIPANT_IDENTIFIER_VALUE = "urn:ehealth:%2s:ncp-idp";
    private static final String DOCUMENT_IDENTIFIER_SCHEME = "ehealth-resid-qns";
    private static final String URN_EHDSI_ISM = "http://ec.europa.eu/sante/ehncp/ism";
    private static final String APPLICATION_BASE_DIR = System.getenv(StandardProperties.OPENNCP_BASEDIR) + "forms" + System.getProperty("file.separator");

    private final DynamicDiscoveryClient dynamicDiscoveryClient;

    public DynamicDiscoveryService(final DynamicDiscoveryClient dynamicDiscoveryClient) {
        this.dynamicDiscoveryClient = dynamicDiscoveryClient;
    }

    /**
     * Creating a HttpClient object initialized with the SSLContext using TLSv1.2 only.
     *
     * @param sslContext - Secured Context of the OpenNCP Gateway.
     * @return CloseableHttpClient initialized
     */
    public static CloseableHttpClient buildHttpClient(final SSLContext sslContext) {

        // Decision for hostname verification: SSLConnectionSocketFactory.getDefaultHostnameVerifier().
        final SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1.2"},
                null,
                new NoopHostnameVerifier());

        final ConfigurationManager configurationManager = ConfigurationManagerFactory.getConfigurationManager();
        final boolean proxyEnabled = configurationManager.getBooleanProperty(StandardProperties.HTTP_PROXY_USED);
        final CloseableHttpClient httpclient;

        if (proxyEnabled) {

            final boolean proxyAuthenticated = configurationManager.getBooleanProperty(StandardProperties.HTTP_PROXY_AUTHENTICATED);
            final String proxyHost = configurationManager.getProperty(StandardProperties.HTTP_PROXY_HOST);
            final int proxyPort = configurationManager.getIntegerProperty(StandardProperties.HTTP_PROXY_PORT);

            if (proxyAuthenticated) {
                final String proxyUsername = configurationManager.getProperty(StandardProperties.HTTP_PROXY_USERNAME);
                final String proxyPassword = configurationManager.getProperty(StandardProperties.HTTP_PROXY_PASSWORD);
                final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(proxyUsername, proxyPassword);
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), credentials);

                httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLSocketFactory(sslConnectionSocketFactory).setProxy(new HttpHost(proxyHost, proxyPort))
                        .build();
            } else {
                httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory)
                        .setProxy(new HttpHost(proxyHost, proxyPort))
                        .build();
            }
        } else {
            httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .build();
        }
        return httpclient;
    }

    /**
     * @param countryCode - ISO Country Code of the concerned country (Format: 2 letters in lowercase).
     */
    public void fetchInternationalSearchMask(final String countryCode) {

        try {
            final String participantIdentifierValue = String.format(PARTICIPANT_IDENTIFIER_VALUE, countryCode);
            LOGGER.info("[Gateway] Querying ISM for participant identifier {}", participantIdentifierValue);
            final ParticipantIdentifier participantIdentifier = new ParticipantIdentifier(participantIdentifierValue, PARTICIPANT_IDENTIFIER_SCHEME);
            final DocumentIdentifier documentIdentifier = new DocumentIdentifier(RegisteredService.EHEALTH_107.getUrn(), DOCUMENT_IDENTIFIER_SCHEME);
            final DynamicDiscovery smpClient = dynamicDiscoveryClient.getInstance();
            final ServiceMetadata serviceMetadata = smpClient.getServiceMetadata(participantIdentifier, documentIdentifier);

            final List<ProcessType> processTypes = serviceMetadata.getOriginalServiceMetadata().getServiceMetadata()
                    .getServiceInformation().getProcessList().getProcess();

            if (!processTypes.isEmpty()) {

                final List<EndpointType> endpointTypes = processTypes.get(0).getServiceEndpointList().getEndpoint();
                if (!endpointTypes.isEmpty()) {

                    final List<ExtensionType> extensionTypes = endpointTypes.get(0).getExtension();
                    if (!extensionTypes.isEmpty()) {

                        final Document document = ((ElementNSImpl) extensionTypes.get(0).getAny()).getOwnerDocument();
                        final DOMSource source = new DOMSource(document.getElementsByTagNameNS(URN_EHDSI_ISM, "searchFields").item(0));
                        final String outPath = APPLICATION_BASE_DIR + "InternationalSearch_" + StringUtils.upperCase(countryCode) + ".xml";
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("International Search Mask Path: '{}", outPath);
                        }
                        final StreamResult result = new StreamResult(new File(outPath));
                        XMLUtil.transformDocument(source, result);
                    }
                }
            }
            //  Audit variables
            final URI smpURI = smpClient.getService().getMetadataLocator().lookup(participantIdentifier);
            final URI serviceMetadataUri = smpClient.getService().getMetadataProvider().resolveServiceMetadata(smpURI, participantIdentifier, documentIdentifier);
            final byte[] encodedObjectID = Base64.encodeBase64(serviceMetadataUri.toASCIIString().getBytes());
            AuditManager.handleDynamicDiscoveryQuery(smpURI.toASCIIString(), new String(encodedObjectID), null, null);

        } catch (final IOException | CertificateException | KeyStoreException | TechnicalException |
                       TransformerException |
                       NoSuchAlgorithmException e) {
            //TODO: [Specification] Analyze if an audit message is required in case of error.
            throw new ConfigurationManagerException(String.format("An internal error occurred while retrieving the International Search Mask from [%s]: [%s]", countryCode, e.getMessage()), e);
        }
    }
}
