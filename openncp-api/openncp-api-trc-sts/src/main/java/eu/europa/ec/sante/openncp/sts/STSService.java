package eu.europa.ec.sante.openncp.sts;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import eu.europa.ec.sante.openncp.core.common.security.exception.SMgrException;
import eu.europa.ec.sante.openncp.core.common.security.issuer.SamlTRCIssuer;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.audit.AuditServiceFactory;
import eu.europa.ec.sante.openncp.common.audit.EventActionCode;
import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.common.audit.EventOutcomeIndicator;
import eu.europa.ec.sante.openncp.common.audit.EventType;
import eu.europa.ec.sante.openncp.common.audit.TransactionName;
import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.common.configuration.util.http.IPUtil;
import eu.europa.ec.sante.openncp.common.util.HttpUtil;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.Validate;
import org.joda.time.DateTimeZone;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

@ServiceMode(value = Mode.MESSAGE)
@WebServiceProvider(targetNamespace = "https://ehdsi.eu/", serviceName = "SecurityTokenService", portName = "ISecurityTokenService_Port")
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class STSService extends SecurityTokenServiceWS implements Provider<SOAPMessage> {

    private final Logger logger = LoggerFactory.getLogger(STSService.class);

    @Autowired
    private SamlTRCIssuer samlTRCIssuer;

    @Override
    public SOAPMessage invoke(final SOAPMessage source) {

        log(source);

        final SOAPBody body;
        final SOAPHeader header;
        try {
            body = source.getSOAPBody();
            header = source.getSOAPHeader();
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
            // these calls are both getters and checkers of message.
            // So we call them first
            final String purposeOfUse = STSUtils.getPurposeOfUse(body);
            final String dispensationPinCode = STSUtils.getDispensationPinCode(body);
            final String prescriptionId = STSUtils.getPrescriptionId(body);
            final String patientID = getPatientID(body);
            final String messageId = getMessageIdFromHeader(header);

            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

            // The response TRC Assertion Issuer.
            final var hcpIdAssertion = getIdAssertionFromHeader(header);
            if (hcpIdAssertion != null) {
                logger.info("hcpIdAssertion: '{}'", hcpIdAssertion.getID());
                if (hcpIdAssertion.getIssueInstant() != null) {
                    logger.info("hcpIdAssertion Issue Instant: '{}'", hcpIdAssertion.getIssueInstant());
                }
            }
            final Assertion trc = samlTRCIssuer.issueTrcToken(hcpIdAssertion, patientID, purposeOfUse, dispensationPinCode, prescriptionId, null);
            if (hcpIdAssertion != null) {
                logger.info("HCP Assertion Date: '{}' TRC Assertion Date: '{}' -- '{}'", hcpIdAssertion.getIssueInstant().withZone(DateTimeZone.UTC),
                            trc.getIssueInstant().withZone(DateTimeZone.UTC), trc.getAuthnStatements().isEmpty());
            }

            final Document signedDoc = builder.newDocument();
            final var marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
            marshallerFactory.getMarshaller(trc).marshall(trc, signedDoc);

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
            sendTRCAuditMessage(samlTRCIssuer.getPointOfCare(), samlTRCIssuer.getHumanRequestorNameId(), samlTRCIssuer.getHumanRequestorSubjectId(),
                                samlTRCIssuer.getFunctionalRole(), patientID, samlTRCIssuer.getFacilityType(), trc.getID(), sslCommonName, messageId,
                                strReqHeader.getBytes(StandardCharsets.UTF_8), getMessageIdFromHeader(response.getSOAPHeader()),
                                strRespHeader.getBytes(StandardCharsets.UTF_8));

            log(response);
            return response;
        } catch (final SOAPException | WSTrustException | MarshallingException | SMgrException | ParserConfigurationException ex) {
            throw new WebServiceException(ex);
        }
    }

    private void sendTRCAuditMessage(final String pointOfCareID, final String humanRequestorNameID, final String humanRequestorSubjectID,
                                     final String humanRequestorRole, final String patientID, final String facilityType, final String assertionId,
                                     final String certificateCommonName, final String reqMid, final byte[] reqSecHeader, final String resMid,
                                     final byte[] resSecHeader) {

        final var auditService = AuditServiceFactory.getInstance();
        final var gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        XMLGregorianCalendar date2 = null;
        try {
            date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (final DatatypeConfigurationException ex) {
            logger.error("DatatypeConfigurationException: '{}'", ex.getMessage(), ex);
        }
        final String trcCommonName = HttpUtil.getTlsCertificateCommonName(
                ConfigurationManagerFactory.getConfigurationManager().getProperty("secman.sts.url"));
        final String sourceGateway = getClientIP();
        logger.info("STS Client IP: '{}'", sourceGateway);
        final var messageContext = context.getMessageContext();
        final HttpServletRequest servletRequest = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);
        final String serverName = servletRequest.getServerName();

        //TODO: Review Audit Trail specification - Identifying SC and SP as value of CN from TLS certificate.
        final EventLog eventLogTRCA = EventLog.createEventLogTRCA(TransactionName.TRC_ASSERTION, EventActionCode.EXECUTE, date2,
                                                                  EventOutcomeIndicator.FULL_SUCCESS, pointOfCareID, facilityType,
                                                                  humanRequestorNameID, humanRequestorRole, humanRequestorSubjectID,
                                                                  certificateCommonName, trcCommonName,
                                                                  ConfigurationManagerFactory.getConfigurationManager()
                                                                                             .getProperty("COUNTRY_PRINCIPAL_SUBDIVISION"), patientID,
                                                                  Constants.UUID_PREFIX + assertionId, reqMid, reqSecHeader, resMid, resSecHeader,
                                                                  IPUtil.isLocalLoopbackIp(sourceGateway) ? serverName : sourceGateway,
                                                                  STSUtils.getSTSServerIP(), NcpSide.NCP_B);

        eventLogTRCA.setEventType(EventType.TRC_ASSERTION);
        auditService.write(eventLogTRCA, "13", "2");
    }
}
