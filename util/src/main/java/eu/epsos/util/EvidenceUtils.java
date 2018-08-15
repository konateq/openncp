package eu.epsos.util;

import eu.esens.abb.nonrep.*;
import org.apache.commons.lang3.StringUtils;
import org.herasaf.xacml.core.SyntaxException;
import org.herasaf.xacml.core.api.PDP;
import org.herasaf.xacml.core.api.UnorderedPolicyRepository;
import org.herasaf.xacml.core.policy.PolicyMarshaller;
import org.herasaf.xacml.core.simplePDP.SimplePDPFactory;
import org.herasaf.xacml.core.utils.JAXBMarshallerConfiguration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tr.com.srdc.epsos.util.DateUtil;
import tr.com.srdc.epsos.util.FileUtil;
import tr.com.srdc.epsos.util.XMLUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author karkaletsis
 */
public class EvidenceUtils {

    public static final String IHE_ITI_XCA_RETRIEVE = "urn:ihe:iti:2007:CrossGatewayRetrieve";
    private static final Logger LOGGER = LoggerFactory.getLogger(EvidenceUtils.class);
    private static final Logger LOGGER_CLINICAL = LoggerFactory.getLogger("LOGGER_CLINICAL");
    private static final String DATATYPE_STRING = "http://www.w3.org/2001/XMLSchema#string";
    private static final String DATATYPE_DATETIME = "http://www.w3.org/2001/XMLSchema#dateTime";

    private EvidenceUtils() {
    }

    private static boolean checkCorrectnessofIHEXCA(final MessageType messageType) {
        return true;
    }

    public static void createEvidenceREMNRR(Document incomingMsg, String issuerKeyStorePath, String issuerKeyPassword,
                                            String issuerCertAlias, String senderKeyStorePath, String senderKeyPassword,
                                            String senderCertAlias, String recipientKeyStorePath, String recipientKeyPassword,
                                            String recipientCertAlias, String eventType, DateTime submissionTime,
                                            String status, String title)
            throws IOException, URISyntaxException, TOElementException, EnforcePolicyException, ObligationDischargeException,
            TransformerException, SyntaxException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
            UnrecoverableKeyException {

        if (!StringUtils.equals(System.getProperty("server.ehealth.mode"), "PROD")) {
            LOGGER_CLINICAL.debug("[Evidences] createEvidenceREMNRR()\nIncoming message:\n'{}'\n Issuer Info: '{}'-'{}'-'{}', " +
                            "Sender Info: '{}'-'{}'-'{}', Recipient Info: '{}'-'{}'-'{}'\nEvent Info: '{}'-'{}'-'{}'-'{}'",
                    XMLUtil.documentToString(incomingMsg), issuerKeyStorePath, issuerKeyPassword, issuerCertAlias, senderKeyStorePath,
                    senderKeyPassword, senderCertAlias, recipientKeyStorePath, recipientKeyPassword, recipientCertAlias, eventType,
                    submissionTime, status, title);
        }
        MessageType messageType = null;
        String msguuid;
        try {
            MessageInspector messageInspector = new MessageInspector(incomingMsg);
            messageType = messageInspector.getMessageType();
            msguuid = messageInspector.getMessageUUID();
        } catch (Exception e) {
            LOGGER.error("Exception: '{}'", e.getMessage(), e);
            UnknownMessageType umt = new UnknownMessageType(incomingMsg);
            messageType = umt;
            msguuid = UUID.randomUUID().toString();
        }
        createEvidenceREMNRR(incomingMsg, issuerKeyStorePath, issuerKeyPassword, issuerCertAlias, senderKeyStorePath,
                senderKeyPassword, senderCertAlias, recipientKeyStorePath, recipientKeyPassword, recipientCertAlias,
                eventType, submissionTime, status, title, msguuid);
    }

    public static void createEvidenceREMNRR(Document incomingMsg, String issuerKeyStorePath, String issuerKeyPassword,
                                            String issuerCertAlias, String senderKeyStorePath, String senderKeyPassword,
                                            String senderCertAlias, String recipientKeyStorePath, String recipientKeyPassword,
                                            String recipientCertAlias, String eventType, DateTime submissionTime,
                                            String status, String title, String msguuid)
            throws IOException, URISyntaxException, TOElementException, EnforcePolicyException,
            ObligationDischargeException, TransformerException, SyntaxException, KeyStoreException,
            NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {

        if (!StringUtils.equals(System.getProperty("server.ehealth.mode"), "PROD")) {
            LOGGER_CLINICAL.debug("[Evidences] createEvidenceREMNRR()\nIncoming message:\n'{}'\n Issuer Info: '{}'-'{}'-'{}', " +
                            "Sender Info: '{}'-'{}'-'{}', Recipient Info: '{}'-'{}'-'{}'\nEvent Info: '{}'-'{}'-'{}'-'{}'-'{}'",
                    XMLUtil.documentToString(incomingMsg), issuerKeyStorePath, issuerKeyPassword, issuerCertAlias, senderKeyStorePath,
                    senderKeyPassword, senderCertAlias, recipientKeyStorePath, recipientKeyPassword, recipientCertAlias, eventType,
                    submissionTime, status, title, msguuid);
        }
        String statusmsg = "failure";
        if (StringUtils.equals("0", status)) {
            statusmsg = "success";
        }

        PDP simplePDP = SimplePDPFactory.getSimplePDP();
        UnorderedPolicyRepository polrep = (UnorderedPolicyRepository) simplePDP.getPolicyRepository();
        ClassLoader loader;
        loader = EvidenceUtils.class.getClassLoader();
        InputStream inputStream = loader.getResourceAsStream("policy/samplePolicyNRR.xml");
        polrep.deploy(PolicyMarshaller.unmarshal(inputStream));

        /*
         * Instantiate the message inspector, to see which type of message is
         */
        MessageType messageType;

        try {
            MessageInspector messageInspector = new MessageInspector(incomingMsg);
            messageType = messageInspector.getMessageType();
        } catch (Exception e) {
            LOGGER.error("Exception: '{}'", e.getMessage(), e);
            UnknownMessageType umt = new UnknownMessageType(incomingMsg);
            messageType = umt;
        }
        /*
         * Now create the XACML request
         */
        LinkedList<XACMLAttributes> actionList = new LinkedList<>();
        XACMLAttributes action = new XACMLAttributes();
        action.setDataType(new URI(DATATYPE_STRING));
        action.setIdentifier(new URI("urn:eSENS:outcome"));
        actionList.add(action);
        action.setValue(statusmsg);

        LinkedList<XACMLAttributes> environmentList = new LinkedList<>();
        XACMLAttributes environment = new XACMLAttributes();
        environment.setDataType(new URI(DATATYPE_DATETIME));
        environment.setIdentifier(new URI("urn:esens:2014:event"));
        environment.setValue(new DateTime().toString());
        environmentList.add(environment);

        XACMLRequestCreator requestCreator = new XACMLRequestCreator(messageType, null, null,
                actionList, environmentList);

        Element request = requestCreator.getRequest();

        // just some printouts
        Utilities.serialize(request);

        EnforcePolicy enforcePolicy = new EnforcePolicy(simplePDP);
        enforcePolicy.decide(request);

        Utilities.serialize(enforcePolicy.getResponseAsDocument().getDocumentElement());

        List<ESensObligation> obligations = enforcePolicy.getObligationList();

        Context context = new Context();
        context.setIncomingMsg(incomingMsg);

        /* Loading the different certificates */
        X509Certificate issuerCert = getCertificate(issuerKeyStorePath, issuerKeyPassword, issuerCertAlias);
        X509Certificate senderCert = getCertificate(senderKeyStorePath, senderKeyPassword, senderCertAlias);
        X509Certificate recipientCert = getCertificate(recipientKeyStorePath, recipientKeyPassword, recipientCertAlias);
        context.setIssuerCertificate(issuerCert);
        context.setSenderCertificate(senderCert);
        context.setRecipientCertificate(recipientCert);

        /* Signing key is the issuer key */
        PrivateKey key = getSigningKey(issuerKeyStorePath, issuerKeyPassword, issuerCertAlias);
        context.setSigningKey(key);
        context.setSubmissionTime(submissionTime);
        context.setEvent(eventType);
        context.setMessageUUID(msguuid);
        context.setAuthenticationMethod("http://uri.etsi.org/REM/AuthMethod#Strong");
        context.setRequest(request);
        context.setEnforcer(enforcePolicy);

        ObligationHandlerFactory handlerFactory = ObligationHandlerFactory.getInstance();
        List<ObligationHandler> handlers = handlerFactory.createHandler(messageType, obligations, context);

        for (ObligationHandler oh : handlers) {

            oh.discharge();
            Utilities.serialize(oh.getMessage().getDocumentElement());
            String oblString = XMLUtil.documentToString(oh.getMessage());
            if (title == null || title.isEmpty()) {
                title = getPath() + "nrr/" + getDocumentTitle(msguuid, oh.toString()) + ".xml";
            } else {
                title = getPath() + "nrr/" + getDocumentTitle(msguuid, title) + ".xml";
            }
            LOGGER.info("MSGUUID: '{}'  NRR TITLE: '{}'", msguuid, title);
            FileUtil.constructNewFile(title, oblString.getBytes());
        }
    }

    public static void createEvidenceREMNRO(Document incomingSoap, String issuerKeyStorePath, String issuerKeyPassword,
                                            String issuerCertAlias, String senderKeyStorePath, String senderKeyPassword,
                                            String senderCertAlias, String recipientKeyStorePath, String recipientKeyPassword,
                                            String recipientCertAlias, String eventType, DateTime submissionTime, String status,
                                            String title) throws Exception {

        if (!StringUtils.equals(System.getProperty("server.ehealth.mode"), "PROD")) {
            LOGGER_CLINICAL.debug("[Evidences] createEvidenceREMNRO()\nIncoming message:\n'{}'\n Issuer Info: '{}'-'{}'-'{}', " +
                            "Sender Info: '{}'-'{}'-'{}', Recipient Info: '{}'-'{}'-'{}'\nEvent Info: '{}'-'{}'-'{}'-'{}'",
                    XMLUtil.documentToString(incomingSoap), issuerKeyStorePath, issuerKeyPassword, issuerCertAlias, senderKeyStorePath,
                    senderKeyPassword, senderCertAlias, recipientKeyStorePath, recipientKeyPassword, recipientCertAlias, eventType, submissionTime, status, title);
        }
        MessageType messageType = null;
        String msguuid;
        try {
            MessageInspector messageInspector = new MessageInspector(incomingSoap);
            messageType = messageInspector.getMessageType();
            msguuid = messageInspector.getMessageUUID();
        } catch (Exception e) {
            LOGGER.error("Exception: '{}'", e.getMessage(), e);
            UnknownMessageType umt = new UnknownMessageType(incomingSoap);
            messageType = umt;
            msguuid = UUID.randomUUID().toString();
        }
        createEvidenceREMNRO(incomingSoap, issuerKeyStorePath, issuerKeyPassword,
                issuerCertAlias, senderKeyStorePath, senderKeyPassword,
                senderCertAlias, recipientKeyStorePath, recipientKeyPassword,
                recipientCertAlias, eventType, submissionTime, status, title, msguuid);

    }

    public static void createEvidenceREMNRO(Document incomingSoap, String issuerKeyStorePath, String issuerKeyPassword,
                                            String issuerCertAlias, String senderKeyStorePath, String senderKeyPassword,
                                            String senderCertAlias, String recipientKeyStorePath, String recipientKeyPassword,
                                            String recipientCertAlias, String eventType, DateTime submissionTime,
                                            String status, String title, String msguuid) throws Exception {

        if (!StringUtils.equals(System.getProperty("server.ehealth.mode"), "PROD")) {
            LOGGER_CLINICAL.debug("[Evidences] createEvidenceREMNRO()\nIncoming message:\n'{}'\n Issuer Info: '{}'-'{}'-'{}', " +
                            "Sender Info: '{}'-'{}'-'{}', Recipient Info: '{}'-'{}'-'{}'\nEvent Info: '{}'-'{}'-'{}'-'{}'-'{}'",
                    XMLUtil.documentToString(incomingSoap), issuerKeyStorePath, issuerKeyPassword, issuerCertAlias, senderKeyStorePath,
                    senderKeyPassword, senderCertAlias, recipientKeyStorePath, recipientKeyPassword, recipientCertAlias, eventType,
                    submissionTime, status, title, msguuid);
        }

        String statusmsg = "failure";
        if (StringUtils.equals("0", status)) {

            statusmsg = "success";
        }

        PDP simplePDP = SimplePDPFactory.getSimplePDP();
        UnorderedPolicyRepository polrep = (UnorderedPolicyRepository) simplePDP.getPolicyRepository();

        JAXBMarshallerConfiguration conf = new JAXBMarshallerConfiguration();
        conf.setValidateParsing(false);
        conf.setValidateWriting(false);
        PolicyMarshaller.setJAXBMarshallerConfiguration(conf);
        // Populate the policy repository
        ClassLoader loader;
        loader = EvidenceUtils.class.getClassLoader();
        InputStream inputStream = loader.getResourceAsStream("policy/samplePolicy.xml");
        polrep.deploy(PolicyMarshaller.unmarshal(inputStream));

        // Read the message as it arrives at the facade
        MessageType messageType = null;
        try {
            MessageInspector messageInspector = new MessageInspector(incomingSoap);
            messageType = messageInspector.getMessageType();
        } catch (Exception e) {
            LOGGER.error("Exception: '{}'", e.getMessage(), e);
            UnknownMessageType umt = new UnknownMessageType(incomingSoap);
            messageType = umt;
        }
        if (checkCorrectnessofIHEXCA(messageType)) {
            LOGGER.info("The message type : '{}' is correct.", messageType);
        }

        /*
         * Now create the XACML request
         */
        LinkedList<XACMLAttributes> actionList = new LinkedList<>();
        XACMLAttributes action = new XACMLAttributes();
        action.setDataType(new URI(DATATYPE_STRING));
        action.setIdentifier(new URI("urn:eSENS:outcome"));
        actionList.add(action);
        action.setValue(statusmsg);

        LinkedList<XACMLAttributes> environmentList = new LinkedList<>();
        XACMLAttributes environment = new XACMLAttributes();
        environment.setDataType(new URI(DATATYPE_DATETIME));
        environment.setIdentifier(new URI("urn:esens:2014:event"));
        environment.setValue(new DateTime().toString());
        environmentList.add(environment);

        XACMLRequestCreator requestCreator = new XACMLRequestCreator(messageType, null, null,
                actionList, environmentList);

        Element request = requestCreator.getRequest();
        Utilities.serialize(request);

        /*
         * Call the XACML engine.
         *
         * The policy has been deployed in the setupBeforeClass.
         */
        EnforcePolicy enforcePolicy = new EnforcePolicy(simplePDP);
        enforcePolicy.decide(request);

        Utilities.serialize(enforcePolicy.getResponseAsDocument().getDocumentElement());
        List<ESensObligation> obligations = enforcePolicy.getObligationList();
        Context context = new Context();
        context.setIncomingMsg(incomingSoap);

        /* Loading the different certificates */
        X509Certificate issuerCert = getCertificate(issuerKeyStorePath, issuerKeyPassword, issuerCertAlias);
        X509Certificate senderCert = getCertificate(senderKeyStorePath, senderKeyPassword, senderCertAlias);
        X509Certificate recipientCert = getCertificate(recipientKeyStorePath, recipientKeyPassword, recipientCertAlias);
        context.setIssuerCertificate(issuerCert);
        context.setSenderCertificate(senderCert);
        context.setRecipientCertificate(recipientCert);

        /* Signing key is the issuer key */
        PrivateKey key = getSigningKey(issuerKeyStorePath, issuerKeyPassword, issuerCertAlias);
        context.setSigningKey(key);
        context.setSubmissionTime(submissionTime);
        context.setEvent(eventType);
        context.setMessageUUID(msguuid);
        context.setAuthenticationMethod("http://uri.etsi.org/REM/AuthMethod#Strong");
        context.setRequest(request);
        context.setEnforcer(enforcePolicy);
        ObligationHandlerFactory handlerFactory = ObligationHandlerFactory.getInstance();
        List<ObligationHandler> handlers = handlerFactory.createHandler(messageType, obligations, context);

        // Here I discharge manually. This behavior is to let free an implementation
        for (ObligationHandler handler : handlers) {

            handler.discharge();
            Utilities.serialize(handler.getMessage().getDocumentElement());
            String oblString = XMLUtil.documentToString(handler.getMessage());
            if (title == null || title.isEmpty()) {
                title = getPath() + "nro/" + getDocumentTitle(msguuid, handler.toString()) + ".xml";
            } else {
                title = getPath() + "nro/" + getDocumentTitle(msguuid, title) + ".xml";
            }
            LOGGER.info("MSGUUID: '{}'  NRO TITLE: '{}'", msguuid, title);
            FileUtil.constructNewFile(title, oblString.getBytes());
        }
    }

    private static String getPath() {

        String exportPath = System.getenv("EPSOS_PROPS_PATH");
        String evidencesPath = exportPath + "obligations/";
        LOGGER.debug("Evidences Path: '{}'", evidencesPath);
        return evidencesPath;
    }

    private static String getDocumentTitle(String uuid, String title) {

        return DateUtil.getCurrentTimeGMT() + "_" + uuid + "_" + title;
    }

    public static Document readMessage(String file) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new File(file));
    }

    private static X509Certificate getCertificate(String keyStorePath, String keyPassword, String certAlias)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {

        LOGGER.debug("X509Certificate getCertificate('{}', '{}', '{}')", keyStorePath, StringUtils.isNotBlank(keyPassword) ? "******" : "N/A", certAlias);
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream keyStream = new FileInputStream(new File(keyStorePath));
        ks.load(keyStream, keyPassword == null ? null : keyPassword.toCharArray());
        return (X509Certificate) ks.getCertificate(certAlias);
    }

    private static PrivateKey getSigningKey(String keyStorePath, String keyPassword, String certAlias)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {

        LOGGER.debug("PrivateKey getSigningKey('{}', '{}', '{}')", keyStorePath, StringUtils.isNotBlank(keyPassword) ? "******" : "N/A", certAlias);
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream keyStream = new FileInputStream(new File(keyStorePath));
        ks.load(keyStream, keyPassword == null ? null : keyPassword.toCharArray());
        return (PrivateKey) ks.getKey(certAlias, keyPassword == null ? null : keyPassword.toCharArray());
    }
}
