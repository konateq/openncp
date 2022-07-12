package eu.epsos.pt.eadc;

import com.spirit.epsos.cc.adc.EadcEntry;
import ee.affecto.epsos.util.EventLogClientUtil;
import eu.epsos.pt.eadc.datamodel.ObjectFactory;
import eu.epsos.pt.eadc.datamodel.TransactionInfo;
import eu.epsos.pt.eadc.util.EadcUtil;
import eu.epsos.pt.eadc.util.EadcUtil.Direction;
import eu.europa.ec.sante.ehdsi.eadc.ServiceType;
import eu.europa.ec.sante.ehdsi.openncp.assertionvalidator.Helper;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tr.com.srdc.epsos.util.Constants;
import tr.com.srdc.epsos.util.OidUtil;
import tr.com.srdc.epsos.util.XMLUtil;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;

/**
 * This class wraps the EADC invocation. As it gathers several aspects required to its proper usage, such as
 * the compilation and preparation of transaction details.
 *
 * @author Marcelo Fonseca<code> - marcelo.fonseca@iuz.pt</code>
 */
public class EadcUtilWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EadcUtilWrapper.class);

    private EadcUtilWrapper() {
    }

    /**
     * Main EADC Wrapper operation. It receives as input all the required information to successfully fill a transaction object.
     *
     * @param requestMsgCtx  the request Servlet Message Context
     * @param responseMsgCtx the response Servlet Message Context
     * @param serviceClient  the Axis2 Service Client
     * @param cda            the (optional) CDA document*
     * @param startTime      the transaction start time
     * @param endTime        the transaction end time
     * @param receivingIso   the country A ISO Code
     * @param dsType         the JDBC Datasource corresponding to the IHE operation
     * @param direction      the Operation type: INBOUND or OUTBOUND
     * @param serviceType    the Service Type representing the action executed to prevent processing of personal data
     */
    public static void invokeEadc(MessageContext requestMsgCtx, MessageContext responseMsgCtx, ServiceClient serviceClient,
                                  Document cda, Date startTime, Date endTime, String receivingIso, EadcEntry.DsTypes dsType,
                                  Direction direction, ServiceType serviceType) {

        new Thread(() -> {
            var watch = new StopWatch();
            watch.start();
            try {
                EadcUtil.invokeEadc(requestMsgCtx, responseMsgCtx, cda, buildTransactionInfo(requestMsgCtx, responseMsgCtx,
                        serviceClient, direction, startTime, endTime, receivingIso, serviceType), dsType);
            } catch (Exception e) {
                LOGGER.error("[EADC] Invocation Failed - Exception: '{}'", e.getMessage(), e);
            } finally {
                watch.stop();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("EADC invocation executed in: '{}ms'", watch.getTime());
                }
            }
        }).start();
    }

    /**
     * Main EADC Wrapper operation. It receives as input all the required information to successfully fill a transaction object.
     *
     * @param requestMsgCtx  the request Servlet Message Context
     * @param responseMsgCtx the response Servlet Message Context
     * @param serviceClient  the Axis2 Service Client
     * @param cda            the (optional) CDA document*
     * @param startTime      the transaction start time
     * @param endTime        the transaction end time
     * @param receivingIso   the country A ISO Code
     * @param dsType         the JDBC Datasource corresponding to the IHE operation
     * @param direction      the Operation type: INBOUND or OUTBOUND
     * @param serviceType    the Service Type representing the action executed to prevent processing of personal data
     */
    public static void invokeEadcFailure(MessageContext requestMsgCtx, MessageContext responseMsgCtx, ServiceClient serviceClient,
                                  Document cda, Date startTime, Date endTime, String receivingIso, EadcEntry.DsTypes dsType,
                                  Direction direction, ServiceType serviceType, String errorDescription) {

        new Thread(() -> {
            var watch = new StopWatch();
            watch.start();
            try {
                EadcUtil.invokeEadcFailure(requestMsgCtx, responseMsgCtx, cda, buildTransactionInfo(requestMsgCtx, responseMsgCtx,
                        serviceClient, direction, startTime, endTime, receivingIso, serviceType), dsType, errorDescription);
            } catch (Exception e) {
                LOGGER.error("[EADC Failure] Invocation Failed - Exception: '{}'", e.getMessage(), e);
            } finally {
                watch.stop();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("EADC Failure invocation executed in: '{}ms'", watch.getTime());
                }
            }
        }).start();
    }

    public static boolean hasTransactionErrors(SOAPEnvelope envelope) {
        if(envelope != null) {
            Iterator<OMElement> it = envelope.getBody().getChildElements();
            while (it.hasNext()) {
                OMElement elementDocSet = it.next();

                if (StringUtils.equals(elementDocSet.getLocalName(), "RegistryError")) {
                    String severity = elementDocSet.getAttributeValue(QName.valueOf("severity"));
                    if (StringUtils.equals(severity, "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error")) {
                        return true;
                    }
                }
                it = elementDocSet.getChildElements();
            }
        }

        return false;
    }

    public static String getTransactionErrorDescription(SOAPEnvelope envelope) {
        String errorDescription = "unknown";

        if(envelope != null) {
            Iterator<OMElement> it = envelope.getBody().getChildElements();
            while (it.hasNext()) {
                OMElement elementDocSet = it.next();

            /* example element
                <RegistryError
                        xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0"
                        codeContext="The requested encoding cannot be provided due to a transcoding error."
                        errorCode="4203"
                        severity="urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error"
                        location="urn:oid:2.16.17.710.823.1000.990.1"/>
            */
                if (StringUtils.equals(elementDocSet.getLocalName(), "RegistryError")) {
                    String err = elementDocSet.getAttributeValue(QName.valueOf("errorCode"));
                    String cod = elementDocSet.getAttributeValue(QName.valueOf("codeContext"));
                    errorDescription = cod + " [" + err + "]";
                    break;
                }
                it = elementDocSet.getChildElements();
            }
        } else {
            errorDescription = "envelope is null!";
        }
        return errorDescription;
    }

    /**
     * Builds a Transaction Info object based on a set of information.
     *
     * @param reqMsgContext the request Servlet Message Context
     * @param rspMsgContext the response Servlet Message Context
     * @param serviceClient the Axis2 Service Client
     * @param direction     the request direction, INBOUND or OUTBOUND
     * @param startTime     the transaction start time
     * @param endTime       the transaction end time
     * @param countryCodeA  the country A ISO Code
     * @param serviceType   the service type related to the IHE transaction
     * @return the filled Transaction Info object
     */
    private static TransactionInfo buildTransactionInfo(MessageContext reqMsgContext, MessageContext rspMsgContext,
                                                        ServiceClient serviceClient, Direction direction, Date startTime,
                                                        Date endTime, String countryCodeA, ServiceType serviceType) throws Exception {

        var transactionInfo = new ObjectFactory().createComplexTypeTransactionInfo();
        transactionInfo.setAuthenticationLevel(reqMsgContext != null ? extractAuthenticationMethodFromAssertion(getAssertion(reqMsgContext)) : null);
        transactionInfo.setDirection(direction != null ? direction.toString() : null);
        transactionInfo.setStartTime(startTime != null ? getDateAsRFC822String(startTime) : null);
        transactionInfo.setEndTime(endTime != null ? getDateAsRFC822String(endTime) : null);
        transactionInfo.setDuration(endTime != null && startTime != null ? String.valueOf(endTime.getTime() - startTime.getTime()) : null);
        transactionInfo.setHomeAddress(EventLogClientUtil.getSourceGatewayIdentifier());
        String sndIso = reqMsgContext != null ? extractSendingCountryIsoFromAssertion(getAssertion(reqMsgContext)) : null;
        transactionInfo.setSndISO(sndIso);
        transactionInfo.setSndNCPOID(sndIso != null ? OidUtil.getHomeCommunityId(sndIso.toLowerCase()) : null);

        if (reqMsgContext != null && reqMsgContext.getOptions() != null && reqMsgContext.getOptions().getFrom() != null
                && reqMsgContext.getOptions().getFrom().getAddress() != null) {

            transactionInfo.setHomeHost(reqMsgContext.getOptions().getFrom().getAddress());
        }

        /*
            (EHNCP-1141) We cannot get the MessageID from the reqMsgContext, it returns a wrong one.
            Probably related to how the Axis2 engine sets the MessageID, similar issues were faced during the Evidence
            Emitter refactoring. Plus, for the XCA Retrieve request messages, when comparing this MessageID with the one
            from the message itself, be sure to compare it with the correct WSA headers, there are duplicated ones,
            although belonging to different namespaces (the correct one is xmlns = http://www.w3.org/2005/08/addressing)
        */
        transactionInfo.setSndMsgID(reqMsgContext != null ? getMessageID(reqMsgContext.getEnvelope()) : null);
        transactionInfo.setHomeHCID("");
        transactionInfo.setHomeISO(Constants.COUNTRY_CODE);
        transactionInfo.setHomeNCPOID(Constants.HOME_COMM_ID);

        //  TODO: Clarify values for this field according specifications and GDPR, current value set to "N/A GDPR"
        transactionInfo.setHumanRequestor("N/A GDPR");
        transactionInfo.setUserId("N/A GDPR");
        transactionInfo.setPOC(reqMsgContext != null ?
                extractAssertionInfo(getAssertion(reqMsgContext), "urn:oasis:names:tc:xspa:1.0:environment:locality") + " (" +
                        extractAssertionInfo(getAssertion(reqMsgContext), "urn:epsos:names:wp3.4:subject:healthcare-facility-type") + ")" : null);
        transactionInfo.setPOCID(reqMsgContext != null ? extractAssertionInfo(getAssertion(reqMsgContext), "urn:oasis:names:tc:xspa:1.0:subject:organization-id") : null);
        transactionInfo.setReceivingISO(countryCodeA);
        transactionInfo.setReceivingNCPOID(countryCodeA != null ? OidUtil.getHomeCommunityId(countryCodeA.toLowerCase()) : null);

        if (serviceClient != null && serviceClient.getOptions() != null && serviceClient.getOptions().getTo() != null && serviceClient.getOptions().getTo().getAddress() != null) {
            transactionInfo.setReceivingHost(serviceClient.getOptions().getTo().getAddress());
            transactionInfo.setReceivingAddr(EventLogClientUtil.getTargetGatewayIdentifier(serviceClient.getOptions().getTo().getAddress()));
        }
        if (reqMsgContext != null && reqMsgContext.getOptions() != null && reqMsgContext.getOptions().getAction() != null) {
            transactionInfo.setRequestAction(reqMsgContext.getOptions().getAction());
        }
        if (rspMsgContext != null && rspMsgContext.getOptions() != null && rspMsgContext.getOptions().getAction() != null) {
            transactionInfo.setResponseAction(rspMsgContext.getOptions().getAction());
        }
        if (reqMsgContext != null && reqMsgContext.getOperationContext() != null && reqMsgContext.getOperationContext().getServiceName() != null) {
            transactionInfo.setServiceName(reqMsgContext.getOperationContext().getServiceName());
        }

        transactionInfo.setReceivingMsgID(rspMsgContext != null ? rspMsgContext.getOptions().getMessageId() : null);
        transactionInfo.setServiceType(serviceType.getDescription());
        transactionInfo.setTransactionCounter("");
        transactionInfo.setTransactionPK(UUID.randomUUID().toString());

        return transactionInfo;
    }

    /**
     * Extracts and assertion from a given message context
     *
     * @param requestMessageContext
     * @return
     * @throws Exception
     */
    private static Assertion getAssertion(MessageContext requestMessageContext) throws Exception {

        var soapHeader = requestMessageContext.getEnvelope().getHeader();
        Element soapHeaderElement = XMLUtils.toDOM(soapHeader);
        return Helper.getHCPAssertion(soapHeaderElement);
    }

    /**
     * Assertion utility method. Will extract information of a specific assertion, based on a given expression.
     *
     * @param idAssertion the Identity Assertion
     * @param expression  the expression to evaluate
     * @return a string representing the information presented on the specified node
     */
    private static String extractAssertionInfo(Assertion idAssertion, String expression) {
        if(idAssertion == null) {
            return null;
        }
        for (AttributeStatement attributeStatement : idAssertion.getAttributeStatements()) {
            for (Attribute attribute : attributeStatement.getAttributes()) {
                if (attribute.getName().equals(expression)) {
                    return getAttributeValue(attribute);
                }
            }
        }
        return null;
    }

    /**
     * Extracts information from a given Assertion attribute.
     *
     * @param attribute the Assertion attribute
     * @return a string containing the value of the attribute
     */
    private static String getAttributeValue(Attribute attribute) {

        String attributeValue = null;
        if (!attribute.getAttributeValues().isEmpty()) {
            attributeValue = attribute.getAttributeValues().get(0).getDOM().getTextContent();
        }
        return attributeValue;

    }

    /**
     * Utility method to convert a specific date to the RFC-2822 format.
     *
     * @param date the date object to be converted
     * @return the RFC 2822 string representation of the date
     */
    private static String getDateAsRFC822String(Date date) {

        var timeZone = TimeZone.getTimeZone("UTC");
        var dateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z");
        dateFormat.setTimeZone(timeZone);

        return dateFormat.format(date);
    }

    /**
     * Extracts a CDA document from a RetrieveDocumentSetResponseType
     *
     * @param retrieveDocumentSetResponseType
     * @return
     */
    public static Document getCDA(RetrieveDocumentSetResponseType retrieveDocumentSetResponseType) {

        RetrieveDocumentSetResponseType.DocumentResponse documentResponse;

        if (retrieveDocumentSetResponseType != null && retrieveDocumentSetResponseType.getDocumentResponse() != null
                && !retrieveDocumentSetResponseType.getDocumentResponse().isEmpty()) {

            documentResponse = retrieveDocumentSetResponseType.getDocumentResponse().get(0);
            byte[] documentData = documentResponse.getDocument();
            return toXmlDocument(documentData);
        }
        return null;
    }

    /**
     * Extracts the HP Authentication Method from the given Assertion.
     * All AuthN methods start with "urn:oasis:names:tc:SAML:2.0:ac:classes", e.g.
     * "urn:oasis:names:tc:SAML:2.0:ac:classes:Password", so we just extract the last portion.
     *
     * @param idAssertion the Identity Assertion
     * @return a string containing the authentication method
     */
    private static String extractAuthenticationMethodFromAssertion(Assertion idAssertion) {

        if(idAssertion == null) {
            return null;
        }
        if (!idAssertion.getAuthnStatements().isEmpty()) {
            var authnStatement = idAssertion.getAuthnStatements().get(0);
            String authnContextClassRef = authnStatement.getAuthnContext().getAuthnContextClassRef().getURI();
            return authnContextClassRef.substring(authnContextClassRef.lastIndexOf(':') + 1);
        } else {
            return null;
        }
    }

    /**
     * Extracts the Subject NameID from the given Assertion.
     *
     * @param idAssertion the Identity Assertion
     * @return string containing the assertion's Subject NameID
     */
    private static String extractNameIdFromAssertion(Assertion idAssertion) {
        return idAssertion.getSubject().getNameID().getValue();
    }

    /**
     * Extracts the sending country ISO code from Issuer of the given Assertion.
     * E.g., for this issuer:
     * <saml2:Issuer NameQualifier="urn:ehdsi:assertions:hcp">urn:idp:PT:countryB</saml2:Issuer> it will extract "PT"
     *
     * @param idAssertion
     * @return String containing the assertion issuer's ISO country code
     */
    private static String extractSendingCountryIsoFromAssertion(Assertion idAssertion) {
        if(idAssertion == null)
            return null;
        return idAssertion.getIssuer().getValue().split(":")[2];
    }

    /**
     * Copied from <code>_ServiceMessageReceiverInOut.java</code>
     * It returns the MessageID directly from the SOAP Envelope.
     *
     * @param envelope The SOAP envelope
     * @return The message ID
     */
    private static String getMessageID(SOAPEnvelope envelope) {

        Iterator<OMElement> it = envelope.getHeader().getChildrenWithName(
                new QName("http://www.w3.org/2005/08/addressing", "MessageID"));
        if (it.hasNext()) {
            return it.next().getText();
        } else {
            // [Mustafa: May 8, 2012]: Should not be empty string, sch. gives error.
            return Constants.UUID_PREFIX;
        }
    }

    public static Document toXmlDocument(byte[] content) {

        if (ArrayUtils.isEmpty(content)) {
            return null;
        }
        try {
            return XMLUtil.parseContent(content);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }
}
