package tr.com.srdc.epsos.ws.xca.client;

import com.spirit.epsos.cc.adc.EadcEntry;
import ee.affecto.epsos.util.EventLogClientUtil;
import ee.affecto.epsos.util.EventLogUtil;
import epsos.ccd.gnomon.auditmanager.EventLog;
import eu.epsos.exceptions.XCAException;
import eu.epsos.pt.eadc.EadcUtilWrapper;
import eu.epsos.pt.eadc.util.EadcUtil.Direction;
import eu.epsos.util.xca.XCAConstants;
import eu.epsos.validation.datamodel.common.NcpSide;
import eu.europa.ec.sante.ehdsi.constant.ClassCode;
import eu.europa.ec.sante.ehdsi.constant.error.OpenNCPErrorCode;
import eu.europa.ec.sante.ehdsi.eadc.ServiceType;
import eu.europa.ec.sante.ehdsi.gazelle.validation.OpenNCPValidation;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.RegisteredService;
import eu.europa.ec.sante.ehdsi.openncp.pt.common.DynamicDiscoveryService;
import eu.europa.ec.sante.ehdsi.openncp.ssl.HttpsClientConfiguration;
import eu.europa.ec.sante.ehdsi.openncp.util.OpenNCPConstants;
import eu.europa.ec.sante.ehdsi.openncp.util.ServerMode;
import eu.europa.ec.sante.ehdsi.constant.assertion.AssertionEnum;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import org.apache.axiom.om.*;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
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
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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

/*
 *  RespondingGateway_ServiceStub java implementation
 */
public class RespondingGateway_ServiceStub extends Stub {

    private static final Logger LOGGER = LoggerFactory.getLogger(RespondingGateway_ServiceStub.class);
    private static final JAXBContext wsContext;
    private static int counter = 0;

    static {

        LOGGER.debug("Loading the WS-Security init libraries in RespondingGateway_ServiceStub xca");
        org.apache.xml.security.Init.init();
    }

    static {

        JAXBContext jc;
        jc = null;
        try {
            jc = JAXBContext.newInstance(AdhocQueryRequest.class,
                    AdhocQueryResponse.class, RetrieveDocumentSetRequestType.class,
                    RetrieveDocumentSetResponseType.class);
        } catch (JAXBException ex) {
            LOGGER.error(XCAConstants.EXCEPTIONS.UNABLE_CREATE_JAXB_CONTEXT + " " + ex.getMessage(), ex);
            Runtime.getRuntime().exit(-1);
        } finally {
            wsContext = jc;
        }
    }

    private final Logger loggerClinical = LoggerFactory.getLogger("LOGGER_CLINICAL");
    //hashmaps to keep the fault mapping
    private final java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
    private final java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
    private final java.util.HashMap faultMessageMap = new java.util.HashMap();
    private final QName[] opNameArray = null;
    protected AxisOperation[] _operations;
    private String addr;
    private String countryCode;
    private Date transactionStartTime;
    private Date transactionEndTime;

    /**
     * Constructor that takes in a configContext
     */
    public RespondingGateway_ServiceStub(ConfigurationContext configurationContext, java.lang.String targetEndpoint) throws AxisFault {

        this(configurationContext, targetEndpoint, false);
    }

    /**
     * Constructor that takes in a configContext and use separate listener
     */
    public RespondingGateway_ServiceStub(ConfigurationContext configurationContext, String targetEndpoint, boolean useSeparateListener) throws AxisFault {

        //To populate AxisService
        populateAxisService();
        populateFaults();

        _serviceClient = new ServiceClient(configurationContext, _service);
        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);
        //  Wait time after which a client times out in a blocking scenario: 3 minutes
        _serviceClient.getOptions().setTimeOutInMilliSeconds(180000);
        //Set the soap version
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
     * Default Constructor
     */
    public RespondingGateway_ServiceStub(ConfigurationContext configurationContext) throws AxisFault {

        this(configurationContext, "http://195.142.27.167:8111/tr-xca/services/RespondingGateway_Service");
    }

    /**
     * Default Constructor
     */
    public RespondingGateway_ServiceStub() throws AxisFault {

        this("http://195.142.27.167:8111/tr-xca/services/RespondingGateway_Service");
    }

    /**
     * Constructor taking the target endpoint
     */
    public RespondingGateway_ServiceStub(java.lang.String targetEndpoint) throws AxisFault {
        this(null, targetEndpoint);
    }

    private static synchronized java.lang.String getUniqueSuffix() {

        // reset the counter if it is greater than 99999
        if (counter > 99999) {
            counter = 0;
        }
        counter++;
        return System.currentTimeMillis() + "_" + counter;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    private void populateAxisService() {

        //creating the Service with a unique name
        _service = new AxisService(XCAConstants.RESPONDING_GATEWAY_SERVICE + getUniqueSuffix());
        addAnonymousOperations();
        //creating the operations
        AxisOperation __operation;
        _operations = new AxisOperation[2];
        __operation = new OutInAxisOperation();
        __operation.setName(new QName(XCAConstants.SOAP_HEADERS.NAMESPACE_URI, XCAConstants.SOAP_HEADERS.QUERY.NAMESPACE_REQUEST_LOCAL_PART));
        _service.addOperation(__operation);
        _operations[0] = __operation;
        __operation = new OutInAxisOperation();
        __operation.setName(new QName(XCAConstants.SOAP_HEADERS.NAMESPACE_URI, XCAConstants.SOAP_HEADERS.RETRIEVE.NAMESPACE_REQUEST_LOCAL_PART));
        _service.addOperation(__operation);
        _operations[1] = __operation;
    }

    /**
     * Populates the faults
     */
    private void populateFaults() {
        // Not implemented in eHDSI-OpenNCP.
    }

    /**
     * Auto generated method signature
     *
     * @param adhocQueryRequest
     */
    public AdhocQueryResponse respondingGateway_CrossGatewayQuery(AdhocQueryRequest adhocQueryRequest,
                                                                  Map<AssertionEnum, Assertion> assertionMap,
                                                                  List<ClassCode> classCodes)
            throws java.rmi.RemoteException, XCAException {

        String eadcError = "";

        MessageContext _messageContext = null;
        MessageContext _returnMessageContext = null;
        try {
            // TMP
            // XCA list request start time
            long start = System.currentTimeMillis();

            OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
            _operationClient.getOptions().setAction(XCAConstants.SOAP_HEADERS.QUERY.REQUEST_ACTION);
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new MessageContext();

            // create SOAP envelope with that payload
            SOAPEnvelope env;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    adhocQueryRequest,
                    optimizeContent(new QName(XCAConstants.SOAP_HEADERS.NAMESPACE_URI, XCAConstants.SOAP_HEADERS.QUERY.NAMESPACE_REQUEST_LOCAL_PART)));

            /*
             * adding SOAP soap_headers
             */
            SOAPFactory soapFactory = getFactory(_operationClient.getOptions().getSoapVersionURI());
            OMFactory factory = OMAbstractFactory.getOMFactory();

            OMNamespace ns2 = factory.createOMNamespace(XCAConstants.SOAP_HEADERS.QUERY.OM_NAMESPACE, "addressing");

            SOAPHeaderBlock action = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("Action", ns2);
            OMNode node = factory.createOMText(XCAConstants.SOAP_HEADERS.QUERY.REQUEST_ACTION);
            action.addChild(node);
            OMAttribute att1 = factory.createOMAttribute(XCAConstants.SOAP_HEADERS.MUST_UNDERSTAND, env.getNamespace(), "1");
            action.addAttribute(att1);

            SOAPHeaderBlock id = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("MessageID", ns2);
            OMNode node2 = factory.createOMText(Constants.UUID_PREFIX + UUID.randomUUID());
            id.addChild(node2);

            SOAPHeaderBlock to = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("To", ns2);
            OMNode node3 = factory.createOMText(addr);
            to.addChild(node3);
            OMAttribute att2 = factory.createOMAttribute(XCAConstants.SOAP_HEADERS.MUST_UNDERSTAND, env.getNamespace(), "1");
            to.addAttribute(att2);

            SOAPHeaderBlock replyTo = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("ReplyTo", ns2);
            OMElement address = OMAbstractFactory.getSOAP12Factory().createOMElement("Address", ns2);
            OMNode node4 = factory.createOMText(XCAConstants.SOAP_HEADERS.ADDRESSING_NAMESPACE);
            address.addChild(node4);
            replyTo.addChild(address);

            /* We are manually adding all WSA headers (To, Action, MessageID). Other IHE service clients (XCPD and XDR) skip the addition of the To header
            and let the client-connector axis2 configurations take care of it (through the global engaging of WS-Addressing module in axis2.xml, which sets
            the To with the endpoint value from the transport information), but we cannot assume that these IHE Service clients will always be coupled with
            the client-connector (and that it'll always be based on Axis2). See issues EHNCP-1141 and EHNCP-1168.
            */
            _serviceClient.addHeader(to);
            _serviceClient.addHeader(action);
            _serviceClient.addHeader(id);
            _serviceClient.addHeader(replyTo);

            var omNamespace = factory.createOMNamespace(XCAConstants.SOAP_HEADERS.SECURITY_XSD, "wsse");
            var headerSecurity = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("Security", omNamespace);

            try {
                if (assertionMap.containsKey(AssertionEnum.NEXT_OF_KIN)) {
                    var assertionNextOfKin = assertionMap.get(AssertionEnum.NEXT_OF_KIN);
                    headerSecurity.addChild(XMLUtils.toOM(assertionNextOfKin.getDOM()));
                }
                var assertionId = assertionMap.get(AssertionEnum.CLINICIAN);
                headerSecurity.addChild(XMLUtils.toOM(assertionId.getDOM()));
                var assertionTreatment = assertionMap.get(AssertionEnum.TREATMENT);
                headerSecurity.addChild(XMLUtils.toOM(assertionTreatment.getDOM()));

                _serviceClient.addHeader(headerSecurity);
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }

            _serviceClient.addHeadersToEnvelope(env);

            /*
             * Prepare request
             */
            _messageContext.setEnvelope(env);   // set the message context with that soap envelope
            _operationClient.addMessageContext(_messageContext);    // add the message contxt to the operation client

            /* Log soap request */
            String logRequestBody;
            try {
                if (!org.apache.commons.lang3.StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                    String logRequestMsg = XMLUtil.prettyPrint(XMLUtils.toDOM(env));
                    loggerClinical.debug("{}\n{}", XCAConstants.LOG.OUTGOING_XCA_QUERY_MESSAGE, logRequestMsg);
                }
                logRequestBody = XMLUtil.prettyPrint(XMLUtils.toDOM(env.getBody().getFirstElement()));
            } catch (Exception ex) {
                throw new XCAException(OpenNCPErrorCode.ERROR_GENERIC, ex.getMessage(), null);
            }
            // NRO
//                try {
//                    EvidenceUtils.createEvidenceREMNRO(envCanonicalized,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                            EventType.epsosOrderServiceList.getCode(),
//                            new DateTime(),
//                            EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
//                            "NCPB_XCA_LIST_REQ");
//                } catch (Exception e) {
//                    LOGGER.error(ExceptionUtils.getStackTrace(e));
//                }

            // TMP
            // XCA List response end time
            long end = System.currentTimeMillis();
            LOGGER.info("XCA LIST REQUEST-RESPONSE TIME: '{}' ms", (end - start) / 1000.0);

            // TMP
            // Validation start time
            start = System.currentTimeMillis();

            /* Validate Request Message */
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validateCrossCommunityAccess(logRequestBody, NcpSide.NCP_B, classCodes);
            }

            // TMP
            // Transaction end time
            end = System.currentTimeMillis();
            LOGGER.info("XCA LIST VALIDATION REQ TIME: '{}' ms", (end - start) / 1000.0);

            // TMP
            // Transaction start time
            start = System.currentTimeMillis();

            /*
             * Execute Operation
             */
            transactionStartTime = new Date();
            try {
                _operationClient.execute(true);
            } catch (AxisFault e) {
                LOGGER.error("Axis Fault error: '{}'", e.getMessage());
                LOGGER.error("Trying to automatically solve the problem by fetching configurations from the Central Services...");
                LOGGER.debug("ClassCode: '{}'", Arrays.toString(classCodes.toArray()));
                DynamicDiscoveryService dynamicDiscoveryService = new DynamicDiscoveryService();
                RegisteredService registeredService = getRegisteredService(classCodes);
                String endpoint = dynamicDiscoveryService.getEndpointUrl(
                        this.countryCode.toLowerCase(Locale.ENGLISH), registeredService, true);

                if (StringUtils.isNotEmpty(endpoint)) {

                    /* if we get something from the Central Services, then we retry the request */
                    /* correctly sets the Transport information with the new endpoint */
                    LOGGER.debug("Retrying the request with the new configurations: [{}]", endpoint);
                    _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(endpoint));

                    /* we need a new OperationClient, otherwise we'll face the error "A message was added that is not valid. However, the operation context was complete." */
                    OperationClient newOperationClient = _serviceClient.createClient(_operations[0].getName());
                    newOperationClient.getOptions().setAction(XCAConstants.SOAP_HEADERS.QUERY.REQUEST_ACTION);
                    newOperationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
                    addPropertyToOperationClient(newOperationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

                    SOAPFactory newSoapFactory = getFactory(newOperationClient.getOptions().getSoapVersionURI());

                    /* As there's no simple way of replacing the previously set WSA To header (still containing the old endpoint) we need to create a new SOAP
                    payload so that the wsa:To header is correctly set. Here we have 2 situations. If we rely on the client-connector axis2.xml configurations,
                    then the new endpoint will be copied from the Transport information to the wsa:To during the running of the Addressing Phase,
                    as defined by the global engagement of the addressing module in axis2.xml. But we're not doing it since these IHE service clients should be
                    decoupled from client-connector, thus we manually add the new WSA To header to the new SOAP envelope. See issues EHNCP-1141 and EHNCP-1168. */
                    SOAPEnvelope newEnv;
                    newEnv = toEnvelope(newSoapFactory,
                            adhocQueryRequest,
                            optimizeContent(new QName(XCAConstants.SOAP_HEADERS.NAMESPACE_URI, XCAConstants.SOAP_HEADERS.QUERY.NAMESPACE_REQUEST_LOCAL_PART)));

                    /* Creating the new WSA To header with the new endpoint */
                    to = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("To", ns2);
                    node3 = newSoapFactory.createOMText(endpoint);
                    to.addChild(node3);
                    to.addAttribute(att2);

                    /* There's no way to remove a single header (and WS-Addressing specification forbids the existence of
                    more than 1 addressing:To header), so we need to remove all the old headers and add them again to _serviceClient
                    (including the new To header with the right endpoint), from which they'll be copied into the final SOAP envelope */
                    _serviceClient.removeHeaders();
                    _serviceClient.addHeader(to);
                    _serviceClient.addHeader(action);
                    _serviceClient.addHeader(id);
                    _serviceClient.addHeader(replyTo);
                    _serviceClient.addHeader(headerSecurity);
                    _serviceClient.addHeadersToEnvelope(newEnv);

                    /* we create a new Message Context with the new SOAP envelope */
                    MessageContext newMessageContext = new MessageContext();
                    newMessageContext.setEnvelope(newEnv);

                    /* add the new message context to the new operation client */
                    newOperationClient.addMessageContext(newMessageContext);
                    /* we retry the request */
                    newOperationClient.execute(true);
                    /* we need to reset the previous variables with the new content, to be used later */
                    _operationClient = newOperationClient;
                    _messageContext = newMessageContext;
                    env = newEnv;
                    LOGGER.debug("Successfully retried the request! Proceeding with the normal workflow...");
                } else {
                    /* if we cannot solve this issue through the Central Services, then there's nothing we can do, so we let it be thrown */
                    eadcError = "Could not find configurations in the Central Services for [" + endpoint + "], the service will fail.";
                    LOGGER.error(eadcError);
                    throw new XCAException(OpenNCPErrorCode.ERROR_GENERIC, e.getMessage(), null);
                }
            }
            _returnMessageContext = _operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            transactionEndTime = new Date();

            // TMP
            // Transaction end time
            end = System.currentTimeMillis();
            LOGGER.info("XCA LIST TRANSACTION TIME: '{}'", (end - start) / 1000.0);

            // TMP
            // Transaction start time
            start = System.currentTimeMillis();

            /* Log soap response */
            String logResponseBody;
            try {
                if (!org.apache.commons.lang3.StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                    String logResponseMsg = XMLUtil.prettyPrint(XMLUtils.toDOM(_returnEnv));
                    loggerClinical.debug("{}\n{}", XCAConstants.LOG.INCOMING_XCA_QUERY_MESSAGE, logResponseMsg);
                }
                logResponseBody = XMLUtil.prettyPrint(XMLUtils.toDOM(_returnEnv.getBody().getFirstElement()));
            } catch (Exception ex) {
                throw new XCAException(OpenNCPErrorCode.ERROR_GENERIC, ex.getMessage(), null);
            }

            /* Validate Response Message */
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validateCrossCommunityAccess(logResponseBody, NcpSide.NCP_B, classCodes);
            }

            // TMP
            // Validation end time
            end = System.currentTimeMillis();
            LOGGER.info("XCA LIST VALIDATION RES TIME: '{}'", (end - start) / 1000);

            // eADC start time
            start = System.currentTimeMillis();

            // NRR
//            try {
//                EvidenceUtils.createEvidenceREMNRR(XMLUtil.prettyPrint(XMLUtils.toDOM(env)),
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                        tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                        EventType.epsosOrderServiceList.getCode(),
//                        new DateTime(),
//                        EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
//                        "NCPB_XCA_LIST_RES");
//            } catch (Exception e) {
//                LOGGER.error(ExceptionUtils.getStackTrace(e));
//            }

            //  Invoke eADC
            if(!EadcUtilWrapper.hasTransactionErrors(_returnEnv)) {
                EadcUtilWrapper.invokeEadc(_messageContext, _returnMessageContext, this._getServiceClient(), null,
                        transactionStartTime, transactionEndTime, this.countryCode, EadcEntry.DsTypes.EADC,
                        Direction.OUTBOUND, ServiceType.DOCUMENT_LIST_QUERY);
            } else {
                eadcError = EadcUtilWrapper.getTransactionErrorDescription(_returnEnv);
            }
            // eADC end time
            end = System.currentTimeMillis();
            LOGGER.info("XCA LIST eADC TIME: '{}' ms", (end - start) / 1000.0);

            // Audit start time
            start = System.currentTimeMillis();

            /*
             * Return
             */
            java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    AdhocQueryResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            AdhocQueryResponse adhocQueryResponse = (AdhocQueryResponse) object;
            for (ClassCode classCode : classCodes) {
                createAndSendEventLogQuery(adhocQueryRequest, adhocQueryResponse,
                        _messageContext, _returnEnv, env, assertionMap.get(AssertionEnum.CLINICIAN), assertionMap.get(AssertionEnum.TREATMENT),
                        this._getServiceClient().getOptions().getTo().getAddress(),
                        classCode); // Audit
            }
            // TMP
            // Audit end time
            end = System.currentTimeMillis();
            LOGGER.info("XCA LIST AUDIT TIME: '{}' ms", (end - start) / 1000.0);

            return adhocQueryResponse;

        } catch (AxisFault axisFault) {
            // TODO A.R. Audit log SOAP Fault is still missing
            OMElement faultElt = axisFault.getDetail();

            if (faultElt != null) {

                if (faultExceptionNameMap.containsKey(faultElt.getQName())) {

                    //make the fault by reflection
                    try {
                        String exceptionClassName = (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                        Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        Exception ex = (java.lang.Exception) exceptionClass.getDeclaredConstructor().newInstance();
                        //message class
                        String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        Class messageClass = java.lang.Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        Method m = exceptionClass.getMethod("setFaultMessage", messageClass);
                        m.invoke(ex, messageObject);

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);

                        /* we cannot intantiate the class - throw the original Axis fault */
                    } catch (Exception e) {
                        eadcError = e.getMessage();
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
            eadcError = OpenNCPErrorCode.ERROR_GENERIC_CONNECTION_NOT_POSSIBLE.getCode();
            throw new XCAException(OpenNCPErrorCode.ERROR_GENERIC_CONNECTION_NOT_POSSIBLE, "AxisFault", null);

        } finally {
            if (_messageContext != null && _messageContext.getTransportOut() != null && _messageContext.getTransportOut().getSender() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
            if(!eadcError.isEmpty()) {
                EadcUtilWrapper.invokeEadcFailure(_messageContext, _returnMessageContext, this._getServiceClient(), null,
                        transactionStartTime, transactionEndTime, this.countryCode, EadcEntry.DsTypes.EADC,
                        Direction.OUTBOUND, ServiceType.DOCUMENT_LIST_QUERY, eadcError);
            }
        }
    }

    private RegisteredService getRegisteredService(List<ClassCode> classCodes) {
        RegisteredService registeredService = null;
        for (ClassCode classCode : classCodes) {
            switch (classCode) {
                case EP_CLASSCODE:
                    if (registeredService == null) {
                        registeredService = RegisteredService.ORDER_SERVICE;
                    } else {
                        LOGGER.error("It is not allowed to pass more than one classCode when the classCode '{}' is used.", ClassCode.EP_CLASSCODE.getCode());
                    }
                    break;
                case PS_CLASSCODE:
                    if (registeredService == null) {
                        registeredService = RegisteredService.PATIENT_SERVICE;
                    } else {
                        LOGGER.error("It is not allowed to pass more than one classCode when the classCode '{}' is used.", ClassCode.PS_CLASSCODE.getCode());
                    }
                    break;
                case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                case ORCD_LABORATORY_RESULTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                case ORCD_MEDICAL_IMAGES_CLASSCODE:
                    if (registeredService == null || registeredService == RegisteredService.ORCD_SERVICE) {
                        registeredService = RegisteredService.ORCD_SERVICE;
                    } else {
                        LOGGER.error("It is only allowed to pass OrCD classCodes when more than one classCode is passed.");
                    }
                    break;
                default:
                    break;
            }
        }
        return registeredService;
    }

    /**
     * Auto generated method signature
     *
     * @param retrieveDocumentSetRequest XCA request
     * @param assertionMap               HCP identity Assertion
     * @param classCode                  Class code of the document to be retrieved, needed for audit log preparation
     * @return RetrieveDocumentSetResponseType - Retrieve Document Response
     * @throws java.rmi.RemoteException
     */
    public RetrieveDocumentSetResponseType respondingGateway_CrossGatewayRetrieve(RetrieveDocumentSetRequestType retrieveDocumentSetRequest,
                                                                                  Map<AssertionEnum, Assertion> assertionMap,
                                                                                  ClassCode classCode)
            throws java.rmi.RemoteException, XCAException {

        String eadcError = "";
        MessageContext _messageContext = null;
        MessageContext _returnMessageContext = null;
        Document cda = null;

        SOAPEnvelope env;
        try {
            OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
            _operationClient.getOptions().setAction(XCAConstants.SOAP_HEADERS.RETRIEVE.REQUEST_ACTION);
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
            addPropertyToOperationClient(_operationClient, org.apache.axis2.Constants.Configuration.ENABLE_MTOM, org.apache.axis2.Constants.VALUE_TRUE);

            // create a message context
            _messageContext = new MessageContext();

            // create SOAP envelope with that payload
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    retrieveDocumentSetRequest,
                    optimizeContent(new QName(XCAConstants.SOAP_HEADERS.NAMESPACE_URI, XCAConstants.SOAP_HEADERS.RETRIEVE.NAMESPACE_REQUEST_LOCAL_PART)));

            /*
             * adding SOAP soap_headers
             */
            SOAPFactory soapFactory = getFactory(_operationClient.getOptions().getSoapVersionURI());
            OMFactory factory = OMAbstractFactory.getOMFactory();

            OMNamespace ns2 = factory.createOMNamespace(XCAConstants.SOAP_HEADERS.ADDRESSING_NAMESPACE, "addressing");

            OMElement action = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("Action", ns2);
            OMNode node = factory.createOMText(XCAConstants.SOAP_HEADERS.RETRIEVE.REQUEST_ACTION);
            action.addChild(node);

            OMElement id = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("MessageID", ns2);
            OMNode node2 = factory.createOMText(Constants.UUID_PREFIX + UUID.randomUUID());
            id.addChild(node2);

            OMElement to = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("To", ns2);
            OMNode node3 = factory.createOMText(addr);
            to.addChild(node3);

            OMElement replyTo = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("ReplyTo", ns2);
            OMElement address = OMAbstractFactory.getSOAP12Factory().createOMElement("Address", ns2);
            OMNode node4 = factory.createOMText(XCAConstants.SOAP_HEADERS.ADDRESSING_NAMESPACE);
            address.addChild(node4);
            replyTo.addChild(address);

            /* We are manually adding all WSA headers (To, Action, MessageID). Other IHE service clients (XCPD and XDR) skip the addition of the To header
            and let the client-connector axis2 configurations take care of it (through the global engaging of WS-Addressing module in axis2.xml, which sets
            the To with the endpoint value from the transport information), but we cannot assume that these IHE Service clients will always be coupled with
            the client-connector (and that it'll always be based on Axis2). See issues EHNCP-1141 and EHNCP-1168.
            */
            _serviceClient.addHeader(to);
            _serviceClient.addHeader(action);
            _serviceClient.addHeader(id);
            _serviceClient.addHeader(replyTo);


            OMNamespace ns = factory.createOMNamespace(XCAConstants.SOAP_HEADERS.SECURITY_XSD, "wsse");
            OMElement security = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("Security", ns);

            try {

                if (assertionMap.containsKey(AssertionEnum.NEXT_OF_KIN)) {
                    var assertionNextOfKin = assertionMap.get(AssertionEnum.NEXT_OF_KIN);
                    security.addChild(XMLUtils.toOM(assertionNextOfKin.getDOM()));
                }
                var assertionId = assertionMap.get(AssertionEnum.CLINICIAN);
                security.addChild(XMLUtils.toOM(assertionId.getDOM()));
                var assertionTreatment = assertionMap.get(AssertionEnum.TREATMENT);
                security.addChild(XMLUtils.toOM(assertionTreatment.getDOM()));
                _serviceClient.addHeader(security);
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }

            _serviceClient.addHeadersToEnvelope(env);

            /*
             * Prepare request
             */
            _messageContext.setEnvelope(env);   // set the message context with that soap envelope
            _operationClient.addMessageContext(_messageContext);    // add the message context to the operation client

            /* Log soap request */
            String logRequestBody;
            try {
                if (!org.apache.commons.lang3.StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                    String logRequestMsg = XMLUtil.prettyPrint(XMLUtils.toDOM(env));
                    loggerClinical.debug("{}\n{}", XCAConstants.LOG.OUTGOING_XCA_RETRIEVE_MESSAGE, logRequestMsg);
                }
                logRequestBody = XMLUtil.prettyPrint(XMLUtils.toDOM(env.getBody().getFirstElement()));
                // NRO
//                try {
//                    EvidenceUtils.createEvidenceREMNRO(envCanonicalized,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                            tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                            EventType.epsosOrderServiceRetrieve.getCode(),
//                            new DateTime(),
//                            EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
//                            "NCPB_XCA_RETRIEVE_REQ");
//                } catch (Exception e) {
//                    LOGGER.error(ExceptionUtils.getStackTrace(e));
//                }
            } catch (Exception ex) {
                throw new XCAException(getErrorCode(classCode), ex.getMessage(), null);
            }

            /* Validate Request Message */
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validateCrossCommunityAccess(logRequestBody, NcpSide.NCP_B, List.of(classCode));
            }

            /*
             * Execute Operation
             */
            transactionStartTime = new Date();
            SOAPEnvelope returnEnv;
            try {
                _operationClient.execute(true);
            } catch (AxisFault e) {
                LOGGER.error("Axis Fault error: '{}'", e.getMessage());
                LOGGER.error("Trying to automatically solve the problem by fetching configurations from the Central Services...");

                String endpoint = null;
                LOGGER.debug("ClassCode: " + classCode);
                DynamicDiscoveryService dynamicDiscoveryService = new DynamicDiscoveryService();
                switch (classCode) {
                    case PS_CLASSCODE:
                        endpoint = dynamicDiscoveryService.getEndpointUrl(
                                this.countryCode.toLowerCase(Locale.ENGLISH), RegisteredService.PATIENT_SERVICE, true);
                        break;
                    case EP_CLASSCODE:
                        endpoint = dynamicDiscoveryService.getEndpointUrl(
                                this.countryCode.toLowerCase(Locale.ENGLISH), RegisteredService.ORDER_SERVICE, true);
                        break;
                    default:
                        break;
                }
                if (StringUtils.isNotEmpty(endpoint)) {

                    /* if we get something from the Central Services, then we retry the request */
                    /* correctly sets the Transport information with the new endpoint */
                    LOGGER.debug("Retrying the request with the new configurations: [{}]", endpoint);
                    _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(endpoint));

                    /* we need a new OperationClient, otherwise we'll face the error "A message was added that is not valid. However, the operation context was complete." */
                    OperationClient newOperationClient = _serviceClient.createClient(_operations[1].getName());
                    newOperationClient.getOptions().setAction(XCAConstants.SOAP_HEADERS.RETRIEVE.REQUEST_ACTION);
                    newOperationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
                    addPropertyToOperationClient(newOperationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
                    addPropertyToOperationClient(newOperationClient, org.apache.axis2.Constants.Configuration.ENABLE_MTOM, org.apache.axis2.Constants.VALUE_TRUE);

                    SOAPFactory newSoapFactory = getFactory(newOperationClient.getOptions().getSoapVersionURI());

                    /* As there's no simple way of replacing the previously set WSA To header (still containing the old endpoint) we need to create a new SOAP
                    payload so that the wsa:To header is correctly set. Here we have 2 situations. If we rely on the client-connector axis2.xml configurations,
                    then the new endpoint will be copied from the Transport information to the wsa:To during the running of the Addressing Phase,
                    as defined by the global engagement of the addressing module in axis2.xml. But we're not doing it since these IHE service clients should be
                    decoupled from client-connector, thus we manually add the new WSA To header to the new SOAP envelope. In this specific case of the XCA Retrieve,
                    since the manual WSA headers are being added under the http://www.w3.org/2005/08/addressing/anonymous namespace and not under the usual http://www.w3.org/2005/08/addressing
                    namespace, both the manually added headers and the automatically added ones from client-connector's axis2 configurations will be added. This needs to be analysed.
                    See issues EHNCP-1141 and EHNCP-1168. */
                    SOAPEnvelope newEnv;
                    newEnv = toEnvelope(newSoapFactory,
                            retrieveDocumentSetRequest,
                            optimizeContent(new QName(XCAConstants.SOAP_HEADERS.NAMESPACE_URI, XCAConstants.SOAP_HEADERS.RETRIEVE.NAMESPACE_REQUEST_LOCAL_PART)));

                    /* Creating the new WSA To header with the new endpoint */
                    to = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("To", ns2);
                    node3 = newSoapFactory.createOMText(endpoint);
                    to.addChild(node3);
//                    to.addAttribute(att2);

                    /* There's no way to remove a single header (and WS-Addressing specification forbids the existence of
                    more than 1 addressing:To header), so we need to remove all the old headers and add them again to _serviceClient
                    (including the new To header with the right endpoint), from which they'll be copied into the final SOAP envelope */
                    _serviceClient.removeHeaders();
                    _serviceClient.addHeader(to);
                    _serviceClient.addHeader(action);
                    _serviceClient.addHeader(id);
                    _serviceClient.addHeader(replyTo);
                    _serviceClient.addHeader(security);
                    _serviceClient.addHeadersToEnvelope(newEnv);

                    /* we create a new Message Context with the new SOAP envelope */
                    MessageContext newMessageContext = new MessageContext();
                    newMessageContext.setEnvelope(newEnv);

                    /* add the new message context to the new operation client */
                    newOperationClient.addMessageContext(newMessageContext);
                    /* we retry the request */
                    newOperationClient.execute(true);
                    /* we need to reset the previous variables with the new content, to be used later */
                    _operationClient = newOperationClient;
                    _messageContext = newMessageContext;
                    env = newEnv;
                    LOGGER.debug("Successfully retried the request! Proceeding with the normal workflow...");
                } else {
                    /* if we cannot solve this issue through the Central Services, then there's nothing we can do, so we let it be thrown */
                    eadcError = "Could not find configurations in the Central Services for [" + endpoint + "], the service will fail.";
                    LOGGER.error(eadcError);
                    throw new XCAException(getErrorCode(classCode), e.getMessage(), null);
                }
            }
            _returnMessageContext = _operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            returnEnv = _returnMessageContext.getEnvelope();
            transactionEndTime = new Date();

            /* Log soap response */
            String logResponseBody;
            try {
                if (!org.apache.commons.lang3.StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                    String logResponseMsg = XMLUtil.prettyPrint(XMLUtils.toDOM(returnEnv));
                    loggerClinical.debug("{}\n{}", XCAConstants.LOG.INCOMING_XCA_RETRIEVE_MESSAGE, logResponseMsg);
                }
                logResponseBody = XMLUtil.prettyPrint(XMLUtils.toDOM(returnEnv.getBody().getFirstElement()));
            } catch (Exception ex) {
                throw new XCAException(getErrorCode(classCode), ex.getMessage(), null);
            }

            /* Validate Response Message */
            if (OpenNCPValidation.isValidationEnable()) {
                OpenNCPValidation.validateCrossCommunityAccess(logResponseBody, NcpSide.NCP_B, List.of(classCode));
            }

            /*
             * Return
             */
            RetrieveDocumentSetResponseType retrieveDocumentSetResponse;
            Object object = fromOM(returnEnv.getBody().getFirstElement(), RetrieveDocumentSetResponseType.class,
                    getEnvelopeNamespaces(returnEnv));
            retrieveDocumentSetResponse = (RetrieveDocumentSetResponseType) object;

            // NRR evidences optional.
            LOGGER.info("XCA Retrieve Request received. EVIDENCE NRR");

            // Invoke eADC
            if (retrieveDocumentSetResponse.getDocumentResponse() != null && !retrieveDocumentSetResponse.getDocumentResponse().isEmpty()) {

                cda = EadcUtilWrapper.toXmlDocument(retrieveDocumentSetResponse.getDocumentResponse().get(0).getDocument());
            }
            EadcUtilWrapper.invokeEadc(_messageContext, _returnMessageContext, this._getServiceClient(), cda,
                    transactionStartTime, transactionEndTime, this.countryCode, EadcEntry.DsTypes.EADC,
                    Direction.OUTBOUND, ServiceType.DOCUMENT_EXCHANGED_QUERY);

            //  Create Audit messages
            EventLog eventLog = createAndSendEventLogRetrieve(retrieveDocumentSetRequest, retrieveDocumentSetResponse,
                    _messageContext, returnEnv, env, assertionMap.get(AssertionEnum.CLINICIAN),
                    assertionMap.get(AssertionEnum.TREATMENT), this._getServiceClient().getOptions().getTo().getAddress(),
                    classCode);
            LOGGER.info("[Audit Service] Event Log '{}' sent to ATNA server", eventLog.getEventType());

            return retrieveDocumentSetResponse;

        } catch (AxisFault axisFault) {
            // TODO A.R. Audit log SOAP Fault is still missing
            OMElement faultElt = axisFault.getDetail();

            if (faultElt != null && faultExceptionNameMap.containsKey(faultElt.getQName())) {

                //make the fault by reflection
                try {
                    String exceptionClassName = (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                    Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                    Exception ex = (Exception) exceptionClass.getDeclaredConstructor().newInstance();
                    //message class
                    String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
                    Class messageClass = java.lang.Class.forName(messageClassName);
                    Object messageObject = fromOM(faultElt, messageClass, null);
                    Method m = exceptionClass.getMethod("setFaultMessage", messageClass);
                    m.invoke(ex, messageObject);
                    throw new java.rmi.RemoteException(ex.getMessage(), ex);

                } catch (Exception e) {
                    // Cannot instantiate the class - throw the original Axis fault
                    eadcError = e.getMessage();
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            eadcError = axisFault.getMessage();
            throw new XCAException(OpenNCPErrorCode.ERROR_GENERIC_CONNECTION_NOT_POSSIBLE, axisFault.getMessage(), null);
        } finally {
            if (_messageContext != null && _messageContext.getTransportOut() != null && _messageContext.getTransportOut().getSender() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
            if(!eadcError.isEmpty()) {
                EadcUtilWrapper.invokeEadcFailure(_messageContext, _returnMessageContext, this._getServiceClient(), cda,
                        transactionStartTime, transactionEndTime, this.countryCode, EadcEntry.DsTypes.EADC,
                        Direction.OUTBOUND, ServiceType.DOCUMENT_EXCHANGED_QUERY, eadcError);
            }
        }
    }

    /**
     * An utility method that copies the namespaces from the SOAPEnvelope
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

    private OMElement toOM(AdhocQueryRequest param, boolean optimizeContent) throws AxisFault {
        try {

            Marshaller marshaller = wsContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            OMFactory factory = OMAbstractFactory.getOMFactory();

            RespondingGateway_ServiceStub.JaxbRIDataSource source = new RespondingGateway_ServiceStub.JaxbRIDataSource(AdhocQueryRequest.class,
                    param, marshaller, XCAConstants.REGREP_QUERY, XCAConstants.ADHOC_QUERY_REQUEST);
            OMNamespace namespace = factory.createOMNamespace(XCAConstants.REGREP_QUERY, null);

            return factory.createOMElement(source, XCAConstants.ADHOC_QUERY_REQUEST, namespace);
        } catch (JAXBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    private SOAPEnvelope toEnvelope(SOAPFactory factory, AdhocQueryRequest param, boolean optimizeContent)
            throws AxisFault {

        SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(toOM(param, optimizeContent));
        return envelope;
    }

    private OMElement toOM(AdhocQueryResponse param, boolean optimizeContent)
            throws AxisFault {
        try {

            Marshaller marshaller = wsContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            OMFactory factory = OMAbstractFactory.getOMFactory();

            RespondingGateway_ServiceStub.JaxbRIDataSource source = new RespondingGateway_ServiceStub.JaxbRIDataSource(AdhocQueryResponse.class,
                    param,
                    marshaller,
                    XCAConstants.REGREP_QUERY,
                    XCAConstants.ADHOC_QUERY_RESPONSE);
            OMNamespace namespace = factory.createOMNamespace(XCAConstants.REGREP_QUERY,
                    null);
            return factory.createOMElement(source, XCAConstants.ADHOC_QUERY_RESPONSE, namespace);
        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    @SuppressWarnings("unused")
    private SOAPEnvelope toEnvelope(SOAPFactory factory, AdhocQueryResponse param, boolean optimizeContent)
            throws AxisFault {
        SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(toOM(param, optimizeContent));
        return envelope;
    }

    private OMElement toOM(RetrieveDocumentSetRequestType param, boolean optimizeContent) throws AxisFault {

        try {

            Marshaller marshaller = wsContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            OMFactory factory = OMAbstractFactory.getOMFactory();

            RespondingGateway_ServiceStub.JaxbRIDataSource source = new RespondingGateway_ServiceStub.JaxbRIDataSource(RetrieveDocumentSetRequestType.class,
                    param,
                    marshaller,
                    XCAConstants.SOAP_HEADERS.NAMESPACE_URI,
                    XCAConstants.RETRIEVE_DOCUMENT_SET_REQUEST);
            OMNamespace namespace = factory.createOMNamespace(XCAConstants.SOAP_HEADERS.NAMESPACE_URI,
                    null);
            return factory.createOMElement(source, XCAConstants.RETRIEVE_DOCUMENT_SET_REQUEST, namespace);
        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    private SOAPEnvelope toEnvelope(SOAPFactory factory, RetrieveDocumentSetRequestType param, boolean optimizeContent)
            throws AxisFault {

        SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(toOM(param, optimizeContent));
        return envelope;
    }

    private OMElement toOM(RetrieveDocumentSetResponseType param, boolean optimizeContent) throws AxisFault {

        try {
            Marshaller marshaller = wsContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            OMFactory factory = OMAbstractFactory.getOMFactory();
            RespondingGateway_ServiceStub.JaxbRIDataSource source = new RespondingGateway_ServiceStub.JaxbRIDataSource(
                    RetrieveDocumentSetResponseType.class, param, marshaller, XCAConstants.SOAP_HEADERS.NAMESPACE_URI,
                    XCAConstants.RETRIEVE_DOCUMENT_SET_RESPONSE);
            OMNamespace namespace = factory.createOMNamespace(XCAConstants.SOAP_HEADERS.NAMESPACE_URI, null);

            return factory.createOMElement(source, XCAConstants.RETRIEVE_DOCUMENT_SET_RESPONSE, namespace);

        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    @SuppressWarnings("unused")
    private SOAPEnvelope toEnvelope(SOAPFactory factory, RetrieveDocumentSetResponseType param, boolean optimizeContent)
            throws AxisFault {

        SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(toOM(param, optimizeContent));
        return envelope;
    }

    /**
     * get the default envelope
     */
    @SuppressWarnings("unused")
    private SOAPEnvelope toEnvelope(SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    @SuppressWarnings("unchecked")
    private Object fromOM(OMElement param, Class type, Map extraNamespaces) throws AxisFault {

        try {

            Unmarshaller unmarshaller = wsContext.createUnmarshaller();
            return unmarshaller.unmarshal(param.getXMLStreamReaderWithoutCaching(), type).getValue();

        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    private EventLog createAndSendEventLogQuery(AdhocQueryRequest request, AdhocQueryResponse response, MessageContext msgContext,
                                                SOAPEnvelope _returnEnv, SOAPEnvelope env, Assertion idAssertion, Assertion trcAssertion,
                                                String address, ClassCode classCode) {

        EventLog eventLog = EventLogClientUtil.prepareEventLog(msgContext, _returnEnv, address);
        EventLogClientUtil.logIdAssertion(eventLog, idAssertion);
        EventLogClientUtil.logTrcAssertion(eventLog, trcAssertion);
        EventLogUtil.prepareXCACommonLogQuery(eventLog, request, response, classCode);
        eventLog.setNcpSide(NcpSide.NCP_B);
        EventLogClientUtil.sendEventLog(eventLog);

        return eventLog;
    }

    private EventLog createAndSendEventLogRetrieve(RetrieveDocumentSetRequestType request, RetrieveDocumentSetResponseType response,
                                                   MessageContext msgContext, SOAPEnvelope _returnEnv, SOAPEnvelope env,
                                                   Assertion idAssertion, Assertion trcAssertion, String address, ClassCode classCode) {

        EventLog eventLog = EventLogClientUtil.prepareEventLog(msgContext, _returnEnv, address);
        EventLogClientUtil.logIdAssertion(eventLog, idAssertion);
        EventLogClientUtil.logTrcAssertion(eventLog, trcAssertion);
        EventLogUtil.prepareXCACommonLogRetrieve(eventLog, request, response, classCode);
        eventLog.setNcpSide(NcpSide.NCP_B);
        EventLogClientUtil.sendEventLog(eventLog);

        return eventLog;
    }
    public OpenNCPErrorCode getErrorCode(ClassCode classCode) {
        switch (classCode) {
            case PS_CLASSCODE:
                return OpenNCPErrorCode.ERROR_PS_GENERIC;
            case EP_CLASSCODE:
                return OpenNCPErrorCode.ERROR_EP_GENERIC;
            case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
            case ORCD_LABORATORY_RESULTS_CLASSCODE:
            case ORCD_MEDICAL_IMAGES_CLASSCODE:
            case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                return OpenNCPErrorCode.ERROR_ORCD_GENERIC;
        }

        return OpenNCPErrorCode.ERROR_GENERIC;
    }

    /**
     * Inner JAXBRIDataSource class
     */
    class JaxbRIDataSource implements OMDataSource {

        /**
         * Bound object for output.
         */
        private final Object outObject;
        /**
         * Bound class for output.
         */
        @SuppressWarnings("unused")
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

        @SuppressWarnings("unchecked")
        public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
            try {
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), output);
            } catch (JAXBException e) {
                throw new XMLStreamException(XCAConstants.EXCEPTIONS.ERROR_JAXB_MARSHALLING, e);
            }
        }

        @SuppressWarnings("unchecked")
        public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {

            try {
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), writer);
            } catch (JAXBException e) {
                throw new XMLStreamException(XCAConstants.EXCEPTIONS.ERROR_JAXB_MARSHALLING, e);
            }
        }

        @SuppressWarnings("unchecked")
        public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {

            try {
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), xmlWriter);
            } catch (JAXBException e) {
                throw new XMLStreamException(XCAConstants.EXCEPTIONS.ERROR_JAXB_MARSHALLING, e);
            }
        }

        @SuppressWarnings("unchecked")
        public XMLStreamReader getReader() throws XMLStreamException {

            try {
                OMDocument omDocument = OMAbstractFactory.getOMFactory().createOMDocument();
                wsContext.createMarshaller().marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject),
                        omDocument.getSAXResult());

                return omDocument.getOMDocumentElement().getXMLStreamReader();
            } catch (JAXBException e) {
                throw new XMLStreamException(XCAConstants.EXCEPTIONS.ERROR_JAXB_MARSHALLING, e);
            }
        }
    }
}
