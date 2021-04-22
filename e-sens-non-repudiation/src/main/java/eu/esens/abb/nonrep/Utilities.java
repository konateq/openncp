package eu.esens.abb.nonrep;

import com.sun.xml.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.impl.XSDateTimeMarshaller;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.impl.PolicySetTypeMarshaller;
import org.opensaml.xacml.policy.impl.PolicyTypeMarshaller;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;
import org.opensaml.xacml.profile.saml.impl.XACMLAuthzDecisionQueryTypeMarshaller;
import org.opensaml.xacml.profile.saml.impl.XACMLPolicyStatementTypeMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.soap.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class Utilities {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utilities.class);
    private static final Logger LOGGER_CLINICAL = LoggerFactory.getLogger("LOGGER_CLINICAL");
    private static final String SERVER_EHEALTH_MODE = "server.ehealth.mode";

    private Utilities() {
    }

    public static void checkForNull(final NodeList nodeList, final String toCheck, final Logger logger) throws MalformedIHESOAPException {

        if (nodeList == null || nodeList.getLength() != 1) {
            String error = "No " + toCheck + " found";
            logger.error(error);
            throw new MalformedIHESOAPException(error);
        }
    }

    public static Element toElement(XMLObject a) throws TOElementException {

        MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
        if (marshallerFactory == null) {
            throw new TOElementException("No MarshallerFactory available. OpenSAML not initialized!!!");
        }

        // register some marshaller
        marshallerFactory.registerMarshaller(XACMLPolicyStatementType.DEFAULT_ELEMENT_NAME_XACML20,
                new XACMLPolicyStatementTypeMarshaller());
        marshallerFactory.registerMarshaller(XACMLAuthzDecisionQueryType.DEFAULT_ELEMENT_NAME_XACML20,
                new XACMLAuthzDecisionQueryTypeMarshaller());
        marshallerFactory.registerMarshaller(PolicySetType.DEFAULT_ELEMENT_NAME, new PolicySetTypeMarshaller());
        marshallerFactory.registerMarshaller(PolicyType.DEFAULT_ELEMENT_NAME, new PolicyTypeMarshaller());
        marshallerFactory.registerMarshaller(XSDateTime.TYPE_NAME, new XSDateTimeMarshaller());

        Marshaller marshaller = marshallerFactory.getMarshaller(a);

        if (marshaller == null) {

            // The XACMLPolicyStatementType needs a separate marshaller
            if (a instanceof XACMLPolicyStatementType) {
                Marshaller policyStmtMarshaller = marshallerFactory.getMarshaller(XACMLPolicyStatementType.DEFAULT_ELEMENT_NAME_XACML20);

                try {
                    return policyStmtMarshaller.marshall(a);

                } catch (MarshallingException e) {
                    throw new TOElementException(e);
                }
            }
            throw new TOElementException("No marshaller found for the xmlobject");
        }

        Element assertionElement;
        try {
            assertionElement = marshaller.marshall(a);
            return assertionElement;
        } catch (Exception e) {
            throw new TOElementException("Unable to marshall the assertion: " + e.getMessage(), e);
        }
    }

    public static void serialize(Element request) throws TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serialize(request, outputStream);
    }

    /**
     * Added for handling alternative outputs (instead of only system out)
     *
     * @param request
     * @param out
     * @throws TransformerException
     */
    public static void serialize(Element request, OutputStream out) throws TransformerException {

        //TODO: @DG Sante - Validate this serialization
        DOMSource source = new DOMSource(request);
        StreamResult result = new StreamResult(out);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.transform(source, result);
    }

    /**
     * @param doc
     * @param requestMimeHeaders
     * @return
     * @throws SOAPException
     */
    public static synchronized SOAPMessage toSoap(Document doc, MimeHeaders requestMimeHeaders) throws SOAPException {

        MessageFactory messageFactory = new SOAPMessageFactory1_2Impl();
        SOAPMessage message = messageFactory.createMessage();

        DOMSource domSource = new DOMSource(doc);
        message.getSOAPPart().setContent(domSource);

        LOGGER.info("Checking if I need to add mime headers");

        // If I have some mime headers, I have to remove them first.
        if (requestMimeHeaders != null) {

            LOGGER.info("Now adding the ones requested");
            Iterator<?> it = requestMimeHeaders.getAllHeaders();
            while (it.hasNext()) {

                MimeHeader mimeItem = (MimeHeader) it.next();
                String retValue = mimeItem.getValue().replace("Multipart/Related", "multipart/related");
                LOGGER.info("ADDING MIME: '{}' : '{}'", mimeItem.getName(), retValue);
                message.getMimeHeaders().addHeader(mimeItem.getName(), retValue);
            }
            message.saveChanges();
        }
        if (message.saveRequired()) {
            LOGGER.info("Saving changes");
            message.saveChanges();
        }

        MimeHeaders mh = message.getMimeHeaders();
        String content = mh.getHeader("Content-Type")[0].replace("Multipart/Related", "multipart/related");

        mh.setHeader("Content-Type", content + ";");

        return message;
    }
}
