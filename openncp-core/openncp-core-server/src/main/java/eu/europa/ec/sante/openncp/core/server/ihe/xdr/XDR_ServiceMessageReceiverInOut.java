package eu.europa.ec.sante.openncp.core.server.ihe.xdr;


import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.audit.AuditService;
import eu.europa.ec.sante.openncp.common.audit.AuditServiceFactory;
import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.common.configuration.util.OpenNCPConstants;
import eu.europa.ec.sante.openncp.common.configuration.util.ServerMode;
import eu.europa.ec.sante.openncp.common.util.XMLUtil;
import eu.europa.ec.sante.openncp.common.validation.OpenNCPValidation;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.rs._3.RegistryResponseType;
import eu.europa.ec.sante.openncp.core.common.ihe.eadc.EadcEntry;
import eu.europa.ec.sante.openncp.core.common.ihe.eadc.EadcUtil;
import eu.europa.ec.sante.openncp.core.common.ihe.eadc.EadcUtilWrapper;
import eu.europa.ec.sante.openncp.core.common.ihe.eadc.ServiceType;
import eu.europa.ec.sante.openncp.core.common.ihe.util.EventLogUtil;
import org.apache.axiom.om.*;
import org.apache.axiom.om.ds.AbstractOMDataSource;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.receivers.AbstractInOutMessageReceiver;
import org.apache.axis2.util.JavaUtils;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

/**
 * XDR_ServiceMessageReceiverInOut message receiver
 */
public class XDR_ServiceMessageReceiverInOut extends AbstractInOutMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(XDR_ServiceMessageReceiverInOut.class);
    private static final JAXBContext wsContext;

    static {

        LOGGER.debug("[XDR Services] Loading the WS-Security init libraries in XDR 2007");
        org.apache.xml.security.Init.init();
    }

    static {

        JAXBContext jaxbContext = null;

        try {
            jaxbContext = JAXBContext.newInstance(ProvideAndRegisterDocumentSetRequestType.class,
                    RegistryResponseType.class);
        } catch (JAXBException ex) {
            LOGGER.error("Unable to create JAXBContext: '{}'", ex.getMessage(), ex);
            Runtime.getRuntime().exit(-1);
        } finally {
            wsContext = jaxbContext;
        }
    }

    private final Logger loggerClinical = LoggerFactory.getLogger("LOGGER_CLINICAL");

    private String getMessageID(SOAPEnvelope envelope) {

        Iterator<OMElement> it = envelope.getHeader().getChildrenWithName(new QName(AddressingConstants.Final.WSA_NAMESPACE,
                AddressingConstants.WSA_MESSAGE_ID));
        if (it.hasNext()) {
            return it.next().getText();
        } else {
            return Constants.UUID_PREFIX;
        }
    }

    /**
     * Axis2 method invoking web service and business logic related to XDR IHE Profile.
     *
     * @param msgContext     - SOAP MessageContext request.
     * @param newMsgContext- SOAP MessageContext response.
     * @throws AxisFault - Exception returned during the process.
     */
    public void invokeBusinessLogic(MessageContext msgContext, MessageContext newMsgContext) throws AxisFault {

        String eadcError = "";

        Date startTime = new Date();
        Date endTime = new Date();

        Document eDispenseCda = null;

        // Out Envelop
        SOAPEnvelope envelope;

        try {
            // get the implementation class for the Web Service
            Object serviceObject = getTheImplementationObject(msgContext);
            XDR_ServiceSkeleton xdrServiceSkeleton = (XDR_ServiceSkeleton) serviceObject;

            // Find the axisOperation that has been set by the Dispatch phase.
            AxisOperation axisOperation = msgContext.getOperationContext().getAxisOperation();

            if (axisOperation == null) {
                String err = "Operation is not located, if this is Doc/lit style the SOAP-ACTION should specified via the " +
                        "SOAP Action to use the RawXMLProvider";

                eadcError = err;

                throw new AxisFault(err);
            }

            String randomUUID = Constants.UUID_PREFIX + UUID.randomUUID();
            String methodName;

            if ((axisOperation.getName() != null) && ((methodName = JavaUtils.xmlNameToJavaIdentifier(axisOperation.getName().getLocalPart())) != null)) {

                SOAPHeader soapHeader = msgContext.getEnvelope().getHeader();
                //  Identification of the TLS Common Name of the client.
                String clientCommonName = EventLogUtil.getClientCommonName(msgContext);
                LOGGER.info("[ITI-41] Incoming XDR Request from '{}'", clientCommonName);

                EventLog eventLog = new EventLog();
                eventLog.setReqM_ParticipantObjectID(getMessageID(msgContext.getEnvelope()));
                eventLog.setReqM_ParticipantObjectDetail(msgContext.getEnvelope().getHeader().toString().getBytes());
                eventLog.setSC_UserID(clientCommonName);
                eventLog.setSourceip(EventLogUtil.getSourceGatewayIdentifier(msgContext));
                eventLog.setTargetip(EventLogUtil.getTargetGatewayIdentifier());

                if (loggerClinical.isDebugEnabled() && !StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                    loggerClinical.debug("Incoming XDR Request Message:\n{}", XMLUtil.prettyPrint(XMLUtils.toDOM(msgContext.getEnvelope())));
                }

                if (StringUtils.equals("documentRecipient_ProvideAndRegisterDocumentSetB", methodName)) {

                    /* Validate incoming request */
                    String requestMessage = XMLUtil.prettyPrint(XMLUtils.toDOM(msgContext.getEnvelope().getBody().getFirstElement()));
                    if (OpenNCPValidation.isValidationEnable()) {
                        OpenNCPValidation.validateXDRMessage(requestMessage, NcpSide.NCP_A, null);
                    }
                    ProvideAndRegisterDocumentSetRequestType wrappedParam = (ProvideAndRegisterDocumentSetRequestType) fromOM(
                            msgContext.getEnvelope().getBody().getFirstElement(),
                            ProvideAndRegisterDocumentSetRequestType.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    RegistryResponseType registryResponse = xdrServiceSkeleton.documentRecipient_ProvideAndRegisterDocumentSetB(wrappedParam, soapHeader, eventLog);

                    envelope = toEnvelope(getSOAPFactory(msgContext), registryResponse);

                    eventLog.setResM_ParticipantObjectID(randomUUID);
                    eventLog.setResM_ParticipantObjectDetail(envelope.getHeader().toString().getBytes());
                    eventLog.setNcpSide(NcpSide.NCP_A);
                    eventLog.setQueryByParameter(" ");
                    eventLog.setHciIdentifier(" ");

                    EventLogUtil.extractQueryByParamFromHeader(eventLog, msgContext, "PRPA_IN201305UV02", "controlActProcess", "queryByParameter");
                    EventLogUtil.extractHCIIdentifierFromHeader(eventLog, msgContext);

                    AuditService auditService = AuditServiceFactory.getInstance();
                    auditService.write(eventLog, "", "1");

                    /* Validate outgoing response */
                    String responseMessage = XMLUtil.prettyPrint(XMLUtils.toDOM(envelope.getBody().getFirstElement()));
                    if (OpenNCPValidation.isValidationEnable()) {
                        OpenNCPValidation.validateXDRMessage(responseMessage, NcpSide.NCP_A, null);
                    }
                    if (loggerClinical.isDebugEnabled() && !StringUtils.equals(System.getProperty(OpenNCPConstants.SERVER_EHEALTH_MODE), ServerMode.PRODUCTION.name())) {
                        loggerClinical.debug("Response Header:\n{}", envelope.getHeader());
                        loggerClinical.debug("Outgoing XDR Response Message:\n{}", XMLUtil.prettyPrint(XMLUtils.toDOM(envelope)));
                    }
                    // eADC: extract of the eDispense CDA required by the KPIs.
                    eDispenseCda = EadcUtilWrapper.toXmlDocument(wrappedParam.getDocument().get(0).getValue());

                } else {
                    String err = "Method not found: '" + methodName + "'";
                    LOGGER.error(err);

                    eadcError = err;

                    throw new RuntimeException(err);
                }

                endTime = new Date();
                newMsgContext.setEnvelope(envelope);
                newMsgContext.getOptions().setMessageId(randomUUID);

                if(!EadcUtilWrapper.hasTransactionErrors(envelope)) {
                    EadcUtilWrapper.invokeEadc(msgContext, newMsgContext, null, eDispenseCda, startTime,
                            endTime, Constants.COUNTRY_CODE, EadcEntry.DsTypes.EADC, EadcUtil.Direction.INBOUND,
                            ServiceType.DOCUMENT_EXCHANGED_RESPONSE);
                } else {
                    eadcError = EadcUtilWrapper.getTransactionErrorDescription(envelope);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            eadcError = e.getMessage();

            throw AxisFault.makeFault(e);
        } finally {
            if(!eadcError.isEmpty()) {
                EadcUtilWrapper.invokeEadcFailure(msgContext, newMsgContext, null, eDispenseCda, startTime, endTime,
                        Constants.COUNTRY_CODE, EadcEntry.DsTypes.EADC, EadcUtil.Direction.INBOUND,
                        ServiceType.DOCUMENT_EXCHANGED_RESPONSE, eadcError);
            }
        }

    }

    private OMElement toOM(RegistryResponseType param) throws AxisFault {

        try {
            OMFactory factory = OMAbstractFactory.getOMFactory();
            Marshaller marshaller = wsContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            JaxbRIDataSource source = new JaxbRIDataSource(RegistryResponseType.class, param, marshaller,
                    "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryResponse");
            OMNamespace namespace = factory.createOMNamespace("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", null);

            return factory.createOMElement(source, "RegistryResponse", namespace);

        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    private SOAPEnvelope toEnvelope(SOAPFactory factory, RegistryResponseType param) throws AxisFault {

        SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(toOM(param));

        return envelope;
    }

    /**
     * Returns default SOAP envelope.
     */
    private SOAPEnvelope toEnvelope(SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private Object fromOM(OMElement param, Class type, Map extraNamespaces) throws AxisFault {

        try {

            Unmarshaller unmarshaller = wsContext.createUnmarshaller();

            return unmarshaller.unmarshal(param.getXMLStreamReaderWithoutCaching(), type).getValue();

        } catch (JAXBException bex) {
            throw AxisFault.makeFault(bex);
        }
    }

    /**
     * A utility method that copies the namespaces from the SOAPEnvelope.
     */
    private Map getEnvelopeNamespaces(SOAPEnvelope env) {

        Map returnMap = new HashMap();
        Iterator namespaceIterator = env.getAllDeclaredNamespaces();

        while (namespaceIterator.hasNext()) {

            OMNamespace ns = (OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return returnMap;
    }

    private AxisFault createAxisFault(Exception e) {

        AxisFault axisFault;
        Throwable cause = e.getCause();
        if (cause != null) {
            axisFault = new AxisFault(e.getMessage(), cause);
        } else {
            axisFault = new AxisFault(e.getMessage());
        }

        return axisFault;
    }

    class JaxbRIDataSource extends AbstractOMDataSource {

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

        public void serialize(OutputStream output) throws XMLStreamException {

            try {
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), output);

            } catch (JAXBException e) {
                throw new XMLStreamException("Error in JAXB marshalling", e);
            }
        }

        public void serialize(Writer writer) throws XMLStreamException {
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

                OMDocument omDocument = OMAbstractFactory.getOMFactory().createOMDocument();
                Marshaller marshaller = wsContext.createMarshaller();
                marshaller.marshal(new JAXBElement(new QName(nsuri, name), outObject.getClass(), outObject), omDocument.getSAXResult());

                return omDocument.getOMDocumentElement().getXMLStreamReader();

            } catch (JAXBException e) {
                throw new XMLStreamException("Error in JAXB marshalling", e);
            }
        }

        @Override
        public boolean isDestructiveRead() {
            return false;
        }

        @Override
        public boolean isDestructiveWrite() {
            return false;
        }
    }
}
