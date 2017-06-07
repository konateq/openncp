/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.affecto.epsos.ws.handler;

import epsos.ccd.gnomon.auditmanager.EventOutcomeIndicator;
import eu.epsos.util.EvidenceUtils;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.handlers.AbstractHandler;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * OutFlowEvidenceEmitter
 * Generates all NROs
 * Currently supporting the generation of evidences in the following cases:
 * NCP-B sends to NCP-A
 * NCP-A replies to NCP-B (left commented as the Evidence Emitter CP does not mandate generation of evidences on the response)
 * NCP-B replies to Portal (left commented as the Evidence Emitter CP does not mandate generation of evidences on the response)
 *
 * @author jgoncalves
 */
public class OutFlowEvidenceEmitterHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OutFlowEvidenceEmitterHandler.class);

    private EvidenceEmitterHandlerUtils evidenceEmitterHandlerUtils;

    @Override
    public Handler.InvocationResponse invoke(MessageContext msgcontext) throws AxisFault {
        LOG.debug("OutFlow Evidence Emitter handler is executing");
        this.evidenceEmitterHandlerUtils = new EvidenceEmitterHandlerUtils();

        /* I'll leave this here as it might be useful in the future */

//        SOAPHeader soapHeader = msgcontext.getEnvelope().getHeader();
//        if (soapHeader != null) {
//            Iterator<?> blocks = soapHeader.examineAllHeaderBlocks();
//            LOG.debug("Iterating over soap headers");
//            while (blocks.hasNext()) {
//                LOG.debug("Processing header");
//                SOAPHeaderBlock block = (SOAPHeaderBlock)blocks.next();
//                LOG.debug(block.toString());
//                block.setProcessed();
//            }
//        }

//        LOG.debug("LOGGING TEST VALUES");
//        LOG.debug("MessageContext properties: " + msgcontext.getProperties());
//        LOG.debug("MessageContext messageID: " + msgcontext.getMessageID());
//        
//        SessionContext sessionCtx = msgcontext.getSessionContext();
//        if (sessionCtx != null) {
//            LOG.debug("SessionContext CookieID: " + sessionCtx.getCookieID());
//        } else {
//            LOG.debug("SessionContext is null!");
//        }

//        OperationContext operationCtx = msgcontext.getOperationContext();
//        if (operationCtx != null) {
//            LOG.debug("OperationContext operationName: " + operationCtx.getOperationName());
//            LOG.debug("OperationContext serviceGroupName: " + operationCtx.getServiceGroupName());
//            LOG.debug("OperationContext serviceName; " + operationCtx.getServiceName());
//            LOG.debug("OperationContext isComplete: " + operationCtx.isComplete());
//        } else {
//            LOG.debug("OperationContext is null!");
//        }

//        ServiceGroupContext serviceGroupCtx = msgcontext.getServiceGroupContext();
//        if (serviceGroupCtx != null) {
//            LOG.debug("ServiceGroupContext ID: " + serviceGroupCtx.getId());
//            AxisServiceGroup axisServiceGroup = serviceGroupCtx.getDescription();
//            Iterator<AxisService> itAxisService = axisServiceGroup.getServices();
//            while (itAxisService.hasNext()) {
//                AxisService axisService = itAxisService.next();
//                LOG.debug("AxisService BindingName: " + axisService.getBindingName());
//                LOG.debug("AxisService CustomSchemaNamePrefix: " + axisService.getCustomSchemaNamePrefix());
//                LOG.debug("AxisService CustomSchemaNameSuffix: " + axisService.getCustomSchemaNameSuffix());
//                LOG.debug("AxisService endpointName: " + axisService.getEndpointName());
//                Map<String,AxisEndpoint> axisEndpoints = axisService.getEndpoints();
//                for (String key : axisEndpoints.keySet()) {
//                    AxisEndpoint axisEndpoint = axisEndpoints.get(key);
//                    LOG.debug("AxisEndpoint calculatedEndpointURL: " + axisEndpoint.calculateEndpointURL());
//                    LOG.debug("AxisEndpoint alias: " + axisEndpoint.getAlias());
//                    LOG.debug("AxisEndpoint endpointURL: " + axisEndpoint.getEndpointURL());
//                    LOG.debug("AxisEndpoint active: " + axisEndpoint.isActive());
//                }
//                LOG.debug("AxisService EPRs: " + Arrays.toString((String[]) axisService.getEPRs()));
//                LOG.debug("AxisService name: " + axisService.getName());
//                LOG.debug("AxisService isClientSide: " + axisService.isClientSide());
//            } 
//        } else {
//            LOG.debug("ServiceGroupContext is null!");
//        }

//        ConfigurationContext configCtx = msgcontext.getRootContext();
//        if (configCtx != null) {
//            LOG.debug("ConfigurationContext contextRoot: " + configCtx.getContextRoot());
//            LOG.debug("ConfigurationContext serviceGroupContextIDs: " + Arrays.toString((String[])configCtx.getServiceGroupContextIDs()));
//            LOG.debug("ConfigurationContext servicePath: " + configCtx.getServicePath());
//        } else {
//            LOG.debug("ConfigurationContext is null!");
//        }

        try {
            /* Canonicalizing the full SOAP message */
            Document envCanonicalized = this.evidenceEmitterHandlerUtils.canonicalizeAxiomSoapEnvelope(msgcontext.getEnvelope());

            SOAPHeader soapHeader = msgcontext.getEnvelope().getHeader();
            SOAPBody soapBody = msgcontext.getEnvelope().getBody();
            String eventType = null;
            String title = null;
            String msgUUID = null;
            AxisService axisService = msgcontext.getServiceContext().getAxisService();
            boolean isClientSide = axisService.isClientSide();
            LOG.debug("AxisService name: " + axisService.getName());
            LOG.debug("AxisService isClientSide: " + isClientSide);
            if (isClientSide) {
                /* NCP-B sends to NCP-A, e.g.: 
                    NRO
                    title = "NCPB_XCPD_REQ"
                    eventType = ihe event 
                */
                eventType = this.evidenceEmitterHandlerUtils.getEventTypeFromMessage(soapBody);
                title = "NCPB_" + this.evidenceEmitterHandlerUtils.getTransactionNameFromMessage(soapBody);
                //msgUUID = null; It stays as null because it's fetched from soap msg
                LOG.debug("eventType: " + eventType);
                LOG.debug("title: " + title);

                EvidenceUtils.createEvidenceREMNRO(envCanonicalized,
                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
                        tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
                        tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
                        tr.com.srdc.epsos.util.Constants.SC_KEYSTORE_PATH,
                        tr.com.srdc.epsos.util.Constants.SC_KEYSTORE_PASSWORD,
                        tr.com.srdc.epsos.util.Constants.SC_PRIVATEKEY_ALIAS,
                        tr.com.srdc.epsos.util.Constants.SP_KEYSTORE_PATH,
                        tr.com.srdc.epsos.util.Constants.SP_KEYSTORE_PASSWORD,
                        tr.com.srdc.epsos.util.Constants.SP_PRIVATEKEY_ALIAS,
                        eventType,
                        new DateTime(),
                        EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
                        title);
            } else {
                /* NCP-A replies to NCP-B, e.g.: 
                    NRO
                    title = "NCPA_XCPD_RES"
                    eventType = ihe event
                NCP-B replies to Portal, e.g.: 
                    NRO
                    title = "NCPB_PD_RES_SENT"
                    eventType = "NCPB_PD_RES"
                    msguuid = random
                */
                /* Joao: as per the CP, evidence generation on the way back is optional,
                so I leave it commented. If in the future it's decided that is mandatory,
                just uncomment.
                */
//                eventType = this.evidenceEmitterHandlerUtils.getEventTypeFromMessage(soapBody);
//                title = this.evidenceEmitterHandlerUtils.getServerSideTitle(soapBody);
//                msgUUID = this.evidenceEmitterHandlerUtils.getMsgUUID(soapHeader, soapBody);
//                LOG.debug("eventType: " + eventType);
//                LOG.debug("title: " + title);
//                LOG.debug("msgUUID: " + msgUUID);
//                
//                if (msgUUID != null) {
//                    // this is a Portal-NCPB interaction: msgUUID comes from IdA or is random
//                    EvidenceUtils.createEvidenceREMNRO(envCanonicalized,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                                tr.com.srdc.epsos.util.Constants.SC_KEYSTORE_PATH,
//                                tr.com.srdc.epsos.util.Constants.SC_KEYSTORE_PASSWORD,
//                                tr.com.srdc.epsos.util.Constants.SC_PRIVATEKEY_ALIAS,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                                eventType,
//                                new DateTime(),
//                                EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
//                                title,
//                                msgUUID);
//                } else {
//                    // this isn't a Portal-NCPB interaction (it's NCPB-NCPA), so msgUUID is retrieved from the soap header
//                    EvidenceUtils.createEvidenceREMNRO(envCanonicalized,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PATH,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_KEYSTORE_PASSWORD,
//                                tr.com.srdc.epsos.util.Constants.NCP_SIG_PRIVATEKEY_ALIAS,
//                                tr.com.srdc.epsos.util.Constants.SP_KEYSTORE_PATH,
//                                tr.com.srdc.epsos.util.Constants.SP_KEYSTORE_PASSWORD,
//                                tr.com.srdc.epsos.util.Constants.SP_PRIVATEKEY_ALIAS,
//                                tr.com.srdc.epsos.util.Constants.SC_KEYSTORE_PATH,
//                                tr.com.srdc.epsos.util.Constants.SC_KEYSTORE_PASSWORD,
//                                tr.com.srdc.epsos.util.Constants.SC_PRIVATEKEY_ALIAS,
//                                eventType,
//                                new DateTime(),
//                                EventOutcomeIndicator.FULL_SUCCESS.getCode().toString(),
//                                title);        
//                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Handler.InvocationResponse.CONTINUE;
    }
}