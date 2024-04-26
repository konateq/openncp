package eu.europa.ec.sante.openncp.core.server.ihe.xca;

import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.query._3.AdhocQueryRequest;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.query._3.AdhocQueryResponse;
import eu.europa.ec.sante.openncp.core.server.ihe.xca.impl.XCAServiceImpl;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;

/**
 * XCA_ServiceSkeleton java skeleton for the axisService
 */
public class XCA_ServiceSkeleton {

    /**
     * Auto generated method signature
     *
     * @param adhocQueryRequest
     */
    private XCAServiceInterface service = null;

    public XCA_ServiceSkeleton() {
    }

    public AdhocQueryResponse respondingGateway_CrossGatewayQuery(AdhocQueryRequest adhocQueryRequest, SOAPHeader sh,
                                                                  EventLog eventLog) throws Exception {

        if (service == null) {
            service = new XCAServiceImpl();
        }
        return service.queryDocument(adhocQueryRequest, sh, eventLog);
    }

    /**
     * Auto generated method signature
     *
     * @param retrieveDocumentSetRequest
     */
    public void respondingGateway_CrossGatewayRetrieve(RetrieveDocumentSetRequestType retrieveDocumentSetRequest,
                                                       SOAPHeader soapHeader, EventLog eventLog, OMElement omElement) throws Exception {

        if (service == null) {
            service = new XCAServiceImpl();
        }
        service.retrieveDocument(retrieveDocumentSetRequest, soapHeader, eventLog, omElement);
    }
}
