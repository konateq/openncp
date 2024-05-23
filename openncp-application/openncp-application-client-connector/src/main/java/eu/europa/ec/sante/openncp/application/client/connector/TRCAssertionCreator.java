package eu.europa.ec.sante.openncp.application.client.connector;

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;

import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.common.validation.util.security.CryptographicConstant;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.AssertionConstants;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.PurposeOfUse;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.saml.SAML;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AssertionIDRef;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Issuer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TRCAssertionCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TRCAssertionCreator.class);

    public Assertion createTRCAssertion() {

        final XMLSignatureFactory factory;
        final KeyStore keyStore;
        final KeyPair keyPair;
        final KeyInfo keyInfo;

        final var saml = new SAML();
        final var subject = saml.createSubject("physician the", "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified", "sender-vouches");

        // Create assertion
        final var assertion = saml.createAssertion(subject);
        final String countryCode = ConfigurationManagerFactory.getConfigurationManager().getProperty("COUNTRY_CODE");
        final var issuer = saml.create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue("urn:initgw:" + countryCode + ":countryB");
        issuer.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
        issuer.setNameQualifier("urn:ehdsi:assertions:trc");
        assertion.setIssuer(issuer);

        // Set version
        final SAMLVersion version = SAMLVersion.VERSION_20;
        assertion.setVersion(version);

        // Set AuthnStatement
        final var issueInstant = DateTime.now();
        final var authnStatement = saml.create(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(issueInstant);
        authnStatement.setSessionNotOnOrAfter(issueInstant.plus(Duration.standardHours(2)));

        // Set AuthnStatement/AuthnContext
        final var authnContext = saml.create(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
        final var authnContextClassRef = saml.create(AuthnContextClassRef.class, AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession");
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);

        // Set Advice
        final var advice = saml.create(Advice.class, Advice.DEFAULT_ELEMENT_NAME);
        final var assertionIDRef = saml.create(AssertionIDRef.class, AssertionIDRef.DEFAULT_ELEMENT_NAME);
        assertionIDRef.setAssertionID(assertion.getID());
        advice.getAssertionIDReferences().add(assertionIDRef);
        assertion.setAdvice(advice);

        // Create AttributeStatement
        final var attributeStatement = saml.create(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);

        // Namespaces
        final var ns1 = new Namespace(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs");
        final var ns2 = new Namespace(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");

        // Set patient Identifier
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("Patient ID");
            att.setName(AssertionConstants.URN_OASIS_NAMES_TC_XSPA_1_0_SUBJECT_SUBJECT_ID);
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);
            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("6212122451^^^&2.16.17.710.815.1000.990.1&ISO");

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Purpose of Use
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Purpose of Use");
            att.setName(AssertionConstants.URN_OASIS_NAMES_TC_XSPA_1_0_SUBJECT_PURPOSEOFUSE);
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent(PurposeOfUse.TREATMENT.toString());

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set AttributeStatement
        assertion.getAttributeStatements().add(attributeStatement);

        // Set Signature
        try {
            // Set assertion.DOM
            final var marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion);
            final Element element = marshaller.marshall(assertion);
            assertion.setDOM(element);

            // Set factory
            final String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
            factory = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(providerName).getDeclaredConstructor().newInstance());

            // Set keyStore
            try (final var is = new FileInputStream(Constants.SC_KEYSTORE_PATH)) {
                keyStore = KeyStore.getInstance("JKS");
                keyStore.load(is, Constants.SC_KEYSTORE_PASSWORD.toCharArray());
            }

            final var passwordProtection = new PasswordProtection(Constants.SC_PRIVATEKEY_PASSWORD.toCharArray());
            final PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(Constants.SC_PRIVATEKEY_ALIAS, passwordProtection);

            // Set keyPair
            keyPair = new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());

            // Set keyInfo
            final var keyInfoFactory = factory.getKeyInfoFactory();
            keyInfo = keyInfoFactory.newKeyInfo(
                    Collections.singletonList(keyInfoFactory.newX509Data(Collections.singletonList(entry.getCertificate()))));

            // Create Signature/SignedInfo/Reference
            final List<Transform> lst = new ArrayList<>();
            lst.add(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            lst.add(factory.newTransform(CryptographicConstant.ALGO_ID_C14N_EXCL_OMIT_COMMENTS, (TransformParameterSpec) null));
            final var reference = factory.newReference("#" + assertion.getID(),
                                                       factory.newDigestMethod(CryptographicConstant.ALGO_ID_DIGEST_SHA256, null), lst, null, null);

            // Set Signature/SignedInfo
            final var signedInfo = factory.newSignedInfo(
                    factory.newCanonicalizationMethod(CryptographicConstant.ALGO_ID_C14N_EXCL_WITH_COMMENTS, (C14NMethodParameterSpec) null),
                    factory.newSignatureMethod(CryptographicConstant.ALGO_ID_SIGNATURE_RSA_SHA256, null), Collections.singletonList(reference));

            // Sign Assertion
            final var xmlSignature = factory.newXMLSignature(signedInfo, keyInfo);
            final var domSignContext = new DOMSignContext(keyPair.getPrivate(), assertion.getDOM());
            xmlSignature.sign(domSignContext);
        } catch (final Exception e) {
            LOGGER.error("Exception: '{}'", e.getMessage(), e);
        }

        // Set Signature's place
        final Node signatureElement = assertion.getDOM().getLastChild();

        var foundIssuer = false;
        Node elementAfterIssuer = null;
        final NodeList children = assertion.getDOM().getChildNodes();
        for (var c = 0; c < children.getLength(); ++c) {
            final Node child = children.item(c);

            if (foundIssuer) {
                elementAfterIssuer = child;
                break;
            }

            if (child.getNodeType() == Node.ELEMENT_NODE && StringUtils.equals(child.getLocalName(), "Issuer")) {
                foundIssuer = true;
            }
        }

        // Place after the Issuer, or as first element if no Issuer:
        if (!foundIssuer || elementAfterIssuer != null) {
            assertion.getDOM().removeChild(signatureElement);
            assertion.getDOM().insertBefore(signatureElement, foundIssuer ? elementAfterIssuer : assertion.getDOM().getFirstChild());
        }

        return assertion;
    }
}
