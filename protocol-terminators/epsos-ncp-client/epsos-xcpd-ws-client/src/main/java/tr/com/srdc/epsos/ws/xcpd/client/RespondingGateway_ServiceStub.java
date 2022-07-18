package tr.com.srdc.epsos.ws.xcpd.client;

import com.spirit.epsos.cc.adc.EadcEntry;
import ee.affecto.epsos.util.EventLogClientUtil;
import ee.affecto.epsos.util.EventLogUtil;
import epsos.ccd.gnomon.auditmanager.EventLog;
import eu.epsos.exceptions.NoPatientIdDiscoveredException;
import eu.epsos.pt.eadc.EadcUtilWrapper;
import eu.epsos.pt.eadc.util.EadcUtil;
import eu.epsos.util.xcpd.XCPDConstants;
import eu.epsos.validation.datamodel.common.NcpSide;
import eu.europa.ec.sante.ehdsi.constant.error.OpenNCPErrorCode;
import eu.europa.ec.sante.ehdsi.eadc.ServiceType;
import eu.europa.ec.sante.ehdsi.gazelle.validation.OpenNCPValidation;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.RegisteredService;
import eu.europa.ec.sante.ehdsi.openncp.pt.common.DynamicDiscoveryService;
import eu.europa.ec.sante.ehdsi.openncp.ssl.HttpsClientConfiguration;
import eu.europa.ec.sante.ehdsi.openncp.util.OpenNCPConstants;
import eu.europa.ec.sante.ehdsi.openncp.util.ServerMode;
import eu.europa.ec.sante.ehdsi.constant.assertion.AssertionEnum;
import org.apache.axiom.om.*;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.kernel.http.HTTPConstants;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.lang.StringUtils;
import org.hl7.v3.PRPAIN201305UV02;
import org.hl7.v3.PRPAIN201306UV02;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.epsos.util.Constants;
import tr.com.srdc.epsos.util.XMLUtil;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * RespondingGateway_ServiceStub java implementation.
 *
 * @author SRDC<code> - epsos@srdc.com.tr>
 * @author Aarne Roosi<code> - Aarne.Roosi@Affecto.com</code>
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public class RespondingGateway_ServiceStub extends Stub {

    private static final Logger LOGGER = LoggerFactory.getLogger(RespondingGateway_ServiceStub.class);
    private static final Logger loggerClinical = LoggerFactory.getLogger("LOGGER_CLINICAL");
    private static final JAXBContext wsContext;
    private static int counter = 0;

    static {
        LOGGER.debug("Loading the WS-Security init libraries in RespondingGateway_ServiceStub xcpd");
        org.apache.xml.security.Init.init();
    }

    static {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(PRPAIN201305UV02.class, PRPAIN201306UV02.class);
        } catch (JAXBException ex) {
            LOGGER.error("Unable to create JAXBContext: '{}'", ex.getMessage(), ex);
            Runtime.getRuntime().exit(-1);
        } finally {
            wsContext = jaxbContext;
        }
    }

    // HashMap to keep the fault mapping
    private final HashMap faultExceptionNameMap = new HashMap();
    private final HashMap faultExceptionClassNameMap = new HashMap();
    private final HashMap faultMessageMap = new HashMap();
    private final QName[] opNameArray = null;
    protected AxisOperation[] _operations;
    private String countryCode;

    /**
     * Constructor that takes in a configContext
     */
    public RespondingGateway_ServiceStub(ConfigurationContext configurationContext, String targetEndpoint) {
        this(configurationContext, targetEndpoint, false);
    }

    /**
     * Constructor that takes in a configContext and useseperate listner
     */
    public RespondingGateway_ServiceStub(ConfigurationContext configurationContext, String targetEndpoint, boolean useSeparateListener) {

        // To populate AxisService
        populateAxisService();
        populateFaults();
        try {
            _serviceClient = new ServiceClient(configurationContext, _service);
        } catch (AxisFault ex) {
            throw new RuntimeException(ex);
        }

        _serviceClient.getOptions().setTo(new EndpointReference(targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);
        //  Wait time after which a client times out in a blocking scenario: 3 minutes
        _serviceClient.getOptions().setTimeOutInMilliSeconds(180000);

        // Set the soap version
        _serviceClient.getOptions().setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        // Enabling Axis2 - SSL 2 ways communication (not active by default).
        try {
            _serviceClient.getServiceContext().getConfigurationContext()
                    .setProperty(HTTPConstants.CACHED_HTTP_CLIENT, HttpsClientConfiguration.getSSLClient());
            _serviceClient.getServiceContext().getConfigurationContext()
                    .setProperty(HTTPConstants.REUSE_HTTP_CLIENT, false);
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException | CertificateException |
                 KeyStoreException | UnrecoverableKeyException e) {
            throw new RuntimeException("SSL Context cannot be initialized");
        }
    }

    /**
     * Constructor taking the target endpoint
     */
    public RespondingGateway_ServiceStub(String targetEndpoint) {
        this(null, targetEndpoint);
    }

    private static synchronized String getUniqueSuffix() {
        // reset the counter if it is greater than 99999
        if (counter > 99999) {
            counter = 0;
        }
        counter++;
        return System.currentTimeMillis() + "_" + counter;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    private void populateAxisService() {

        // creating the Service with a unique name
        _service = new AxisService("RespondingGateway_Service" + getUniqueSuffix());
        addAnonymousOperations();

        // creating the operations
        _operations = new AxisOperation[1];
        AxisOperation axisOperation = new OutInAxisOperation();
        axisOperation.setName(new QName(XCPDConstants.SOAP_HEADERS.NAMESPACE_URI, XCPDConstants.SOAP_HEADERS.NAMESPACE_REQUEST_LOCAL_PART));
        _service.addOperation(axisOperation);
        _operations[0] = axisOperation;
    }

    // populates the faults
    private void populateFaults() {
    }

    /**
     * Auto generated method signature
     *
     * @param prpain201305UV02
     * @param assertionMap
     * @return
     */
    public org.hl7.v3.PRPAIN201306UV02 respondingGateway_PRPA_IN201305UV02(PRPAIN201305UV02 prpain201305UV02, Map<AssertionEnum, Assertion> assertionMap) throws NoPatientIdDiscoveredException {

        MessageContext _messageContext = null;
        MessageContext _returnMessageContext = null;
        MessageContext messageContext = null;

        String eadcError = "";

        // Start Date for eADC
        Date transactionStartTime = new Date();
        // End Date for eADC
        Date transactionEndTime = new Date();

        LOGGER.info("respondingGateway_PRPA_IN201305UV02('{}', '{}'", prpain201305UV02.getId().getRoot(),
                assertionMap.get(AssertionEnum.CLINICIAN).getID());

        try {
            // TMP
            // XCPD request start time
            long start = System.currentTimeMillis();

            var operationClient = _serviceClient.createClient(_operations[0].getName());
            operationClient.getOptions().setAction(XCPDConstants.SOAP_HEADERS.REQUEST_ACTION);
            operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            var soapFactory = getFactory(operationClient.getOptions().getSoapVersionURI());

            /* create SOAP envelope with that payload */
            SOAPEnvelope env = toEnvelope(soapFactory, prpain201305UV02, optimizeContent(
                    new QName(XCPDConstants.SOAP_HEADERS.NAMESPACE_URI, XCPDConstants.SOAP_HEADERS.NAMESPACE_REQUEST_LOCAL_PART)));

            /* adding SOAP soap_headers */
            var factory = OMAbstractFactory.getOMFactory();
            var ns2 = factory.createOMNamespace(XCPDConstants.SOAP_HEADERS.OM_NAMESPACE, "");
            var soapHeaderBlock = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("Action", ns2);
            OMNode node = factory.createOMText(XCPDConstants.SOAP_HEADERS.REQUEST_ACTION);
            soapHeaderBlock.addChild(node);
            var omAttribute = factory.createOMAttribute(XCPDConstants.SOAP_HEADERS.MUST_UNDERSTAND, env.getNamespace(), "1");
            soapHeaderBlock.addAttribute(omAttribute);

            var headerBlockMessageId = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("MessageID", ns2);
            OMNode node2 = factory.createOMText(Constants.UUID_PREFIX + UUID.randomUUID());
            headerBlockMessageId.addChild(node2);

            var omNamespace = factory.createOMNamespace(XCPDConstants.SOAP_HEADERS.SECURITY_XSD, "wsse");
            var headerSecurity = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("Security", omNamespace);

            try {
                if (assertionMap.containsKey(AssertionEnum.NEXT_OF_KIN)) {
                    var assertionNextOfKin = assertionMap.get(AssertionEnum.NEXT_OF_KIN);
                    headerSecurity.addChild(XMLUtils.toOM(assertionNextOfKin.getDOM()));
                }
                var assertionId = assertionMap.get(AssertionEnum.CLINICIAN);
                headerSecurity.addChild(XMLUtils.toOM(assertionId.getDOM()));

                _serviceClient.addHeader(headerSecurity);
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            /* The WSA To header is not being manually added, it's added by the client-connector axis2.xml configurations
            (which globally engages the addressing module, adding the wsa:To header based on the endpoint value from the transport)
            based on the assumption that these IHE Service clients will always be coupled with client-connector, which may not be
            the case in the future. When that happens, we may need to revisit this code to add the To header like it's done in the IHE XCA service client.
            See issues EHNCP-1141 and EHNCP-1168. */
            _serviceClient.addHeader(soapHeaderBlock);
            _serviceClient.addHeader(headerBlockMessageId);
            _serviceClient.addHeadersToEnvelope(env);

            /* set the message context with that soap envelope */
            messageContext = new MessageContext();
            messageContext.setEnvelope(env);
            _messageContext = messageContext;

            /* add the message contxt to the operation client */
            operationClient.addMessageContext(messageContext);

            /* Log soap request */
            String logRequestBody;
            try {
                if (loggerClinical.isDebugEnabled() && !StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                    String logRequestMsg = XMLUtil.prettyPrint(XMLUtils.toDOM(env));
                    loggerClinical.debug(XCPDConstants.LOG.OUTGOING_XCPD_MESSAGE + System.getProperty("line.separator") + logRequestMsg);
                }
                logRequestBody = XMLUtil.prettyPrint(XMLUtils.toDOM(env.getBody().getFirstElement()));
                // NRO  NCPB_XCPD_REQ - LOGGER.info("XCPD Request sent. EVIDENCE NRO");

            } catch (Exception ex) {
                throw new NoPatientIdDiscoveredException(OpenNCPErrorCode.ERROR_PI_GENERIC,ex.getMessage());
            }

            // XCPD response end time
            long end = System.currentTimeMillis();
            LOGGER.info("XCPD REQUEST-RESPONSE TIME: '{}'", (end - start) / 1000.0);

            // Validation start time
            start = System.currentTimeMillis();

            // Validate Request Messages
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validatePatientDemographicRequest(logRequestBody, NcpSide.NCP_B);
            }
            // Transaction end time
            end = System.currentTimeMillis();
            LOGGER.info("XCPD VALIDATION REQ TIME: '{}'", (end - start) / 1000.0);

            // Transaction start time
            start = System.currentTimeMillis();

            /* execute the operation client */
            transactionStartTime = new Date();
            try {
                operationClient.execute(true);
            } catch (AxisFault e) {
                LOGGER.error("Axis Fault: Code-'{}' Message-'{}'", e.getFaultCode(), e.getMessage());
                LOGGER.error("Trying to automatically solve the problem by fetching configurations from the Central Services...");
                DynamicDiscoveryService dynamicDiscoveryService = new DynamicDiscoveryService();
                String value = dynamicDiscoveryService.getEndpointUrl(
                        this.countryCode.toLowerCase(Locale.ENGLISH), RegisteredService.PATIENT_IDENTIFICATION_SERVICE, true);

                if (StringUtils.isNotEmpty(value)) {

                    /* if we get something from the Central Services, then we retry the request */
                    /* correctly sets the Transport information with the new endpoint */
                    LOGGER.debug("Retrying the request with the new configurations: [{}]", value);
                    _serviceClient.getOptions().setTo(new EndpointReference(value));

                    /* we need a new OperationClient, otherwise we'll face the error "A message was added that is not valid. However, the operation context was complete." */
                    OperationClient newOperationClient = _serviceClient.createClient(_operations[0].getName());
                    newOperationClient.getOptions().setAction(XCPDConstants.SOAP_HEADERS.REQUEST_ACTION);
                    newOperationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
                    addPropertyToOperationClient(newOperationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

                    SOAPFactory newSoapFactory = getFactory(newOperationClient.getOptions().getSoapVersionURI());

                    /* we need to create a new SOAP payload so that the wsa:To header is correctly set
                    (i.e., copied from the Transport information to the wsa:To during the running of the Addressing Phase,
                    as defined by the global engagement of the addressing module in axis2.xml). The old payload still contains the old endpoint. */
                    SOAPEnvelope newEnv = toEnvelope(newSoapFactory, prpain201305UV02,
                            optimizeContent(new QName(XCPDConstants.SOAP_HEADERS.NAMESPACE_URI, XCPDConstants.SOAP_HEADERS.NAMESPACE_REQUEST_LOCAL_PART)));

                    /* we set the previous headers in the new SOAP envelope. Note: the wsa:To header is not manually set (only Action and MessageID are) but instead handled by the
                    axis2 configuration of client-connector (my assumption). This may have impact if we decouple client-connector from the IHE service clients. If
                    they are decoupled, we most probably have to add the To header manually like it's done in the IHE XCA client, both here and in the initial
                    request. See issues EHNCP-1141 and EHNCP-1168. */
                    _serviceClient.addHeadersToEnvelope(newEnv);

                    /* we create a new Message Context with the new SOAP envelope */
                    MessageContext newMessageContext = new MessageContext();
                    newMessageContext.setEnvelope(newEnv);

                    /* add the new message context to the new operation client */
                    newOperationClient.addMessageContext(newMessageContext);
                    /* we retry the request */
                    newOperationClient.execute(true);
                    /* we need to reset the previous variables with the new content, to be used later */
                    operationClient = newOperationClient;
                    messageContext = newMessageContext;
                    env = newEnv;
                    LOGGER.debug("Successfully retried the request! Proceeding with the normal workflow...");
                } else {
                    /* if we cannot solve this issue through the Central Services, then there's nothing we can do, so we let it be thrown */
                    eadcError = "Could not find configurations in the Central Services for [" + this.countryCode.toLowerCase(Locale.ENGLISH)
                            + RegisteredService.PATIENT_IDENTIFICATION_SERVICE.getServiceName() + "], the service will fail.";
                    LOGGER.error(eadcError);                    throw new NoPatientIdDiscoveredException(OpenNCPErrorCode.ERROR_PI_GENERIC, e);
                }
            }

            _returnMessageContext = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            transactionEndTime = new Date();

            // TMP
            // Transaction end time
            end = System.currentTimeMillis();
            LOGGER.info("XCPD TRANSACTION TIME: '{}'", (end - start) / 1000.0);

            // TMP
            // Validation start time
            start = System.currentTimeMillis();

            /* Log soap response */
            String logResponseBody;
            try {
                if (loggerClinical.isDebugEnabled() && !StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                    String logResponseMsg = XMLUtil.prettyPrint(XMLUtils.toDOM(_returnEnv));
                    loggerClinical.debug(XCPDConstants.LOG.INCOMING_XCPD_MESSAGE + System.getProperty("line.separator") + logResponseMsg);
                }
                logResponseBody = XMLUtil.prettyPrint(XMLUtils.toDOM(_returnEnv.getBody().getFirstElement()));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            /* Validate Response Messages */
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validatePatientDemographicResponse(logResponseBody, NcpSide.NCP_B);
            }
            Object object = fromOM(_returnEnv.getBody().getFirstElement(), PRPAIN201306UV02.class, getEnvelopeNamespaces(_returnEnv));

            // TMP
            // Validation end time
            end = System.currentTimeMillis();
            LOGGER.info("XCPD VALIDATION RES TIME: '{}'", (end - start) / 1000.0);

            // TMP
            // eADC start time
            start = System.currentTimeMillis();

            /*
             * Invoke NRR
             */
//            LOGGER.info("XCPD Response received. EVIDENCE NRR");
//            // NRR
//            try {
//                EvidenceUtils.createEvidenceREMNRR(XMLUtil.prettyPrint(XMLUtils.toDOM(env)),
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                        EventType.epsosIdentificationServiceFindIdentityByTraits.getCode(),
//                        new DateTime(),
//                        EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
//                        "NCPB_XCPD_RES");
//            } catch (Exception e) {
//                LOGGER.error(ExceptionUtils.getStackTrace(e));
//            }
            /*
             * Invoque eADC
             */
            EadcUtilWrapper.invokeEadc(messageContext, _returnMessageContext, this._getServiceClient(), null,
                    transactionStartTime, transactionEndTime, this.countryCode, EadcEntry.DsTypes.EADC,
                    EadcUtil.Direction.OUTBOUND, ServiceType.PATIENT_IDENTIFICATION_QUERY);

            // TMP
            // eADC end time
            end = System.currentTimeMillis();
            LOGGER.info("XCPD eADC TIME: '{}'", (end - start) / 1000.0);

            // TMP
            // Audit start time
            start = System.currentTimeMillis();

            // eventLog
            EventLog eventLog = createAndSendEventLog(prpain201305UV02, (org.hl7.v3.PRPAIN201306UV02) object, messageContext,
                    _returnEnv, env, assertionMap.get(AssertionEnum.CLINICIAN), this._getServiceClient().getOptions().getTo().getAddress());

            try {
                LOGGER.info("SOAP MESSAGE IS: '{}'", XMLUtils.toDOM(_returnEnv));
            } catch (Exception ex) {
                LOGGER.error(null, ex);
            }

            // Audit end time
            end = System.currentTimeMillis();
            LOGGER.info("XCPD AUDIT TIME: '{}'", (end - start) / 1000.0);

            return (org.hl7.v3.PRPAIN201306UV02) object;

        } catch (AxisFault axisFault) {

            //  TODO A.R. Audit log SOAP Fault is still missing
            OMElement faultElt = axisFault.getDetail();

            if (faultElt != null && faultExceptionNameMap.containsKey(faultElt.getQName())) {

                /* make the fault by reflection */
                try {
                    String exceptionClassName = (String) faultExceptionClassNameMap.get(faultElt.getQName());
                    var exceptionClass = Class.forName(exceptionClassName);
                    Exception ex = (Exception) exceptionClass.getDeclaredConstructor().newInstance();
                    // message class
                    String messageClassName = (String) faultMessageMap.get(faultElt.getQName());
                    var messageClass = Class.forName(messageClassName);
                    Object messageObject = fromOM(faultElt, messageClass, null);
                    Method method = exceptionClass.getMethod("setFaultMessage", messageClass);
                    method.invoke(ex, messageObject);

                    throw new java.rmi.RemoteException(ex.getMessage(), ex);

                } catch (Exception e) {
                    // we cannot instantiate the class - throw the original Axis fault
                    eadcError = axisFault.getMessage();
                    throw new RuntimeException(axisFault.getMessage(), axisFault);
                }
            }
            eadcError = OpenNCPErrorCode.ERROR_GENERIC_CONNECTION_NOT_POSSIBLE.getCode();
            throw new NoPatientIdDiscoveredException(OpenNCPErrorCode.ERROR_GENERIC_CONNECTION_NOT_POSSIBLE, "AxisFault");
        } finally {
            if (_messageContext != null && _messageContext.getTransportOut() != null && _messageContext.getTransportOut().getSender() != null) {
                try {
                    _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                } catch (AxisFault ex) {
                    LOGGER.error(null, ex);
                }
            }
            if(!eadcError.isEmpty()) {
                EadcUtilWrapper.invokeEadcFailure(messageContext, _returnMessageContext, this._getServiceClient(), null,
                        transactionStartTime, transactionEndTime, this.countryCode, EadcEntry.DsTypes.EADC,
                        EadcUtil.Direction.OUTBOUND, ServiceType.PATIENT_IDENTIFICATION_QUERY, eadcError);
            }
        }
    }

    /**
     * A utility method that copies the namespaces from the SOAPEnvelope
     */
    private Map getEnvelopeNamespaces(SOAPEnvelope env) {

        Map returnMap = new java.util.HashMap();
        Iterator namespaceIterator = env.getAllDeclaredNamespaces();

        while (namespaceIterator.hasNext()) {
            OMNamespace ns = (OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }

        return returnMap;
    }

    private boolean optimizeContent(QName opName) {

        if (opNameArray == null) {
            return false;
        }
        for (QName anOpNameArray : opNameArray) {
            if (opName.equals(anOpNameArray)) {
                return true;
            }
        }
        return false;
    }

    private OMElement toOM(PRPAIN201305UV02 param, boolean optimizeContent) throws AxisFault {

        try {
            var marshaller = wsContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            var omFactory = OMAbstractFactory.getOMFactory();
            var jaxbRIDataSource = new JaxbRIDataSource(PRPAIN201305UV02.class, param, marshaller,
                    XCPDConstants.HL7_V3_NAMESPACE_URI, XCPDConstants.PATIENT_DISCOVERY_REQUEST);
            var omNamespace = omFactory.createOMNamespace(XCPDConstants.HL7_V3_NAMESPACE_URI, null);

            return omFactory.createOMElement(jaxbRIDataSource, XCPDConstants.PATIENT_DISCOVERY_REQUEST, omNamespace);
        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    private SOAPEnvelope toEnvelope(SOAPFactory factory, PRPAIN201305UV02 param, boolean optimizeContent) throws AxisFault {

        SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(toOM(param, optimizeContent));
        return envelope;
    }

    private OMElement toOM(PRPAIN201306UV02 param, boolean optimizeContent) throws AxisFault {

        try {
            Marshaller marshaller = wsContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            OMFactory factory = OMAbstractFactory.getOMFactory();

            JaxbRIDataSource source = new JaxbRIDataSource(PRPAIN201306UV02.class, param, marshaller,
                    XCPDConstants.HL7_V3_NAMESPACE_URI, XCPDConstants.PATIENT_DISCOVERY_RESPONSE);
            OMNamespace namespace = factory.createOMNamespace(XCPDConstants.HL7_V3_NAMESPACE_URI, null);
            return factory.createOMElement(source, XCPDConstants.PATIENT_DISCOVERY_RESPONSE, namespace);
        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    private SOAPEnvelope toEnvelope(SOAPFactory factory, PRPAIN201306UV02 param, boolean optimizeContent) throws AxisFault {

        SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(toOM(param, optimizeContent));
        return envelope;
    }

    /**
     * get the default envelope
     */
    private SOAPEnvelope toEnvelope(SOAPFactory factory) {

        return factory.getDefaultEnvelope();
    }

    private Object fromOM(OMElement param, Class type, Map extraNamespaces) throws AxisFault {

        try {
            JAXBContext context = wsContext;
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return unmarshaller.unmarshal(param.getXMLStreamReaderWithoutCaching(), type).getValue();

        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    private EventLog createAndSendEventLog(PRPAIN201305UV02 sended, PRPAIN201306UV02 received, MessageContext msgContext,
                                           SOAPEnvelope _returnEnv, SOAPEnvelope env, Assertion idAssertion, String address) {

        EventLog eventLog = EventLogClientUtil.prepareEventLog(msgContext, _returnEnv, address);
        eventLog.setNcpSide(NcpSide.NCP_B);
        EventLogClientUtil.logIdAssertion(eventLog, idAssertion);
        EventLogUtil.prepareXCPDCommonLog(eventLog, sended, received);
        EventLogClientUtil.sendEventLog(eventLog);
        return eventLog;
    }

    class JaxbRIDataSource implements OMDataSource {

        /**
         * Bound object for output.
         */
        private final Object outObject;
        /**
         * Bound class for output.
         */
        private final Class outClazz;
        /**
         * Marshaller.
         */
        private final Marshaller marshaller;
        /**
         * Namespace
         */
        private final String nsuri;
        /**
         * Local name
         */
        private final String name;

        /**
         * Constructor from object and marshaller.
         *
         * @param obj
         * @param marshaller
         */
        public JaxbRIDataSource(Class clazz, Object obj, Marshaller marshaller, String nsuri, String name) {
            this.outClazz = clazz;
            this.outObject = obj;
            this.marshaller = marshaller;
            this.nsuri = nsuri;
            this.name = name;
        }

        public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {

            try {
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), output);

            } catch (JAXBException e) {
                throw new XMLStreamException("Error in JAXB marshalling", e);
            }
        }

        public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
            try {
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), writer);

            } catch (JAXBException e) {
                throw new XMLStreamException("Error in JAXB marshalling", e);
            }
        }

        public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
            try {
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), xmlWriter);

            } catch (JAXBException e) {
                throw new XMLStreamException("Error in JAXB marshalling", e);
            }
        }

        public XMLStreamReader getReader() throws XMLStreamException {

            try {
                var omDocument = OMAbstractFactory.getOMFactory().createOMDocument();
                wsContext.createMarshaller().marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), omDocument.getSAXResult());

                return omDocument.getOMDocumentElement().getXMLStreamReader();
            } catch (JAXBException e) {
                throw new XMLStreamException("Error in JAXB marshalling", e);
            }
        }
    }
}
