package eu.europa.ec.sante.openncp.core.server.ihe.xdr;

import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.common.ihe.XDRServiceInterface;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.rs._3.RegistryResponseType;
import eu.europa.ec.sante.openncp.core.server.ihe.xdr.impl.XDRServiceImpl;
import org.apache.axiom.soap.SOAPHeader;

/**
 * XDR_ServiceSkeleton java skeleton for the axisService
 */
public class XDR_ServiceSkeleton {

    private XDRServiceInterface service = null;

    public XDR_ServiceSkeleton() {
    }

    /**
     * Auto generated method signature
     *
     * @param provideAndRegisterDocumentSetRequest
     */
    public RegistryResponseType documentRecipient_ProvideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType provideAndRegisterDocumentSetRequest,
                                                                                 SOAPHeader soapHeader, EventLog eventLog) throws Exception {

        if (service == null) {
            service = new XDRServiceImpl();
        }

        return service.saveDocument(provideAndRegisterDocumentSetRequest, soapHeader, eventLog);
    }
}
