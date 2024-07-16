package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import eu.europa.ec.sante.openncp.core.common.ihe.evidence.etsi.rem.*;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.herasaf.xacml.core.policy.impl.AttributeAssignmentType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This class is a sample discharge of the Evidence Emitter.
 *
 * @author max
 */
public class ETSIREMObligationHandler implements ObligationHandler {

    // Prefixes, that matches the XACML policy
    private static final String REM_NRR_PREFIX = "urn:eSENS:obligations:nrr:ETSIREM";
    private static final String REM_NRO_PREFIX = "urn:eSENS:obligations:nro:ETSIREM";
    private static final String REM_NRD_PREFIX = "urn:eSENS:obligations:nrd:ETSIREM";
    private static final String VERSION = ":version";
    private static final String POLICY_ID = ":PolicyID";
    private static final String HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance("eu.europa.ec.sante.openncp.core.common.ihe.evidence.etsi.rem");
        } catch (final JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(ETSIREMObligationHandler.class);
    private final List<ESensObligation> obligations;
    private Document audit = null;
    private final Context context;

    public ETSIREMObligationHandler(final MessageType messageType, final List<ESensObligation> obligations, final Context context) {
        this.obligations = obligations;
        this.context = context;
    }

    /**
     * Discharge returns the object discharged, or exception(non-Javadoc)
     *
     * @throws ObligationDischargeException
     * @see ObligationHandler#discharge()
     */
    @Override
    public void discharge() throws ObligationDischargeException {

        final StopWatch watch = new StopWatch();
        watch.start();

        //  Here I need to check the IHE message type. It can be XCA, XCF, whatever
        //

        //  For the e-SENS pilot we issue the NRO and NRR token to all the incoming messages -> This is the per hop protocol.
        try {
            makeETSIREM();
        } catch (final Exception e) {
            watch.stop();
            throw new ObligationDischargeException(e);
        }
        watch.stop();
        logger.info("Time Elapsed: '{} ms'", watch.getTime());
    }

    /**
     * @throws DatatypeConfigurationException
     * @throws JAXBException
     * @throws CertificateEncodingException
     * @throws NoSuchAlgorithmException
     * @throws SOAPException
     * @throws ParserConfigurationException
     * @throws XMLSecurityException
     * @throws TransformerException
     */
    private void makeETSIREM() throws DatatypeConfigurationException, JAXBException, CertificateEncodingException,
            NoSuchAlgorithmException, SOAPException, ParserConfigurationException, XMLSecurityException, TransformerException {

        for (final ESensObligation eSensObligation : obligations) {

            logger.info("ObligationID: '{}'", eSensObligation.getObligationID());
            switch (eSensObligation.getObligationID()) {
                case REM_NRO_PREFIX:
                    audit = processNonRepudiationOfOrigin(eSensObligation);
                    break;
                case REM_NRR_PREFIX:
                    audit = processNonRepudiationOfReceipt(eSensObligation);
                    break;
                case REM_NRD_PREFIX:
                    audit = processNonRepudiationOfDelivery(eSensObligation);
                    break;
                default:
                    logger.warn("ETSI-REM evidence type not supported: '{}'", eSensObligation.getObligationID());
                    break;
            }
        }
    }

    /**
     * @param eSensObligation
     * @return signed ETSI REM Document
     * @throws ParserConfigurationException
     * @throws JAXBException
     * @throws CertificateEncodingException
     * @throws NoSuchAlgorithmException
     * @throws DatatypeConfigurationException
     * @throws SOAPException
     * @throws TransformerException
     * @throws XMLSecurityException
     */
    private Document processNonRepudiationOfDelivery(final ESensObligation eSensObligation) throws ParserConfigurationException,
            JAXBException, CertificateEncodingException, NoSuchAlgorithmException, DatatypeConfigurationException,
            SOAPException, TransformerException, XMLSecurityException {

        final ObjectFactory of = new ObjectFactory();
        final REMEvidenceType type = new REMEvidenceType();
        final String outcome;
        if (eSensObligation instanceof PERMITEsensObligation) {
            outcome = "Delivery";
        } else {
            outcome = "DeliveryExpiration";
        }
        final List<AttributeAssignmentType> listAttr = eSensObligation.getAttributeAssignments();

        type.setVersion(find(REM_NRD_PREFIX + VERSION, listAttr));
        type.setEventCode(outcome);
        type.setEvidenceIdentifier(UUID.randomUUID().toString());

        /*
         * ISO Token mappings
         */
        // This is the Pol field of the ISO13888 token
        final String policyUrl = find(REM_NRD_PREFIX + POLICY_ID, listAttr);
        if (policyUrl != null) {
            final EvidenceIssuerPolicyID eipid = new EvidenceIssuerPolicyID();
            eipid.getPolicyIDs().add(policyUrl);
            type.setEvidenceIssuerPolicyID(eipid);
        }
        mapToIso(type);

        // Imp is the signature
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(false);
        dbf.setFeature(HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL, true);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document document = db.newDocument();

        final JAXBElement<REMEvidenceType> back = of.createDeliveryNonDeliveryToRecipient(type);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(back, document);
        signObligation(document, context.getIssuerCertificate(), context.getSigningKey());
        return document;
    }

    private Document processNonRepudiationOfOrigin(final ESensObligation eSensObligation) throws ParserConfigurationException,
            JAXBException, CertificateEncodingException, NoSuchAlgorithmException, DatatypeConfigurationException,
            SOAPException, TransformerException, XMLSecurityException {

        final ObjectFactory of = new ObjectFactory();
        final REMEvidenceType type = new REMEvidenceType();
        final String outcome;
        if (eSensObligation instanceof PERMITEsensObligation) {
            outcome = "Acceptance";
        } else {
            outcome = "Rejection";
        }
        final List<AttributeAssignmentType> listAttr = eSensObligation.getAttributeAssignments();

        type.setVersion(find(REM_NRO_PREFIX + VERSION, listAttr));
        type.setEventCode(outcome);
        type.setEvidenceIdentifier(UUID.randomUUID().toString());

        /*
         * ISO Token mappings
         */
        // This is the Pol field of the ISO13888 token
        final EvidenceIssuerPolicyID eipid = new EvidenceIssuerPolicyID();
        eipid.getPolicyIDs().add(find(REM_NRO_PREFIX + POLICY_ID, listAttr));
        type.setEvidenceIssuerPolicyID(eipid);

        mapToIso(type);

        // Imp is the signature

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(false);
        dbf.setFeature(HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL, true);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document document = db.newDocument();

        final JAXBElement<REMEvidenceType> back = of.createSubmissionAcceptanceRejection(type);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(back, document);
        signObligation(document, context.getIssuerCertificate(), context.getSigningKey());
        return document;
    }

    private Document processNonRepudiationOfReceipt(final ESensObligation eSensObligation) throws ParserConfigurationException,
            JAXBException, CertificateEncodingException, NoSuchAlgorithmException, DatatypeConfigurationException,
            SOAPException, TransformerException, XMLSecurityException {

        final ObjectFactory of = new ObjectFactory();
        final REMEvidenceType type = new REMEvidenceType();
        final String outcome;
        if (eSensObligation instanceof PERMITEsensObligation) {
            outcome = "Acceptance";
        } else {
            outcome = "Rejection";
        }
        final List<AttributeAssignmentType> listAttr = eSensObligation.getAttributeAssignments();

        type.setVersion(find(REM_NRR_PREFIX + VERSION, listAttr));
        type.setEventCode(outcome);

        type.setEvidenceIdentifier(UUID.randomUUID().toString());

        /*
         * ISO Token mappings
         */
        // This is the Policy field of the ISO13888 token
        final EvidenceIssuerPolicyID eipid = new EvidenceIssuerPolicyID();
        eipid.getPolicyIDs().add(find(REM_NRR_PREFIX + POLICY_ID, listAttr));
        type.setEvidenceIssuerPolicyID(eipid);

        mapToIso(type);

        // Imp is the signature
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(false);
        dbf.setFeature(HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL, true);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document document = db.newDocument();

        final JAXBElement<REMEvidenceType> back = of.createAcceptanceRejectionByRecipient(type);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(back, document);
        signObligation(document, context.getIssuerCertificate(), context.getSigningKey());
        return document;
    }

    /**
     * @param type
     * @throws CertificateEncodingException
     * @throws NoSuchAlgorithmException
     * @throws DatatypeConfigurationException
     * @throws SOAPException
     * @throws TransformerException
     */
    private void mapToIso(final REMEvidenceType type) throws CertificateEncodingException, NoSuchAlgorithmException,
            DatatypeConfigurationException, SOAPException, TransformerException {

        // The flag f1 is the AcceptanceRejection (the evidence type)
        // This is the A field the originator
        final EntityDetailsType edt1 = new EntityDetailsType();

        if (context.getSenderCertificate() != null) {
            final CertificateDetails cd1 = new CertificateDetails();
            edt1.setCertificateDetails(cd1);
            cd1.setX509Certificate(context.getSenderCertificate().getEncoded());
        }
        // To check if null sender details is allowed
        type.setSenderDetails(edt1);

        // This is the B field, the recipient
        /*
         * Made optional by a request from the eJustice domain
         */
        final EntityDetailsType edt2 = new EntityDetailsType();

        if (context.getRecipientCertificate() != null) {

            final CertificateDetails cd2 = new CertificateDetails();
            edt2.setCertificateDetails(cd2);
            cd2.setX509Certificate(context.getRecipientCertificate().getEncoded());

        }
        if (context.getRecipientNamePostalAddress() != null) {
            final LinkedList<String> list = context.getRecipientNamePostalAddress();

            final NamesPostalAddresses npas = new NamesPostalAddresses();

            for (final String aList : list) {
                final EntityName en = new EntityName();
                en.getNames().add(aList);
                final NamePostalAddress npa = new NamePostalAddress();
                npa.setEntityName(en);
                npas.getNamePostalAddresses().add(npa);

            }
            edt2.setNamesPostalAddresses(npas);
        }

        final RecipientsDetails rd = new RecipientsDetails();
        rd.getEntityDetails().add(edt2);
        type.setRecipientsDetails(rd);

        // Evidence Issuer Details is the C field of the ISO token
        logger.debug("Context Details: Issuer:'{}', Recipient:'{}', Sender:'{}'",
                context.getIssuerCertificate() != null ? context.getIssuerCertificate().getSerialNumber() : "N/A",
                context.getRecipientCertificate() != null ? context.getRecipientCertificate().getSerialNumber() : "N/A",
                context.getSenderCertificate() != null ? context.getSenderCertificate().getSerialNumber() : "N/A");

        final EntityDetailsType edt = new EntityDetailsType();
        final CertificateDetails cd = new CertificateDetails();
        edt.setCertificateDetails(cd);
        cd.setX509Certificate(context.getIssuerCertificate().getEncoded());
        type.setEvidenceIssuerDetails(edt);

        // This is the T_g field
        final DateTime dt = new DateTime();
        type.setEventTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(dt.toGregorianCalendar()));

        // This is the T_1 field
        type.setSubmissionTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(context.getSubmissionTime().toGregorianCalendar()));

        // This is mandated by REM. If this is the full message,
        // we can avoid to build up the NRO Token as text||z_1||sa(z_1)
        final MessageDetailsType messageDetailsType = new MessageDetailsType();
        final DigestMethod dm = new DigestMethod();
        dm.setAlgorithm("SHA256");
        messageDetailsType.setDigestMethod(dm);

        // do the message digest
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.reset();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (context.getIncomingMsg() != null) {
            Utilities.serialize(context.getIncomingMsg().getSOAPBody().getOwnerDocument().getDocumentElement(), baos);

        } else if (context.getIncomingMsgAsDocument() != null) {
            Utilities.serialize(context.getIncomingMsgAsDocument().getDocumentElement(), baos);

        } else {
            throw new IllegalStateException("Not valid incoming Message passed");
        }
        messageDigest.update(baos.toByteArray());
        messageDetailsType.setDigestValue(messageDigest.digest());
        messageDetailsType.setIsNotification(false);
        messageDetailsType.setMessageSubject(context.getEvent());
        messageDetailsType.setUAMessageIdentifier(context.getMessageUUID());
        // Set UUID of the message, we don't handle the local parts.
        messageDetailsType.setMessageIdentifierByREMMD(context.getMessageUUID());
        messageDetailsType.setDigestMethod(dm);
        type.setSenderMessageDetails(messageDetailsType);

        final AuthenticationDetailsType authenticationDetails = new AuthenticationDetailsType();
        authenticationDetails.setAuthenticationMethod(context.getAuthenticationMethod());
        // this is the authentication time. I set it as "now", since it is required by the REM, but it is not used here.
        final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
        authenticationDetails.setAuthenticationTime(xmlGregorianCalendar);

        type.setSenderAuthenticationDetails(authenticationDetails);
    }

    /**
     * @param string
     * @param listAttr
     * @return
     */
    private String find(final String string, final List<AttributeAssignmentType> listAttr) {

        for (final AttributeAssignmentType att : listAttr) {
            if (att.getAttributeId().equals(string)) {
                return ((String) att.getContent().get(0)).trim();
            }
        }
        return null;
    }

    /**
     * @param doc
     * @param cert
     * @param key
     * @throws XMLSecurityException
     */
    private void signObligation(final Document doc, final X509Certificate cert, final PrivateKey key) throws XMLSecurityException {

        final String baseURI = "./";
        final XMLSignature signature = new XMLSignature(doc, baseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);
        doc.getDocumentElement().appendChild(signature.getElement());
        doc.appendChild(doc.createComment(" Comment after "));

        final Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

        signature.addDocument("", transforms, javax.xml.crypto.dsig.DigestMethod.SHA256);
        signature.addKeyInfo(cert);
        signature.addKeyInfo(cert.getPublicKey());
        signature.sign(key);
    }

    @Override
    public Document getMessage() {
        return audit;
    }
}
