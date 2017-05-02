/*
 *  HCPIAssertionCreator.java
 *  Created on 09/09/2014, 9:34:46 AM
 *  
 *  TRILLIUM.SECURITY.UTILS
 *  eu.europa.ec.joinup.ecc.trilliumsecurityutils.saml
 *  
 *  Copyright (C) 2014 iUZ Technologies, Lda - http://www.iuz.pt and Gnomon Informatics - http://www.gnomon.com.gr 
 */
package eu.europa.ec.joinup.ecc.trilliumsecurityutils.saml;

import eu.epsos.assertionvalidator.XSPARole;
import eu.epsos.exceptions.InvalidInput;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.schema.XSAny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tr.com.srdc.epsos.util.Constants;
import tr.com.srdc.epsos.util.saml.SAML;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for SAML related functionality, such as the generation and
 * conversion of SAML Tokens
 *
 * @author Marcelo Fonseca <marcelo.fonseca@iuz.pt>
 */
public class HCPIAssertionCreator {

    /**
     * Class logger
     */
    protected static final Logger LOG = LoggerFactory.getLogger(HCPIAssertionCreator.class);

    private HCPIAssertionCreator() {
    }

    /**
     * Produces an HCP SAML Assertion, based on a specific role, a Patient Id and
     * a HomeCommunityId
     *
     * @param role            the HCP role
     * @param patientId       the Patient identifier in ISO format
     * @param homeCommunityId the Home Community ID
     * @return
     */
    public static Assertion createHCPIAssertion(final XSPARole role, final String patientId, final String homeCommunityId) {
        return createHCPIAssertion(new ArrayList<String>() {
            {
                add("4");
                add("6");
                add("10");
                add("46");
            }
        }, role, patientId, homeCommunityId);
    }

    /**
     * Produces an HCP SAML Assertion, based on a set of permissions, a specific role, a patient id and a Home Community Id,
     *
     * @param permissions
     * @param role
     * @param patientId
     * @param homeCommunityId
     * @return
     */
    public static Assertion createHCPIAssertion(final List<String> permissions, final XSPARole role, final String patientId, final String homeCommunityId) {
        if (permissions == null) {
            throw new InvalidInput("permissions == null");
        }
        XMLSignatureFactory factory;
        KeyStore keyStore;
        KeyPair keyPair;
        KeyInfo keyInfo;

        SAML saml = new SAML();

        // Subject
        org.opensaml.saml2.core.Subject subject = saml.createSubject("UID=medical doctor", "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName", "sender-vouches");

        // Create assertion
        Assertion assertion = saml.createAssertion(subject);

        Issuer issuer = saml.create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue("O=European HCP,L=Europe,ST=Europe,C=EU");
        issuer.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");
        assertion.setIssuer(issuer);

        // Set version
        SAMLVersion version = SAMLVersion.VERSION_20;
        assertion.setVersion(version);

        // Set Conditions/AudienceRestriction
        AudienceRestrictionBuilder arb = new AudienceRestrictionBuilder();
        AudienceRestriction ar = arb.buildObject();
        AudienceBuilder ab = new AudienceBuilder();
        Audience a = ab.buildObject();
        a.setAudienceURI("http://ihe.connecthaton.XUA/X-ServiceProvider-IHE-Connectathon");
        ar.getAudiences().add(a);
        assertion.getConditions().getAudienceRestrictions().add(ar);

        // Set AuthnStatement
        DateTime dateTime = new DateTime();
        AuthnStatement authnStatement = saml.create(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(dateTime);
        authnStatement.setSessionNotOnOrAfter(dateTime.plusHours(2));

        // Set AuthnStatement/AuthnContext
        AuthnContext authnContext = saml.create(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef authnContextClassRef = saml.create(AuthnContextClassRef.class, AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:X509");
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);

        // Create AttributeStatement
        AttributeStatement attributeStatement = saml.create(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);

        // Namespaces
        Namespace ns1 = new Namespace();
        ns1.setNamespacePrefix("xs");
        ns1.setNamespaceURI("http://www.w3.org/2001/XMLSchema");

        Namespace ns2 = new Namespace();
        ns2.setNamespacePrefix("xsi");
        ns2.setNamespaceURI("http://www.w3.org/2001/XMLSchema-instance");

        // Set HCP Identifier
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Subject");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:subject-id");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("medical doctor" + " the");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Structural Role of the HCP
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Role");
            att.setName("urn:oasis:names:tc:xacml:2.0:subject:role");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("medical doctor");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Speciality of the HCP
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("HITSP Clinical Speciality");
            att.setName("urn:epsos:names:wp3.4:subject:clinical-speciality");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("UNKNOWN");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Healthcare Professional Organisation
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Organization");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:organization");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("MemberState");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Healthcare Professional Organisation ID
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Organization Id");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:organization-id");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("urn:oid:1.2.3.4.5.6.7");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Type of HCPO
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("epSOS Healthcare Facility Type");
            att.setName("urn:epsos:names:wp3.4:subject:healthcare-facility-type");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("Hospital");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Purpose of Use
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Purpose of Use");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("TREATMENT");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Point of Care
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA Locality");
            att.setName("urn:oasis:names:tc:xspa:1.0:environment:locality");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("Aveiro, Portugal");

            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");

            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set Permissions acc. to the legislation of the country of care
        if (true) {
            Attribute att = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            att.setFriendlyName("XSPA permissions according with Hl7");
            att.setName("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission");
            att.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

            for (String s : permissions) {
                XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

                XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);

                attVal.setTextContent("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:" + s);

                attVal.getNamespaceManager().registerNamespace(ns1);
                attVal.getNamespaceManager().registerNamespace(ns2);
                QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
                attVal.getUnknownAttributes().put(attributeName, "xs:string");

                att.getAttributeValues().add(attVal);
                attributeStatement.getAttributes().add(att);
            }

            // Resource Id: Patient ID (required by eHealth Exchange)
            Attribute resourceId = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            resourceId.setFriendlyName("Resource Id");
            resourceId.setName("urn:oasis:names:tc:xacml:2.0:resource:resource-id");

            XMLObjectBuilder<?> resourceIdBuilder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny resourceIdVal = (XSAny) resourceIdBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            resourceIdVal.setTextContent(patientId);

            resourceIdVal.getNamespaceManager().registerNamespace(ns1);
            resourceIdVal.getNamespaceManager().registerNamespace(ns2);
            QName resourceIdributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            resourceIdVal.getUnknownAttributes().put(resourceIdributeName, "xs:string");

            resourceId.getAttributeValues().add(resourceIdVal);
            attributeStatement.getAttributes().add(resourceId);

            // Resource Id: Home Community ID (required by eHealth Exchange)
            Attribute homeCommunityIdAttr = saml.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
            homeCommunityIdAttr.setFriendlyName("Home Community Id");
            homeCommunityIdAttr.setName("urn:nhin:names:saml:homeCommunityId");

            XMLObjectBuilder<?> homeCommunityIdBuilder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);

            XSAny homeCommunityIdVal = (XSAny) homeCommunityIdBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            homeCommunityIdVal.setTextContent(homeCommunityId);

            homeCommunityIdVal.getNamespaceManager().registerNamespace(ns1);
            homeCommunityIdVal.getNamespaceManager().registerNamespace(ns2);
            QName homeCommunityIdributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            homeCommunityIdVal.getUnknownAttributes().put(homeCommunityIdributeName, "xs:string");

            homeCommunityIdAttr.getAttributeValues().add(homeCommunityIdVal);
            attributeStatement.getAttributes().add(homeCommunityIdAttr);

            //add PPD-046 (Record Medication Administration Record) needed by security manager for XDR ED submit
            XMLObjectBuilder<?> builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);
            XSAny attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PPD-046");
            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            QName attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");
            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);

            //add PPD-032 (New Consents and Authorizations) needed for Consent submit.
            builder = Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);
            attVal = (XSAny) builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
            attVal.setTextContent("urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PPD-032");
            attVal.getNamespaceManager().registerNamespace(ns1);
            attVal.getNamespaceManager().registerNamespace(ns2);
            attributeName = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
            attVal.getUnknownAttributes().put(attributeName, "xs:string");
            att.getAttributeValues().add(attVal);
            attributeStatement.getAttributes().add(att);
        }

        // Set AttributeStatement
        assertion.getAttributeStatements().add(attributeStatement);

        // Set Signature
        try {
            // Set assertion.DOM
            Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(assertion);
            Element elem;
            elem = marshaller.marshall(assertion);
            assertion.setDOM(elem);

            // Set factory
            String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
            factory = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(providerName).newInstance());

            // Set keyStore
            FileInputStream is = new FileInputStream(Constants.SC_KEYSTORE_PATH.split(Constants.EPSOS_PROPS_PATH)[0]);
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, Constants.SC_KEYSTORE_PASSWORD.toCharArray());
            is.close();

            PasswordProtection pp = new PasswordProtection(Constants.SC_PRIVATEKEY_PASSWORD.toCharArray());
            PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(Constants.SC_PRIVATEKEY_ALIAS, pp);

            // Set keyPair
            keyPair = new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());

            // Set keyInfo
            KeyInfoFactory kFactory = factory.getKeyInfoFactory();
            keyInfo = kFactory.newKeyInfo(Collections.singletonList(kFactory.newX509Data(Collections.singletonList(entry.getCertificate()))));

            // Create Signature/SignedInfo/Reference
            List<Transform> lst = new ArrayList<Transform>();
            lst.add(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            lst.add(factory.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null));
            Reference ref = factory.newReference("#" + assertion.getID(), factory.newDigestMethod(DigestMethod.SHA1, null), lst, null, null);

            // Set Signature/SignedInfo
            SignedInfo signedInfo = factory.newSignedInfo(factory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
                    factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

            // Sign Assertion
            XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
            DOMSignContext signContext = new DOMSignContext(keyPair.getPrivate(), assertion.getDOM());
            signature.sign(signContext);
        } catch (Exception e) {
            LOG.error("Signature element not created!", e.getLocalizedMessage());
        }

        // Set Signature's place
        org.w3c.dom.Node signatureElement = assertion.getDOM().getLastChild();

        boolean foundIssuer = false;
        org.w3c.dom.Node elementAfterIssuer = null;
        NodeList children = assertion.getDOM().getChildNodes();
        for (int c = 0; c < children.getLength(); ++c) {
            org.w3c.dom.Node child = children.item(c);

            if (foundIssuer) {
                elementAfterIssuer = child;
                break;
            }

            if (child.getNodeType() == Node.ELEMENT_NODE && child.getLocalName().equals("Issuer")) {
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