package eu.europa.ec.sante.openncp.core.client.connector.cdadisplaytool.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

abstract class AbstractValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractValidator.class);

    protected static Document transformStringToDocument(String documentAsString) throws ParserConfigurationException {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document document = null;
        final DocumentBuilder builder;
        if (isHTML(documentAsString)) {
            if (documentAsString.contains("<meta")) {
                String subString = documentAsString.substring(documentAsString.indexOf("<meta"));
                subString = subString.substring(0, subString.indexOf(">") + 1);
                documentAsString = documentAsString.replaceAll(subString, "");
            }
            documentAsString = documentAsString.replaceAll("<br>", "");
            documentAsString = documentAsString.replaceAll("</br>", "");
            do {
                if (documentAsString.contains("<br")) {
                    String subString = documentAsString.substring(documentAsString.indexOf("<br"));
                    subString = subString.substring(0, subString.indexOf(">") + 1);
                    documentAsString = documentAsString.replaceAll(subString, "");
                }
            } while (documentAsString.contains("<br"));
            if (documentAsString.contains("<hr")) {
                String subString = documentAsString.substring(documentAsString.indexOf("<hr"));
                subString = subString.substring(0, subString.indexOf(">") + 1);
                documentAsString = documentAsString.replaceAll(subString, "");
            }
        }
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(documentAsString)));
        } catch (final Exception e) {
            LOGGER.error("{}: '{}'", e.getClass(), e.getMessage(), e);
        }
        return document;
    }

    private static boolean isHTML(final String documentAsString) {
        return documentAsString.contains("<html");
    }

    public void validate(final String cda, final String resultHtml) throws XPathExpressionException, ParserConfigurationException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final Document cdaDoc = transformStringToDocument(cda);
        final Document resultDoc = transformStringToDocument(resultHtml);

        validateTitle(xpath, cdaDoc, resultDoc);
        validatePatientName(xpath, cdaDoc, resultDoc);
        validateActiveIngredients(xpath, cdaDoc, resultDoc);
    }

    protected abstract void validateActiveIngredients(XPath xpath, Document cdaDoc, Document resultDoc) throws XPathExpressionException;

    protected abstract void validateTitle(XPath xpath, Document cdaDoc, Document resultDoc) throws XPathExpressionException;

    protected abstract void validatePatientName(XPath xpath, Document cdaDoc, Document resultDoc) throws XPathExpressionException;
}