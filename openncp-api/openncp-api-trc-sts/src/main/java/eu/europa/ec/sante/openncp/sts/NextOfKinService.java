package eu.europa.ec.sante.openncp.sts;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.audit.AuditServiceFactory;
import eu.europa.ec.sante.openncp.common.audit.EventActionCode;
import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.common.audit.EventOutcomeIndicator;
import eu.europa.ec.sante.openncp.common.audit.EventType;
import eu.europa.ec.sante.openncp.common.audit.TransactionName;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.common.configuration.util.http.IPUtil;
import eu.europa.ec.sante.openncp.common.util.DateUtil;
import eu.europa.ec.sante.openncp.common.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceProvider;
import jakarta.xml.ws.handler.MessageContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider(targetNamespace = "https://ehdsi.eu/", serviceName = "NextOfKinTokenService", portName = "NextOfKinTokenService_Port")
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class NextOfKinService extends SecurityTokenServiceWS implements Provider<SOAPMessage> {

    private final Logger logger = LoggerFactory.getLogger(NextOfKinService.class);

    @Override
    public SOAPMessage invoke(final SOAPMessage soapMessage) {

        logger.info("[NextOfKin WS] Invoke method");
        log(soapMessage);

        final SOAPBody body;
        final SOAPHeader header;
        try {
            body = soapMessage.getSOAPBody();
            header = soapMessage.getSOAPHeader();
        } catch (final SOAPException ex) {
            throw new WebServiceException("Cannot get Soap Message Parts", ex);
        }

        try {
            if (!SUPPORTED_ACTION_URI.equals(getRSTAction(body))) {
                throw new WebServiceException("Only ISSUE action is supported");
            }
            if (!SAML20_TOKEN_URN.equals(getRequestedToken(body))) {
                throw new WebServiceException("Only SAML2.0 Tokens are Issued");
            }
        } catch (final WSTrustException ex) {
            throw new WebServiceException(ex);
        }

        try {
            final var nextOfKinDetail = STSUtils.getNextOfKinDetails(body);
            final String messageId = getMessageIdFromHeader(header);

            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

            // The response TRC Assertion Issuer.
            final var samlNextOfKinIssuer = new SamlNextOfKinIssuer();
            final var hcpIdAssertion = getIdAssertionFromHeader(header);
            if (hcpIdAssertion != null) {
                logger.info("hcpIdAssertion: '{}'", hcpIdAssertion.getID());
                if (hcpIdAssertion.getIssueInstant() != null) {
                    logger.info("hcpIdAssertion Issue Instant: '{}'", hcpIdAssertion.getIssueInstant());
                }
            }
            final List<Attribute> attributeList = buildNextOfKinAttributes(nextOfKinDetail);
            final Assertion nextOfKinAssertion = samlNextOfKinIssuer.issueNextOfKinToken(hcpIdAssertion, "doctorId", hcpIdAssertion.getID(), attributeList);
            logger.info("HCP Assertion Date: '{}' TRC Assertion Date: '{}' -- '{}'", hcpIdAssertion.getIssueInstant().withZone(DateTimeZone.UTC),
                        nextOfKinAssertion.getIssueInstant().withZone(DateTimeZone.UTC), nextOfKinAssertion.getAuthnStatements().isEmpty());
            final Document signedDoc = builder.newDocument();
            final var marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
            marshallerFactory.getMarshaller(nextOfKinAssertion).marshall(nextOfKinAssertion, signedDoc);

            final SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
            response.getSOAPBody().addDocument(STSUtils.createRSTRC(signedDoc));
            createResponseHeader(response.getSOAPHeader(), messageId);

            final var strRespHeader = STSUtils.domElementToString(response.getSOAPHeader());
            final var strReqHeader = STSUtils.domElementToString(header);

            String sslCommonName;
            if (context.getUserPrincipal() != null) {

                sslCommonName = context.getUserPrincipal().getName();
                logger.info("WebServiceContext JAX-WS User: '{}'", sslCommonName);
            } else {
                logger.info("WebServiceContext JAX-WS - No User authenticated");
            }

            sslCommonName = HttpUtil.getSubjectDN(false);
            sendNOKAuditMessage(samlNextOfKinIssuer.getPointOfCare(), samlNextOfKinIssuer.getHumanRequestorNameId(),
                                samlNextOfKinIssuer.getHumanRequestorSubjectId(), samlNextOfKinIssuer.getFunctionalRole(),
                                nextOfKinDetail.getLivingSubjectIds().get(0), samlNextOfKinIssuer.getFacilityType(), nextOfKinAssertion.getID(),
                                sslCommonName, messageId, strReqHeader.getBytes(StandardCharsets.UTF_8),
                                getMessageIdFromHeader(response.getSOAPHeader()), strRespHeader.getBytes(StandardCharsets.UTF_8));

            log(response);
            return response;
        } catch (final ParserConfigurationException | WSTrustException | SMgrException | SOAPException | MarshallingException | ParseException e) {
            throw new WebServiceException(e);
        }
    }

    private void sendNOKAuditMessage(final String pointOfCareID, final String humanRequestorNameID, final String humanRequestorSubjectID, final String humanRequestorRole,
                                     final String nokID, final String facilityType, final String assertionId, final String certificateCommonName, final String reqMid,
                                     final byte[] reqSecHeader, final String resMid, final byte[] resSecHeader) {

        final var auditService = AuditServiceFactory.getInstance();
        final XMLGregorianCalendar date = DateUtil.getDateAsXMLGregorian(new Date());
        final String trcCommonName = HttpUtil.getTlsCertificateCommonName(
                ConfigurationManagerFactory.getConfigurationManager().getProperty("secman.nextOfKin.url"));
        final String sourceGateway = getClientIP();
        logger.info("STS Client IP: '{}'", sourceGateway);
        final var messageContext = context.getMessageContext();
        final HttpServletRequest servletRequest = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);
        final String serverName = servletRequest.getServerName();

        //TODO: Review Audit Trail specification - Identifying SC and SP as value of CN from TLS certificate.
        final EventLog eventLogNOKA = EventLog.createEventLogNOKA(TransactionName.NOK_ASSERTION, EventActionCode.EXECUTE, date,
                                                                  EventOutcomeIndicator.FULL_SUCCESS, pointOfCareID, facilityType, humanRequestorNameID,
                                                                  humanRequestorRole, humanRequestorSubjectID, certificateCommonName, trcCommonName,
                                                                  ConfigurationManagerFactory.getConfigurationManager()
                                                                                       .getProperty("COUNTRY_PRINCIPAL_SUBDIVISION"), nokID,
                                                            Constants.UUID_PREFIX + assertionId, reqMid, reqSecHeader, resMid, resSecHeader,
                                                            IPUtil.isLocalLoopbackIp(sourceGateway) ? serverName : sourceGateway,
                                                                  STSUtils.getSTSServerIP(), NcpSide.NCP_B);

        eventLogNOKA.setEventType(EventType.NOK_ASSERTION);
        auditService.write(eventLogNOKA, "13", "2");
    }

    private List<Attribute> buildNextOfKinAttributes(final NextOfKinDetail nextOfKinDetail) {

        final List<Attribute> attributeList = new ArrayList<>();
        if (nextOfKinDetail.getLivingSubjectIds() != null && StringUtils.isNotBlank(nextOfKinDetail.getLivingSubjectIds().get(0))) {
            attributeList.add(SamlIssuerHelper.createAttribute(nextOfKinDetail.getLivingSubjectIds().get(0), "NextOfKinId", Attribute.URI_REFERENCE,
                                                               "urn:ehdsi:names:subject:nextofkin:id"));
        }
        if (StringUtils.isNotBlank(nextOfKinDetail.getFirstName())) {
            attributeList.add(SamlIssuerHelper.createAttribute(nextOfKinDetail.getFirstName(), "NextOfKinFirstName", Attribute.URI_REFERENCE,
                                                               "urn:ehdsi:names:subject:nextofkin:firstname"));
        }
        if (StringUtils.isNotBlank(nextOfKinDetail.getFamilyName())) {
            attributeList.add(SamlIssuerHelper.createAttribute(nextOfKinDetail.getFamilyName(), "NextOfKinFamilyName", Attribute.URI_REFERENCE,
                                                               "urn:ehdsi:names:subject:nextofkin:familyname"));
        }
        if (StringUtils.isNotBlank(nextOfKinDetail.getGender())) {
            attributeList.add(SamlIssuerHelper.createAttribute(nextOfKinDetail.getGender(), "NextOfKinGender", Attribute.URI_REFERENCE,
                                                               "urn:ehdsi:names:subject:nextofkin:gender"));
        }
        if (nextOfKinDetail.getBirthDate() != null) {
            attributeList.add(
                    SamlIssuerHelper.createAttribute(nextOfKinDetail.getBirthDate().toString(), "NextOfKinBirthDate", Attribute.URI_REFERENCE,
                                                     "urn:ehdsi:names:subject:nextofkin:birthdate"));
        }
        if (StringUtils.isNotBlank(nextOfKinDetail.getAddressStreet())) {
            attributeList.add(SamlIssuerHelper.createAttribute(nextOfKinDetail.getAddressStreet(), "NextOfKinAddressStreet", Attribute.URI_REFERENCE,
                                                               "urn:ehdsi:names:subject:nextofkin:address:street"));
        }
        if (StringUtils.isNotBlank(nextOfKinDetail.getAddressCity())) {
            attributeList.add(SamlIssuerHelper.createAttribute(nextOfKinDetail.getAddressCity(), "NextOfKinAddressCity", Attribute.URI_REFERENCE,
                                                               "urn:ehdsi:names:subject:nextofkin:address:city"));
        }
        if (StringUtils.isNotBlank(nextOfKinDetail.getAddressPostalCode())) {
            attributeList.add(SamlIssuerHelper.createAttribute(nextOfKinDetail.getAddressPostalCode(), "NextOfKinPostalCode", Attribute.URI_REFERENCE,
                                                               "urn:ehdsi:names:subject:nextofkin:address:postalcode"));
        }
        if (StringUtils.isNotBlank(nextOfKinDetail.getAddressCountry())) {
            attributeList.add(
                    SamlIssuerHelper.createAttribute(nextOfKinDetail.getAddressCountry(), "NextOfKinAddressCountry", Attribute.URI_REFERENCE,
                                                     "urn:ehdsi:names:subject:nextofkin:address:country"));
        }
        return attributeList;
    }
}
