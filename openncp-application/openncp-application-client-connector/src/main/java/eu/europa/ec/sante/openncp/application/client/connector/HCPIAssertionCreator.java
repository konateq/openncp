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
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;

import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.common.validation.util.security.CryptographicConstant;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.PurposeOfUse;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.saml.SAML;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml.saml2.core.impl.AudienceRestrictionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HCPIAssertionCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HCPIAssertionCreator.class);

    public Assertion createHCPIAssertion() {

        final XMLSignatureFactory factory;
        final KeyStore keyStore;
        final KeyPair keyPair;
        final KeyInfo keyInfo;

        final SAML saml = new SAML();

        final Subject subject = saml.createSubject("physician", "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified", "sender-vouches");

        // Create assertion
        final Assertion assertion = saml.createAssertion(subject);

        final Issuer issuer = saml.create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue("urn:tiani-spirit:sts");
        assertion.setIssuer(issuer);

        // Set version
        final SAMLVersion version = SAMLVersion.VERSION_20;
        assertion.setVersion(version);

        // Set Conditions/AudienceRestriction
        final AudienceRestrictionBuilder arb = new AudienceRestrictionBuilder();
        final AudienceRestriction ar = arb.buildObject();
        final AudienceBuilder ab = new AudienceBuilder();
        final Audience a = ab.buildObject();
        a.setAudienceURI("http://ihe.connecthaton.XUA/X-ServiceProvider-IHE-Connectathon");
        ar.getAudiences().add(a);
        assertion.getConditions().getAudienceRestrictions().add(ar);

        // Set AuthnStatement
        //DateTime dateTime = new DateTime().toDateTime(DateTimeZone.UTC);
        final DateTime issueInstant = DateTime.now();
        final AuthnStatement authnStatement = saml.create(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(issueInstant);
        authnStatement.setSessionNotOnOrAfter(issueInstant.plus(Duration.standardHours(2)));

        // Set AuthnStatement/AuthnContext
        final AuthnContext authnContext = saml.create(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
        final AuthnContextClassRef authnContextClassRef = saml.create(AuthnContextClassRef.class, AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);

        // Create AttributeStatement
        final var attributeStatement = saml.create(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);

        // Namespaces
        final var ns1 = new Namespace(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs");
        final var ns2 = new Namespace(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");

        // Set HC Identifier
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("HCI Identifier");
            att.setName("urn:ihe:iti:xca:2010:homeCommunityId");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent(Constants.HOME_COMM_ID);

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set NP Identifier
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("NPI Identifier");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:npi");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent(Constants.COUNTRY_NAME);

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set HCP Identifier
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Subject");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:subject-id");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("physician the");

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Structural Role of the HCP
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Role");
            att.setName("urn:oasis:names:tc:xacml:2.0:subject:role");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);
            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("physician");

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Speciality of the HCP
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("HITSP Clinical Speciality");
            att.setName("urn:epsos:names:wp3.4:subject:clinical-speciality");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("UNKNOWN");

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Healthcare Professional Organisation
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Organization");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:organization");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("MemberState");

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Healthcare Professional Organisation ID
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Organization Id");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:organization-id");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("urn:oid:1.2.3.4.5.6.7");

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Type of HCPO
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("eHealth DSI Healthcare Facility Type");
            att.setName("urn:ehdsi:names:subject:healthcare-facility-type");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("Resident Physician");
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
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse");
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

        // Set Point of Care
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Locality");
            att.setName("urn:oasis:names:tc:xspa:1.0:environment:locality");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("Vienna, Austria");

            attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
            attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
            final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Permissions acc. to the legislation of the country of care
        if (true) {
            final Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA permissions according with Hl7");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            final String[] permissions = { "POE-006", "PRD-003", "PRD-004", "PRD-005", "PRD-006", "PRD-010", "PRD-016", "PPD-032", "PPD-033",
                                           "PPD-046", };

            for (final String permission : permissions) {

                final XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);
                final XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);

                attVal.setTextContent("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:" + permission);
                attVal.getNamespaceManager().registerNamespaceDeclaration(ns1);
                attVal.getNamespaceManager().registerNamespaceDeclaration(ns2);
                final QName attributeName = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");
                attVal.getUnknownAttributes().put(attributeName, "xs:string");

                att.getAttributeValues().add(attVal);
                attributeStatement.getAttributes().add(att);
            }
        }

        // Set AttributeStatement
        assertion.getAttributeStatements().add(attributeStatement);

        // Set Signature
        try {
            // Set assertion.DOM
            final Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion);
            final Element elem;
            elem = marshaller.marshall(assertion);
            assertion.setDOM(elem);

            // Set factory
            final String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
            factory = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(providerName).newInstance());

            // Set keyStore
            try (final var is = new FileInputStream(Constants.SC_KEYSTORE_PATH)) {
                keyStore = KeyStore.getInstance("JKS");
                keyStore.load(is, Constants.SC_KEYSTORE_PASSWORD.toCharArray());
            }
            final PasswordProtection pp = new PasswordProtection(Constants.SC_PRIVATEKEY_PASSWORD.toCharArray());
            final PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(Constants.SC_PRIVATEKEY_ALIAS, pp);

            // Set keyPair
            keyPair = new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());

            // Set keyInfo
            final KeyInfoFactory kFactory = factory.getKeyInfoFactory();
            keyInfo = kFactory.newKeyInfo(Collections.singletonList(kFactory.newX509Data(Collections.singletonList(entry.getCertificate()))));

            // Create Signature/SignedInfo/Reference
            final List<Transform> lst = new ArrayList<>();
            lst.add(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            lst.add(factory.newTransform(CryptographicConstant.ALGO_ID_C14N_EXCL_OMIT_COMMENTS, (TransformParameterSpec) null));
            final Reference ref = factory.newReference("#" + assertion.getID(),
                                                       factory.newDigestMethod(CryptographicConstant.ALGO_ID_DIGEST_SHA256, null), lst, null, null);

            // Set Signature/SignedInfo
            final SignedInfo signedInfo = factory.newSignedInfo(
                    factory.newCanonicalizationMethod(CryptographicConstant.ALGO_ID_C14N_EXCL_OMIT_COMMENTS, (C14NMethodParameterSpec) null),
                    factory.newSignatureMethod(CryptographicConstant.ALGO_ID_SIGNATURE_RSA_SHA256, null), Collections.singletonList(ref));

            // Sign Assertion
            final XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
            final DOMSignContext signContext = new DOMSignContext(keyPair.getPrivate(), assertion.getDOM());
            signature.sign(signContext);
        } catch (final Exception e) {
            LOGGER.error("Exception: '{}'", e.getMessage(), e);
        }

        // Set Signature's place
        final Node signatureElement = assertion.getDOM().getLastChild();

        boolean foundIssuer = false;
        Node elementAfterIssuer = null;
        final NodeList children = assertion.getDOM().getChildNodes();
        for (int c = 0; c < children.getLength(); ++c) {
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
