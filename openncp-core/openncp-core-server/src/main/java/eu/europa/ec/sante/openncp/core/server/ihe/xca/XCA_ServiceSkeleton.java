package eu.europa.ec.sante.openncp.core.server.ihe.xca;

import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.query._3.AdhocQueryRequest;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.query._3.AdhocQueryResponse;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * XCA_ServiceSkeleton java skeleton for the axisService
 */
public class XCA_ServiceSkeleton extends SpringBeanAutowiringSupport {

    @Autowired
    private XCAServiceInterface service;

    public AdhocQueryResponse respondingGateway_CrossGatewayQuery(AdhocQueryRequest adhocQueryRequest, SOAPHeader sh,
                                                                  EventLog eventLog) throws Exception {
        return service.queryDocument(adhocQueryRequest, sh, eventLog);
    }

    /**
     * Auto generated method signature
     *
     * @param retrieveDocumentSetRequest
     */
    public void respondingGateway_CrossGatewayRetrieve(RetrieveDocumentSetRequestType retrieveDocumentSetRequest,
                                                       SOAPHeader soapHeader, EventLog eventLog, OMElement omElement) throws Exception {
        service.retrieveDocument(retrieveDocumentSetRequest, soapHeader, eventLog, omElement);
    }
}
