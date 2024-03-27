package eu.europa.ec.sante.openncp.application.client.connector;

import eu.europa.ec.sante.openncp.application.client.connector.interceptor.AddSamlAssertionInterceptor;
import eu.europa.ec.sante.openncp.application.client.connector.interceptor.TransportTokenInInterceptor;
import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.security.key.KeyStoreManager;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.BindingProvider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

/*
 *  Client Service class providing access to the MyHealth@EU workflows (Patient Summary, ePrescription, OrCD etc.).
 */
public class DefaultClientConnectorService implements ClientConnectorService {

    // Default timeout set to Three minutes.
    private static final Integer TIMEOUT = 180000;
    private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    // Logger
    private final Logger logger = LoggerFactory.getLogger(DefaultClientConnectorService.class);
    // URL of the targeted NCP-B - ClientConnectorService.wsdl
    private final String endpointReference;

    private final ObjectFactory objectFactory = new ObjectFactory();

    private final ClientConnectorServicePortTypeWrapper clientConnectorService;

    public LoggingFeature loggingFeature() {
        final LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        loggingFeature.setVerbose(true);
        return loggingFeature;
    }

    public DefaultClientConnectorService(final String endpointReference) {
        this.endpointReference = Validate.notBlank(endpointReference);

        final eu.europa.ec.sante.openncp.core.client.ClientConnectorService ss = new eu.europa.ec.sante.openncp.core.client.ClientConnectorService();
        clientConnectorService = new ClientConnectorServicePortTypeWrapper(ss.getClientConnectorServicePort());
        clientConnectorService.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointReference);


        final Client client = ClientProxy.getClient(clientConnectorService.getClientConnectorServicePortType());
        client.getBus().getFeatures().add(loggingFeature());
        client.getBus().getFeatures().add(new WSAddressingFeature());

        client.getOutInterceptors().add(new AddSamlAssertionInterceptor());
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        client.getInInterceptors().add(new TransportTokenInInterceptor());

        final HTTPConduit conduit = (HTTPConduit) client.getConduit();

        final TLSClientParameters tlsClientParameters = new TLSClientParameters();
        //FIXME [KJW] this should be configurable, you dont want to disable the CN check in production!!
        tlsClientParameters.setDisableCNCheck(true);
        tlsClientParameters.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        tlsClientParameters.setSSLSocketFactory(getSSLSocketFactory());

        conduit.setTlsClientParameters(tlsClientParameters);
    }



    private SSLSocketFactory getSSLSocketFactory() {
        // Load the Keystore
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            throw new ClientConnectorException("Error creating the keystore instance", e);
        }
        InputStream keystoreStream = null;
        try {
            keystoreStream = new FileInputStream(Constants.SC_KEYSTORE_PATH);
        } catch (FileNotFoundException e) {
            throw new ClientConnectorException("Could not find the keystore", e);
        }
        try {
            keyStore.load(keystoreStream, Constants.SC_KEYSTORE_PASSWORD.toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new ClientConnectorException("Error loading the keystore", e);
        }

        // Add Keystore to KeyManager
        KeyManagerFactory keyManagerFactory = null;
        try {
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new ClientConnectorException("Could not create the key manager factory", e);
        }
        try {
            keyManagerFactory.init(keyStore, Constants.SC_KEYSTORE_PASSWORD.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new ClientConnectorException("Could not initialize the keystore", e);
        }

        final TrustManagerFactory trustManagerFactory;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        } catch (NoSuchAlgorithmException e) {
            throw new ClientConnectorException("Could not create the trust manager factory", e);
        }

        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            throw new ClientConnectorException("Error creating the truststore instance", e);
        }
        InputStream trustStoreStream = null;
        try {
            trustStoreStream = new FileInputStream(Constants.SC_KEYSTORE_PATH);
        } catch (FileNotFoundException e) {
            throw new ClientConnectorException("Could not find the truststore", e);
        }
        try {
            trustStore.load(trustStoreStream, Constants.SC_KEYSTORE_PASSWORD.toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new ClientConnectorException("Error loading the truststore", e);
        }

        try {
            trustManagerFactory.init(trustStore);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        // Create SSLContext with KeyManager and TrustManager
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            throw new ClientConnectorException("Could not get the ssl context instance", e);
        }
        try {
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (KeyManagementException e) {
            throw new ClientConnectorException("Could not initialize the SSL context", e);
        }
        return context.getSocketFactory();
    }


    /**
     * Returns a list of clinical documents related to the patient demographics provided.
     *
     * @param assertions   - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param countryCode  - ISO Country code of the patient country of origin.
     * @param patientId    - Unique Patient Identifier retrieved from NCP-A.
     * @param classCodes   - Class Codes of the documents to retrieve.
     * @param filterParams - Extra parameters for search filtering.
     * @return List of clinical documents and metadata searched by the clinician.
     */
    @Override
    public List<EpsosDocument> queryDocuments(final Map<AssertionEnum, Assertion> assertions, final String countryCode, final PatientId patientId,
                                              final List<GenericDocumentCode> classCodes, final FilterParams filterParams)
            throws ClientConnectorException {

        logger.info("[National Connector] queryDocuments(countryCode:'{}')", countryCode);

        final var queryDocumentRequest = objectFactory.createQueryDocumentRequest();
        classCodes.forEach(genericDocumentCode -> queryDocumentRequest.getClassCode().add(genericDocumentCode));
        queryDocumentRequest.setPatientId(patientId);
        queryDocumentRequest.setCountryCode(countryCode);
        queryDocumentRequest.setFilterParams(filterParams);

        clientConnectorService.setAssertions(assertions);

        List<EpsosDocument> epsosDocuments = clientConnectorService.getClientConnectorServicePortType().queryDocuments(queryDocumentRequest);
        logger.info("epsosDocuments : " + epsosDocuments);
        return epsosDocuments;
    }

    /**
     * Returns demographics of the patient corresponding to the identity traits provided.
     *
     * @param assertions          - Map of assertions required by the transaction (HCP, TRC and NoK optional)
     * @param countryCode         - ISO Country code of the patient country of origin.
     * @param patientDemographics - Identifiers of the requested patient
     * @return List of patients found (only 1 patient is expected in MyHealth@EU)
     */
    public List<PatientDemographics> queryPatient(final Map<AssertionEnum, Assertion> assertions, final String countryCode,
                                                  final PatientDemographics patientDemographics) throws ClientConnectorException {

        logger.info("[National Connector] queryPatient(countryCode:'{}')", countryCode);

        final var queryPatientRequest = objectFactory.createQueryPatientRequest();
        queryPatientRequest.setPatientDemographics(patientDemographics);
        queryPatientRequest.setCountryCode(countryCode);

        clientConnectorService.setAssertions(assertions);

        //set assertions to soap call
        return clientConnectorService.getClientConnectorServicePortType().queryPatient(queryPatientRequest);
    }

    /**
     * Default Webservice test method available mainly for configuration and testing purpose.
     *
     * @param assertions - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param name       - Token sent for testing.
     * @return Hello message concatenated with the token passed as parameter.
     */
    public String sayHello(final Map<AssertionEnum, Assertion> assertions, final String name) throws ClientConnectorException {

        logger.info("[National Connector] sayHello(name:'{}')", name);
        //set assertions to soap call
        return clientConnectorService.getClientConnectorServicePortType().sayHello(name);
    }

    /**
     * Retrieves the clinical document of an identified patient (prescription, patient summary or original clinical document).
     *
     * @param assertions      - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param countryCode     - ISO Country code of the patient country of origin.
     * @param documentId      - Unique identifier of the CDA document.
     * @param homeCommunityId - HL7 Home Community ID of the country of origin.
     * @param classCode       - HL7 ClassCode of the document type to be retrieved.
     * @param targetLanguage  - Expected target language of the CDA translation.
     * @return Clinical Document and metadata returned by the Country of Origin.
     */
    public EpsosDocument retrieveDocument(final Map<AssertionEnum, Assertion> assertions, final String countryCode, final DocumentId documentId,
                                          final String homeCommunityId, final GenericDocumentCode classCode, final String targetLanguage)
            throws ClientConnectorException {

        logger.info("[National Connector] retrieveDocument(countryCode:'{}', homeCommunityId:'{}', targetLanguage:'{}')", countryCode,
                homeCommunityId, targetLanguage);
        final var retrieveDocumentRequest = objectFactory.createRetrieveDocumentRequest();
        retrieveDocumentRequest.setDocumentId(documentId);
        retrieveDocumentRequest.setClassCode(classCode);
        retrieveDocumentRequest.setCountryCode(countryCode);
        retrieveDocumentRequest.setHomeCommunityId(homeCommunityId);
        retrieveDocumentRequest.setTargetLanguage(targetLanguage);

        //set assertions to soap call
        return clientConnectorService.getClientConnectorServicePortType().retrieveDocument(retrieveDocumentRequest);
    }

    /**
     * Submits Clinical Document to the patient country of origin (dispense and discard).
     *
     * @param assertions          - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param countryCode         - ISO Country code of the patient country of origin.
     * @param document            - Clinical document and metadata to be submitted to the patient country of origin.
     * @param patientDemographics - Demographics of the patient linked to the document submission.
     * @return Acknowledge and status of the document submission.
     */
    public SubmitDocumentResponse submitDocument(final Map<AssertionEnum, Assertion> assertions, final String countryCode, final EpsosDocument document,
                                                 final PatientDemographics patientDemographics) throws ClientConnectorException {

        logger.info("[National Connector] submitDocument(countryCode:'{}')", countryCode);
        final var submitDocumentRequest = objectFactory.createSubmitDocumentRequest();
        submitDocumentRequest.setDocument(document);
        submitDocumentRequest.setCountryCode(countryCode);
        submitDocumentRequest.setPatientDemographics(patientDemographics);

        //set assertions to soap call
        final SubmitDocumentResponse submitDocumentResponse = objectFactory.createSubmitDocumentResponse();
        submitDocumentResponse.setResponseStatus(clientConnectorService.getClientConnectorServicePortType().submitDocument(submitDocumentRequest));
        return submitDocumentResponse;
    }


    public static SSLSocketFactory getSslSocketFactory(final KeyStoreManager keyStoreManager, final String keyStorePwd) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        final SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLSv1.2");

        final var keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStoreManager.getKeyStore(), keyStorePwd.toCharArray());

        final var trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStoreManager.getTrustStore());

        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

//
//    /**
//     * Adds the different types of Assertions to the SOAP Header.
//     *
//     * @param clientConnectorServiceStub - Client Service stub.
//     * @param assertions                 - Map of assertions required by the transaction (HCP, TRC and NoK optional).
//     */
//    private void addAssertions(ClientConnectorServiceStub clientConnectorServiceStub, Map<AssertionEnum, Assertion> assertions) throws
//    Exception {
//
//        if (!assertions.containsKey(AssertionEnum.CLINICIAN)) {
//            throw new ClientConnectorConsumerException(OpenNCPErrorCode.ERROR_GENERIC, "HCP Assertion element is required.", null);
//        }
//
//        if (AssertionHelper.isExpired(assertions.get(AssertionEnum.CLINICIAN))) {
//            throw new ClientConnectorConsumerException(OpenNCPErrorCode.ERROR_HPI_GENERIC, "HCP Assertion expired", null);
//        }
//
//        var omFactory = OMAbstractFactory.getSOAP12Factory();
//        SOAPHeaderBlock omSecurityElement = omFactory.createSOAPHeaderBlock(omFactory.createOMElement(new QName(WSSE_NS, "Security", "wsse"),
//        null));
//
//        if (assertions.containsKey(AssertionEnum.NEXT_OF_KIN)) {
//            var assertion = assertions.get(AssertionEnum.NEXT_OF_KIN);
//            if (AssertionHelper.isExpired(assertion)) {
//                throw new ClientConnectorConsumerException(OpenNCPErrorCode.ERROR_SEC_DATA_INTEGRITY_NOT_ENSURED, "Next of Kin Assertion is
//                expired",
//                                                           null);
//            }
//            omSecurityElement.addChild(XMLUtils.toOM(assertion.getDOM()));
//        }
//        if (assertions.containsKey(AssertionEnum.TREATMENT)) {
//            var assertion = assertions.get(AssertionEnum.TREATMENT);
//            if (AssertionHelper.isExpired(assertion)) {
//                throw new ClientConnectorConsumerException(OpenNCPErrorCode.ERROR_SEC_DATA_INTEGRITY_NOT_ENSURED,
//                                                           "Treatment Confirmation Assertion is expired", null);
//            }
//            omSecurityElement.addChild(XMLUtils.toOM(assertion.getDOM()));
//        }
//        var assertion = assertions.get(AssertionEnum.CLINICIAN);
//        omSecurityElement.addChild(XMLUtils.toOM(assertion.getDOM()));
//        clientConnectorServiceStub._getServiceClient().addHeader(omSecurityElement);
//    }
//
//    /**
//     * Configures the SSL Context to support Two Way SLL and using the Service Consumer TLS certificate.
//     *
//     * @return Initialized SSL Context supporting Two Way SSL.
//     */
//    private SSLContext buildSSLContext() throws ClientConnectorConsumerException {
//
//        try {
//            SSLContextBuilder builder = SSLContextBuilder.create();
//            builder.setKeyStoreType("JKS");
//            builder.setKeyManagerFactoryAlgorithm("SunX509");
//
//            builder.loadKeyMaterial(ResourceUtils.getFile(Constants.SC_KEYSTORE_PATH), Constants.SC_KEYSTORE_PASSWORD.toCharArray(),
//                                    Constants.SC_PRIVATEKEY_PASSWORD.toCharArray());
//
//            builder.loadTrustMaterial(ResourceUtils.getFile(Constants.TRUSTSTORE_PATH), Constants.TRUSTSTORE_PASSWORD.toCharArray(),
//                                      TrustAllStrategy.INSTANCE);
//
//            return builder.build();
//        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | CertificateException | IOException |
//                 KeyManagementException e) {
//            throw new ClientConnectorConsumerException(OpenNCPErrorCode.ERROR_SEC_DATA_INTEGRITY_NOT_ENSURED,
//                                                       "SSL Context cannot be initialized: " + e.getMessage(), null, e);
//        }
//    }
//
//    /**
//     * Returns Apache HttpClient supporting Two Way SSL and using TLS protocol 1.2 and 1.3.
//     *
//     * @return Secured HttpClient initialized.
//     */
//    private HttpClient getSSLClient() throws ClientConnectorConsumerException {
//
//        SSLContext sslContext = buildSSLContext();
//        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2", "TLSv1
//        .3" },
//                                                                                               null, NoopHostnameVerifier.INSTANCE);
//        HttpClientBuilder builder = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory);
//        builder.setSSLContext(sslContext);
//        builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
//
//        return builder.build();
//    }
//
//    /**
//     * Initializes the ClientConnectorService client stubs to contact WSDL.
//     *
//     * @return Initialized ClientConnectorServiceStub set to the configured EPR and the SOAP version.
//     */
//    private ClientConnectorServiceStub initializeServiceStub() throws ClientConnectorConsumerException {
//
//        try {
//
//            logger.info("[National Connector] Initializing ClientConnectorService Stub");
//            var clientConnectorStub = new ClientConnectorServiceStub(endpointReference);
//            clientConnectorStub._getServiceClient().getOptions().setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
//            clientConnectorStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(TIMEOUT);
//            clientConnectorStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, TIMEOUT);
//            clientConnectorStub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, TIMEOUT);
//            // Enabling WS Addressing module.
//            clientConnectorStub._getServiceClient().engageModule("addressing");
//            this.registerEvidenceEmitterHandler(clientConnectorStub);
//
//            // Enabling Axis2 - SSL 2 ways communication (not active by default).
//            clientConnectorStub._getServiceClient()
//                               .getServiceContext()
//                               .getConfigurationContext()
//                               .setProperty(HTTPConstants.CACHED_HTTP_CLIENT, getSSLClient());
//            clientConnectorStub._getServiceClient().getServiceContext().getConfigurationContext().setProperty(HTTPConstants
//            .REUSE_HTTP_CLIENT, false);
//
//            return clientConnectorStub;
//        } catch (AxisFault axisFault) {
//            throw createClientConnectorConsumerException(axisFault);
//        }
//    }
//
//    /**
//     * Configures the Non Repudiation process into the Apache Axis2 phase.
//     *
//     * @param clientConnectorServiceStub - Client Service stub.
//     */
//    private void registerEvidenceEmitterHandler(ClientConnectorServiceStub clientConnectorServiceStub) {
//
//        // Adding custom phase for evidence emitter processing.
//        logger.debug("Adding custom phase for Outflow Evidence Emitter processing");
//        var outFlowHandlerDescription = new HandlerDescription("OutFlowEvidenceEmitterHandler");
//        outFlowHandlerDescription.setHandler(new OutFlowEvidenceEmitterHandler());
//        var axisConfiguration = clientConnectorServiceStub._getServiceClient().getServiceContext().getConfigurationContext()
//        .getAxisConfiguration();
//        List<Phase> outFlowPhasesList = axisConfiguration.getOutFlowPhases();
//        var outFlowEvidenceEmitterPhase = new Phase("OutFlowEvidenceEmitterPhase");
//        try {
//            outFlowEvidenceEmitterPhase.addHandler(outFlowHandlerDescription);
//        } catch (PhaseException ex) {
//            logger.error("PhaseException: '{}'", ex.getMessage(), ex);
//        }
//        outFlowPhasesList.add(outFlowEvidenceEmitterPhase);
//        axisConfiguration.setGlobalOutPhase(outFlowPhasesList);
//    }
//
//    /**
//     * Trims the Patient Demographics sent by the client and received by the Client Connector.
//     *
//     * @param patientDemographics Identity Traits to be trimmed and provided by the client
//     */
//    private void trimPatientDemographics(PatientDemographics patientDemographics) {
//
//        // Iterate over the Patient Ids
//        List<PatientId> patientIds = new ArrayList<>();
//        for (PatientId patientId : patientDemographics.getPatientIdArray()) {
//            if (StringUtils.isNotBlank(patientId.getExtension())) {
//                patientId.setExtension(StringUtils.trim(patientId.getExtension()));
//                patientId.setRoot(StringUtils.trim(patientId.getRoot()));
//                patientIds.add(patientId);
//            }
//        }
//        patientDemographics.setPatientIdArray(patientIds.toArray(new PatientId[0]));
//        if (StringUtils.isNotBlank(patientDemographics.getAdministrativeGender())) {
//            patientDemographics.setAdministrativeGender(StringUtils.trim(patientDemographics.getAdministrativeGender()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getFamilyName())) {
//            patientDemographics.setFamilyName(StringUtils.trim(patientDemographics.getFamilyName()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getGivenName())) {
//            patientDemographics.setGivenName(StringUtils.trim(patientDemographics.getGivenName()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getEmail())) {
//            patientDemographics.setEmail(StringUtils.trim(patientDemographics.getEmail()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getTelephone())) {
//            patientDemographics.setTelephone(StringUtils.trim(patientDemographics.getTelephone()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getStreetAddress())) {
//            patientDemographics.setStreetAddress(StringUtils.trim(patientDemographics.getStreetAddress()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getPostalCode())) {
//            patientDemographics.setPostalCode(StringUtils.trim(patientDemographics.getPostalCode()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getCity())) {
//            patientDemographics.setCity(StringUtils.trim(patientDemographics.getCity()));
//        }
//        if (StringUtils.isNotBlank(patientDemographics.getCountry())) {
//            patientDemographics.setCountry(StringUtils.trim(patientDemographics.getCountry()));
//        }
//    }
//
//    private ClientConnectorConsumerException createClientConnectorConsumerException(AxisFault axisFault) {
//
//        String errorCode = axisFault.getFaultCode() != null ? axisFault.getFaultCode().getLocalPart() : null;
//        String message = axisFault.getMessage();
//        String context = axisFault.getDetail() != null ? axisFault.getDetail().getText() : null;
//
//        OpenNCPErrorCode openncpErrorCode = OpenNCPErrorCode.getErrorCode(errorCode);
//
//        return new ClientConnectorConsumerException(openncpErrorCode, message, context, axisFault);
//    }
}
