package eu.europa.ec.sante.ehdsi.openncp.assertionvalidator.saml;

import net.shibboleth.utilities.java.support.security.impl.SecureRandomIdentifierGenerationStrategy;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.saml2.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tr.com.srdc.epsos.util.PrettyPrinter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class SAML {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAML.class);

    private static final String CM_PREFIX = "urn:oasis:names:tc:SAML:2.0:cm:";
    private static SecureRandomIdentifierGenerationStrategy generator;

    /*
     *Any use of this class assures that OpenSAML is bootstrapped.
     *Also initializes an ID generator.
     */
    static {
        try {
            InitializationService.initialize();
            generator = new SecureRandomIdentifierGenerationStrategy();
        } catch (InitializationException e) {
            LOGGER.error("ConfigurationException: '{}'", e.getMessage(), e);
        }
    }

    private DocumentBuilder builder;
    private String issuerURL;


    /**
     * Initialize JAXP DocumentBuilder instance for later use and reuse.
     */
    public SAML() {
        this(null);
    }

    /**
     * Initialize JAXP DocumentBuilder instance for later use and reuse, and establishes an issuer URL.
     *
     * @param issuerURL This will be used in all generated assertions
     */
    public SAML(String issuerURL) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
            this.issuerURL = issuerURL;

        } catch (ParserConfigurationException e) {
            LOGGER.error("ParserConfigurationException: '{}'", e.getMessage(), e);
        }
    }

    /**
     * Helper method to add an XMLObject as a child of a DOM Element.
     */
    public static Element addToElement(XMLObject object, Element parent) throws MarshallingException {

        var marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(object);
        return marshaller.marshall(object, parent);
    }

    /**
     * Helper method to read an XML object from a DOM element.
     */
    public static XMLObject fromElement(Element element) throws UnmarshallingException {

        return XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element).unmarshall(element);
    }

    /**
     * <u>Slightly</u> easier way to create objects using OpenSAML's builder system.
     * Cast to SAMLObjectBuilder<T> is caller's choice
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> cls, QName qname) {

        var xmlObjectBuilderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        XMLObjectBuilder xmlObjectBuilder = xmlObjectBuilderFactory.getBuilder(qname);
        XMLObject xmlObject = xmlObjectBuilder.buildObject(qname);
        return (T) xmlObject;
    }

    /**
     * Helper method to get an XMLObject as a DOM Document.
     */
    public Document asDOMDocument(XMLObject object) throws MarshallingException {

        Document document = builder.newDocument();
        var marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(object);
        marshaller.marshall(object, document);

        return document;
    }

    /**
     * Helper method to pretty-print any XML object to a file.
     */
    public void printToFile(XMLObject object, String filename) throws IOException, MarshallingException, TransformerException {

        Document document = asDOMDocument(object);

        String result = PrettyPrinter.prettyPrint(document);
        if (filename != null) {
            var printWriter = new PrintWriter(new FileWriter(filename));
            printWriter.println(result);
            printWriter.close();
        } else {
            LOGGER.info("File not found! Printing to system out: '{}'", result);
        }
    }

    /**
     * Helper method to read an XML object from a file.
     */
    public XMLObject readFromFile(String filename) throws IOException, UnmarshallingException, SAXException {

        return fromElement(builder.parse(filename).getDocumentElement());
    }

    /**
     * Helper method to spawn a new Issuer element based on our issuer URL.
     */
    public Issuer spawnIssuer() {

        Issuer result = null;
        if (issuerURL != null) {
            result = create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
            result.setValue(issuerURL);
        }

        return result;
    }

    /**
     * Returns a SAML subject.
     *
     * @param username           The subject name
     * @param format             If non-null, we'll set as the subject name format
     * @param confirmationMethod If non-null, we'll create a SubjectConfirmation
     *                           element and use this as the Method attribute; must be "sender-vouches"
     *                           or "bearer", as HOK would require additional parameters and so is NYI
     */
    public Subject createSubject(String username, String format, String confirmationMethod) {

        NameID nameID = create(NameID.class, NameID.DEFAULT_ELEMENT_NAME);
        nameID.setValue(username);
        if (format != null)
            nameID.setFormat(format);

        Subject subject = create(Subject.class, Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID(nameID);

        if (confirmationMethod != null) {

            SubjectConfirmation confirmation = create(SubjectConfirmation.class, SubjectConfirmation.DEFAULT_ELEMENT_NAME);
            confirmation.setMethod(CM_PREFIX + confirmationMethod);
            subject.getSubjectConfirmations().add(confirmation);
        }

        return subject;
    }

    /**
     * Returns a SAML assertion with generated ID, current timestamp, given subject, and simple time-based conditions.
     *
     * @param subject Subject of the assertion
     */
    public Assertion createAssertion(Subject subject) {

        Assertion assertion = create(Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID(generator.generateIdentifier());

        var issueInstant = Instant.now();
        assertion.setIssueInstant(issueInstant);

        if (issuerURL != null) {
            assertion.setIssuer(spawnIssuer());
        }
        assertion.setSubject(subject);

        Conditions conditions = create(Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore(issueInstant);

        AudienceRestriction audienceRestriction = create(AudienceRestriction.class, AudienceRestriction.DEFAULT_ELEMENT_NAME);
        Audience audience = create(Audience.class, Audience.DEFAULT_ELEMENT_NAME);
        audience.setURI("urn:ehdsi:assertions.audience:x-border");
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);

        conditions.setNotOnOrAfter(issueInstant.plus(Duration.ofHours(4)));
        assertion.setConditions(conditions);

        return assertion;
    }

    /**
     * Helper method to generate a response, based on a pre-built assertion.
     */
    public Response createResponse(Assertion assertion) {

        return createResponse(assertion, null);
    }

    /**
     * Helper method to generate a shell response with a given status code
     * and query ID.
     */
    public Response createResponse(String statusCode, String inResponseTo) {

        return createResponse(statusCode, null, inResponseTo);
    }

    /**
     * Helper method to generate a shell response with a given status code, status message, and query ID.
     */
    public Response createResponse(String statusCode, String message, String inResponseTo) {

        Response response = create(Response.class, Response.DEFAULT_ELEMENT_NAME);
        response.setID(generator.generateIdentifier());

        if (inResponseTo != null) {
            response.setInResponseTo(inResponseTo);
        }

        var issueInstant = Instant.now();
        response.setIssueInstant(issueInstant);

        if (issuerURL != null) {
            response.setIssuer(spawnIssuer());
        }

        StatusCode statusCodeElement = create(StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
        statusCodeElement.setValue(statusCode);

        Status status = create(Status.class, Status.DEFAULT_ELEMENT_NAME);
        status.setStatusCode(statusCodeElement);
        response.setStatus(status);

        if (message != null) {

            StatusMessage statusMessage = create(StatusMessage.class, StatusMessage.DEFAULT_ELEMENT_NAME);
            statusMessage.setValue(message);
            status.setStatusMessage(statusMessage);
        }

        return response;
    }

    /**
     * Helper method to generate a response, based on a pre-built assertion and query ID.
     */
    public Response createResponse(Assertion assertion, String inResponseTo) {

        var response = createResponse(StatusCode.SUCCESS, inResponseTo);
        response.getAssertions().add(assertion);

        return response;
    }

    /**
     * Returns a SAML authentication assertion.
     *
     * @param subject  The subject of the assertion
     * @param authnCtx The "authentication context class reference",
     *                 e.g. AuthnContext.PPT_AUTHN_CTX
     */
    public Assertion createAuthnAssertion(Subject subject, String authnCtx) {

        var assertion = createAssertion(subject);
        AuthnContextClassRef authnContextClassRef = create(AuthnContextClassRef.class, AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setURI(authnCtx);

        // As of this writing, OpenSAML doesn't model the wide range of authentication context namespaces defined in SAML 2.0.
        // For a real project we'd probably move on to XSAny objects, setting QNames and values each-by-each a
        // JAXB mapping of the required schema DOM-building.
        // For classroom purposes the road ends here ...

        AuthnContext authnContext = create(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
        authnContext.setAuthnContextClassRef(authnContextClassRef);

        AuthnStatement authnStatement = create(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnContext(authnContext);

        assertion.getStatements().add(authnStatement);

        return assertion;
    }

    /**
     * Adds a SAML attribute to an attribute statement.
     *
     * @param statement Existing attribute statement
     * @param name      Attribute name
     * @param value     Attribute value
     */
    public void addAttribute(AttributeStatement statement, String name, String value) {

        // Build attribute values as XMLObjects, there is an AttributeValue interface, but it's apparently dead code
        final var xmlObjectBuilder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

        XSAny valueElement = (XSAny) xmlObjectBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
        valueElement.setTextContent(value);

        Attribute attribute = create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setName(name);
        attribute.getAttributeValues().add(valueElement);

        statement.getAttributes().add(attribute);
    }

    /**
     * Returns a SAML attribute assertion.
     *
     * @param subject    Subject of the assertion
     * @param attributes Attributes to be stated(may be null)
     */
    public Assertion createAttributeAssertion(Subject subject, Map<String, String> attributes) {

        var assertion = createAssertion(subject);

        AttributeStatement statement = create(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                addAttribute(statement, entry.getKey(), entry.getValue());
            }
        }
        assertion.getStatements().add(statement);

        return assertion;
    }
}
