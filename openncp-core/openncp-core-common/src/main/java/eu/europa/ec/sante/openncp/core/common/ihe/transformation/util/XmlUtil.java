package eu.europa.ec.sante.openncp.core.common.ihe.transformation.util;

import eu.europa.ec.sante.openncp.common.util.NoNsXpath;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaxen.JaxenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlUtil implements TMConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);
    private static final String TRANSFORMER_EXCEPTION = "TransformerException: '{}'";
    private static final String EXCEPTION = "Exception: '{}'";
    private static final String HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

    private XmlUtil() {
    }

    /**
     * Simply prints Node as String (useful for logging/testing purpose)
     *
     * @param node Node element
     * @return String representation of the Node element
     */
    public static String xmlToString(Node node) {

        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerException e) {
            LOGGER.error(TRANSFORMER_EXCEPTION, e.getMessage());
        }
        return null;
    }

    public static String nodeListToString(List<Node> nodes) {
        try {
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            for (Node node : nodes) {
                Source source = new DOMSource(node);
                transformer.transform(source, result);
            }

            return stringWriter.getBuffer().toString();
        } catch (TransformerException e) {
            LOGGER.error(TRANSFORMER_EXCEPTION, e.getMessage());
        }
        return null;
    }

    /**
     * Creates DOM Document from File
     *
     * @param file           XML File
     * @param namespaceAware boolean parameter, determines if Document is namespaceAware or not
     * @return Document
     */
    public static Document getDocument(File file, boolean namespaceAware) {

        try {
            return getDocument(new FileInputStream(file), namespaceAware);
        } catch (FileNotFoundException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e);
            return null;
        }
    }

    public static Document getDocument(InputStream inputStream, boolean namespaceAware) {

        Document document = null;
        try {
            // Parse a XML document into a DOM tree
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            documentFactory.setFeature(HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL, true);
            documentFactory.setXIncludeAware(false);
            documentFactory.setNamespaceAware(namespaceAware);
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            document = documentBuilder.parse(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e);
        }
        return document;
    }

    /**
     * Creates DOM Document from ByteArray
     *
     * @param xml            input ByteArray
     * @param namespaceAware boolean parameter, determines if Document is namespaceAware or not
     * @return Document
     */
    public static Document bytesToXml(byte[] xml, boolean namespaceAware) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        DocumentBuilder builder;
        try {
            factory.setFeature(HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL, true);
            factory.setXIncludeAware(false);
            builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns ByteArray from input Node
     *
     * @param node Input Node (Document for example)
     * @return ByteArray
     */
    public static byte[] doc2bytes(Node node) {

        try {
            Source source = new DOMSource(node);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Result result = new StreamResult(out);
            TransformerFactory factory = TransformerFactory.newInstance(
                    "net.sf.saxon.TransformerFactoryImpl", null);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return out.toByteArray();
        } catch (TransformerException e) {
            LOGGER.error(TRANSFORMER_EXCEPTION, e.getMessage(), e);
        }
        return new byte[]{};
    }

    /**
     * Returns namespaceAware Document (namespaceAware Document is required by validation against schema)
     *
     * @param inputDocument Document
     * @return output NamespaceAware Document
     */
    public static Document getNamespaceAwareDocument(Document inputDocument) {

        return bytesToXml(doc2bytes(inputDocument), true);
    }

    /**
     * Returns namespaceAware Document (namespaceAware Document is required by validation against schema)
     *
     * @param inputDocumentBytes Document as byte array
     * @return output NamespaceAware Document
     */
    public static Document getNamespaceAwareDocument(byte[] inputDocumentBytes) {

        return bytesToXml(inputDocumentBytes, true);
    }

    /**
     * Returns namespace NOT Aware Document (namespace NOT Aware Document is required bye XPath evaluation)
     *
     * @param inputDocument Document
     * @return output Namespace NOT aware Document
     */
    public static Document getNamespaceNOTAwareDocument(Document inputDocument) {

        return bytesToXml(doc2bytes(inputDocument), false);
    }

    /**
     * Using XPath expression evaluates input node
     *
     * @param node            input Document
     * @param xPathExpression XPath expression to be evaluated
     * @return NodeList
     */
    public static List<Node> getNodeList(Node node, String xPathExpression) {

        List<Node> result;
        try {
            NoNsXpath xpath = new NoNsXpath(xPathExpression);
            result = xpath.selectNodes(node);
        } catch (JaxenException e) {
            LOGGER.error("JaxenException: XPath: '{}', Node: '{}'", xPathExpression, node, e);
            return new ArrayList<>();
        }
        return result;
    }

    public static Node getNode(Node node, String xPathExpression) {

        try {
            NoNsXpath xpath = new NoNsXpath(xPathExpression);
            return (Node) xpath.selectSingleNode(node);
        } catch (JaxenException e) {
            LOGGER.error("JaxenException: XPath: '{}', Node: '{}'", xPathExpression, node, e);
            return null;
        }
    }

    public static Document stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL, true);
        factory.setXIncludeAware(false);
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

    /**
     * Workaround how to avoid empty xmlns="" by processing document
     *
     * @param input Document to process
     * @return Document with empty namespace removed
     */
    public static Document removeEmptyXmlns(Document input) throws SAXException, ParserConfigurationException, IOException {

        String documentToString = xmlToString(input);
        documentToString = RegExUtils.removeAll(documentToString, EMPTY_XMLNS);
        return stringToDom(documentToString);
    }

    public static Document parseContent(String content) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature(HTTP_APACHE_ORG_XML_FEATURES_DISALLOW_DOCTYPE_DECL, true);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        StringReader lReader = new StringReader(content);
        InputSource inputSource = new InputSource(lReader);
        return documentBuilder.parse(inputSource);
    }

    public static String getElementPath(Node e) {

        ArrayList<String> path = new ArrayList<>();
        Node parent = null;
        while (!(parent instanceof Document)) {
            String name = e.getLocalName();
            Node prev = e;
            Node next = e;
            int pos = 1;
            Node tmp = e;

            while (prev != null) {

                prev = tmp.getPreviousSibling();
                if (prev != null) {
                    tmp = prev;
                    if (prev.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    if (StringUtils.equals(prev.getLocalName(), name)) {
                        pos++;
                    }
                }
            }
            // CZ Translation: If POS is null, Checking the following elements if there is more than one with the same name
            boolean more = false;
            if (pos == 1) {
                tmp = e;
                while (next != null) {
                    next = tmp.getNextSibling();

                    if (next != null) {

                        tmp = next;
                        if (next.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }
                        if (next.getLocalName() != null && StringUtils.equals(next.getLocalName(), name)) {
                            more = true;
                            break;
                        }
                    }
                }
            }
            if (e.getPrefix() != null && !e.getPrefix().isEmpty()) {
                name = e.getPrefix() + ":" + name;
            }
            if (pos > 1 || more) {
                name = name + "[" + pos + "]";
            }
            path.add(name);
            parent = e.getParentNode();
            e = parent;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = path.size() - 1; i >= 0; i--) {
            sb.append("/");
            sb.append(path.get(i));
        }
        return sb.toString();
    }
}

class CdaNameSpaceContext implements NamespaceContext {

    private static final String NS_CDA = "urn:hl7-org:v3";

    public String getNamespaceURI(String prefix) {
        if (StringUtils.equals("nsCda", prefix)) {
            return NS_CDA;
        }
        return null;
    }

    public String getPrefix(String namespaceURI) {
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
