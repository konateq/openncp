package eu.europa.ec.sante.openncp.sts.client;

import epsos.ccd.netsmart.securitymanager.key.KeyStoreManager;
import epsos.ccd.netsmart.securitymanager.key.impl.DefaultKeyStoreManager;
import eu.ehdsi.openncp.sts.client.cxf.SecurityTokenService;
import eu.epsos.validation.datamodel.common.NcpSide;
import eu.europa.ec.sante.ehdsi.gazelle.validation.OpenNCPValidation;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.ConfigurationManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.*;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tr.com.srdc.epsos.util.Constants;

import javax.net.ssl.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.UUID;

public class SecurityTokenServiceRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTokenServiceRequest.class);

    private static final QName MESSAGING_TO = new QName("http://www.w3.org/2005/08/addressing", "To");
    private static final String SAML20_TOKEN_URN = "urn:oasis:names:tc:SAML:2.0:assertion"; // What can be only requested from the STS
    private static final String ACTION_URI = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue"; // Only Issuance is supported
    private static final String TRC_NS = "https://ehdsi.eu/assertion/trc"; //TRC Parameters Namespace
    private static final String WS_TRUST_NS = "http://docs.oasis-open.org/ws-sx/ws-trust/200512"; // WS-Trust Namespace
    private static final String ADDRESSING_NS = "http://www.w3.org/2005/08/addressing"; // WSA Namespace
    private static final String WS_SEC_UTIL_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    private static final String WS_SEC_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String DEFAULT_STS_URL;
    private static final String CHECK_FOR_HOSTNAME;
    private static SecurityTokenService securityTokenService;

    static {

        try {
            InitializationService.initialize();
        } catch (InitializationException e) {
            LOGGER.error("OpenSAML module cannot be initialized: '{}'", e.getMessage(), e);
        }
        if (ConfigurationManagerFactory.getConfigurationManager().getProperty("secman.sts.url").length() == 0) {
            ConfigurationManagerFactory.getConfigurationManager().setProperty("secman.sts.url", "https://localhost:8443/TRC-STS/SecurityTokenService");
        }
        DEFAULT_STS_URL = ConfigurationManagerFactory.getConfigurationManager().getProperty("secman.sts.url");

        if (ConfigurationManagerFactory.getConfigurationManager().getProperty("secman.sts.checkHostname").length() == 0) {
            ConfigurationManagerFactory.getConfigurationManager().setProperty("secman.sts.checkHostname", "false");
        }
        CHECK_FOR_HOSTNAME = ConfigurationManagerFactory.getConfigurationManager().getProperty("secman.sts.checkHostname");
        try {
            securityTokenService = new SecurityTokenService(new URL(DEFAULT_STS_URL), new WSAddressingFeature());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private final Assertion idAssert;
    private final String purposeOfUse;
    private final String prescriptionId;
    private final String patientId;
    private final String dispensationPinCode;
    private final SOAPMessage rstMsg;
    private final String messageId;
    private final DocumentBuilder builder;
    private final URL location;

    private SecurityTokenServiceRequest(Builder builder) throws Exception {

        this.idAssert = builder.idAssert;
        this.patientId = builder.patientId;
        this.dispensationPinCode = builder.dispensationPinCode;
        this.prescriptionId = builder.prescriptionId;
        this.purposeOfUse = builder.purposeOfUse;
        this.location = builder.location;
        this.messageId = createMessageId();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        this.builder = documentBuilderFactory.newDocumentBuilder();

        try {
            rstMsg = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
            createRSTHeader(rstMsg.getSOAPHeader());
            createRSTBody(rstMsg.getSOAPBody());
        } catch (SOAPException ex) {
            throw new Exception("Unable to create RST Message");
        }
    }

    private String createMessageId() {
        return Constants.UUID_PREFIX + UUID.randomUUID();
    }

    private Element convertAssertionToElement(Assertion assertion) {


        try {
            Document doc = builder.newDocument();
            Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion);
            if (marshaller == null) {
                LOGGER.error("SAML Marshalling is NULL");
                return null;
            }
            marshaller.marshall(assertion, doc);
            return doc.getDocumentElement();
        } catch (MarshallingException e) {
            LOGGER.error("MarshallingException: '{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param element
     * @return
     */
    private Assertion convertElementToAssertion(Element element) {

        // Unmarshalling using the document root element, an EntitiesDescriptor in this case
        try {

            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element);
            if (unmarshaller == null) {
                LOGGER.error("SAML Unmarshalling is NULL");
                return null;
            }
            return (Assertion) unmarshaller.unmarshall(element);

        } catch (UnmarshallingException e) {
            LOGGER.error("UnmarshallingException: '{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param header
     */
    private void createRSTHeader(SOAPHeader header) {

        try {

            SOAPHeaderElement messageIdElem = header.addHeaderElement(new QName(ADDRESSING_NS, "MessageID", "wsa"));
            messageIdElem.setTextContent(messageId);

            SOAPHeaderElement securityHeaderElem = header.addHeaderElement(new QName(WS_SEC_NS, "Security", "wsse"));
            securityHeaderElem.setMustUnderstand(true);

            Element idAssertElem = convertAssertionToElement(idAssert);
            securityHeaderElem.appendChild(header.getOwnerDocument().importNode(idAssertElem, true));

        } catch (SOAPException ex) {
            LOGGER.error(null, ex);
        }
    }

    /**
     * @param body
     */
    private void createRSTBody(SOAPBody body) {

        try {
            SOAPFactory fac = SOAPFactory.newInstance();

            Name rstName = fac.createName("RequestSecurityToken", "wst", WS_TRUST_NS);
            SOAPBodyElement rstElem = body.addBodyElement(rstName);

            Name reqTypeName = fac.createName("RequestType", "wst", WS_TRUST_NS);
            SOAPElement reqTypeElem = rstElem.addChildElement(reqTypeName);
            reqTypeElem.addTextNode(ACTION_URI);

            Name tokenName = fac.createName("TokenType", "wst", WS_TRUST_NS);
            SOAPElement tokenElem = rstElem.addChildElement(tokenName);
            tokenElem.addTextNode(SAML20_TOKEN_URN);

            Name trcParamsName = fac.createName("TRCParameters", "trc", TRC_NS);
            SOAPElement trcParamsElem = rstElem.addChildElement(trcParamsName);

            Name purposeOfUseName = fac.createName("PurposeOfUse", "trc", TRC_NS);
            SOAPElement purposeOfUseElem = trcParamsElem.addChildElement(purposeOfUseName);
            purposeOfUseElem.addTextNode(purposeOfUse);

            Name patientIdName = fac.createName("PatientId", "trc", TRC_NS);
            SOAPElement patientIdElem = trcParamsElem.addChildElement(patientIdName);
            patientIdElem.addTextNode(patientId);

            if (StringUtils.isNotBlank(dispensationPinCode)) {
                Name dispensationPinCodeName = fac.createName("DispensationPinCode", "trc", TRC_NS);
                SOAPElement dispensationPinCodeElement = trcParamsElem.addChildElement(dispensationPinCodeName);
                dispensationPinCodeElement.addTextNode(dispensationPinCode);
            }
            if (StringUtils.isNotBlank(prescriptionId)) {
                Name prescriptionIdName = fac.createName("PrescriptionId", "trc", TRC_NS);
                SOAPElement prescriptionIdElement = trcParamsElem.addChildElement(prescriptionIdName);
                prescriptionIdElement.addTextNode(prescriptionId);
            }

        } catch (SOAPException ex) {
            LOGGER.error(null, ex);
        }
    }

    /**
     * Sends the request to the TRC STS Service.
     *
     * @return the TRC Assertion that was received from the STS, if the request was successful.
     * @throws Exception if the request failed.
     */
    public Assertion request() throws Exception {

        try {
            LOGGER.info("TRC-STS client request Assertion");
            HttpURLConnection httpConnection = (HttpURLConnection) location.openConnection();
            //Set headers
            httpConnection.setRequestProperty("Content-Type", "application/soap+xml");
            httpConnection.setRequestProperty("SOAPAction", "");
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);

            LOGGER.info("Checking SSL Hostname Verifier: '{}'", CHECK_FOR_HOSTNAME);
            if (httpConnection instanceof HttpsURLConnection) {  // Going SSL
                ((HttpsURLConnection) httpConnection).setSSLSocketFactory(getSSLSocketFactory());
                if (StringUtils.equals(CHECK_FOR_HOSTNAME, "false"))
                    ((HttpsURLConnection) httpConnection).setHostnameVerifier(
                            (hostname, sslSession) -> true);
            }

            String value = System.getProperty("javax.net.ssl.key.alias");

            //  Write and send the SOAP message
            LOGGER.info("Sending SOAP request - Default Key Alias: '{}'", StringUtils.isNotBlank(value) ? value : "N/A");
            rstMsg.writeTo(httpConnection.getOutputStream());
            SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL)
                    .createMessage(new MimeHeaders(), httpConnection.getInputStream());

            LOGGER.info("Receiving SOAP response");
            if (response.getSOAPBody().hasFault()) {

                SOAPFault newFault = response.getSOAPBody().getFault();
                String code = newFault.getFaultCode();
                String string = newFault.getFaultString();
                throw new SOAPException("Code:" + code + ", Error String:" + string);
            }
            Assertion assertionTRCA = extractTRCAssertionFromRSTC(response);
            LOGGER.info("TRC Assertion: '{}'", assertionTRCA != null ? assertionTRCA.getID() : "TRC Assertion is NULL");
            return assertionTRCA;

        } catch (SOAPException ex) {
            throw new Exception("SOAP Exception: " + ex.getMessage());
        } catch (UnsupportedOperationException ex) {
            throw new Exception("Unsupported Operation: " + ex.getMessage());
        }
    }

    private Assertion extractTRCAssertionFromRSTC(SOAPMessage response) throws Exception {

        try {
            LOGGER.info("[TRC-STS Client] Extract TRC from RSTC");
            SOAPBody body = response.getSOAPBody();
            if (body.getElementsByTagNameNS(SAML20_TOKEN_URN, "Assertion").getLength() != 1) {
                throw new Exception("TRC Assertion is missing from the RSTRC body");
            }
            SOAPElement assertion = (SOAPElement) body.getElementsByTagNameNS(SAML20_TOKEN_URN, "Assertion").item(0);
            Document assertDoc = builder.newDocument();

            Node dupBody = assertDoc.importNode(assertion, true);
            assertDoc.appendChild(dupBody);
            if (assertion == null) {
                return null;
            }

            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(assertion);

            Assertion trcAssertion = (Assertion) unmarshaller.unmarshall(assertDoc.getDocumentElement());
            if (OpenNCPValidation.isValidationEnable()) {

                OpenNCPValidation.validateTRCAssertion(trcAssertion, NcpSide.NCP_B);
            }
            return trcAssertion;

        } catch (Exception ex) {
            throw new Exception("Error while trying to extract the SAML TRC Assertion from RSTRC Body: " + ex.getMessage());
        }
    }

    public SSLSocketFactory getSSLSocketFactory() {

        SSLContext ctx;
        try {
            KeyStoreManager ksm = new DefaultKeyStoreManager();
            String KEYSTORE_PASS = ConfigurationManagerFactory.getConfigurationManager().getProperty("NCP_SIG_KEYSTORE_PASSWORD");

            ctx = SSLContext.getInstance("TLSv1.2");

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ksm.getKeyStore(), KEYSTORE_PASS.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ksm.getTrustStore());

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return ctx.getSocketFactory();

        } catch (KeyManagementException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            LOGGER.error("Exception: '{}'", e.getMessage(), e);
            return null;
        }
    }

    /**
     * The Builder class is responsible for the creation of the final TRCAssertionRequest Object.
     * It is used to incrementally create the TRCAssertionRequest and then when calling #build returns the final immutable object
     */
    public static class Builder {

        //  Required attributes
        private final Assertion idAssert;
        private final String patientId;
        //  Optional attributes
        private String purposeOfUse = "TREATMENT";
        private String prescriptionId;
        private String dispensationPinCode;
        private URL location = null;

        /**
         * The Builder class constructor. Its parameters are the required fields of the TRCAssertionRequest Object.
         *
         * @param idAssert  The OpenSAML Identity Assertion
         * @param patientId the relevant patient id.
         */
        public Builder(Assertion idAssert, String patientId) {

            this.idAssert = idAssert;
            this.patientId = patientId;
            try {

                this.location = new URL(DEFAULT_STS_URL);
            } catch (MalformedURLException ex) {
                LOGGER.error(null, ex);
            }
        }

        public Builder prescriptionId(String prescriptionId) {

            this.prescriptionId = prescriptionId;
            return this;
        }

        public Builder dispensationPinCode(String dispensationPinCode) {

            this.dispensationPinCode = dispensationPinCode;
            return this;
        }

        /**
         * Method to incrementally add the Purpose Of Use parameter of the TRC Request.
         *
         * @param purposeOfUse Purpose Of use. Either TREATMENT or EMERGENCY
         * @return the Builder object for further initialization
         */
        public Builder purposeOfUse(String purposeOfUse) {

            this.purposeOfUse = purposeOfUse;
            return this;
        }

        /**
         * method to incrementally add the STS URL the request. If not added, the builder will use the one that exists
         * in the secman.sts.url parameter of the OpenNCP database properties.
         *
         * @param url the URL of the STS that the client will make the request
         * @return the Builder object for further initialization
         */
        public Builder location(String url) {

            try {
                this.location = new URL(url);
            } catch (MalformedURLException ex) {
                LOGGER.error(null, ex);
            }
            return this;
        }

        public SecurityTokenServiceRequest build() throws Exception {

            return new SecurityTokenServiceRequest(this);
        }
    }
}
