package eu.europa.ec.sante.openncp.core.server.ihe.xca;

import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.query._3.AdhocQueryRequest;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.query._3.AdhocQueryResponse;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;

public interface XCAServiceInterface {

    /**
     * @param request
     * @param soapHeader
     * @param eventLog
     * @return
     * @throws Exception
     */
    AdhocQueryResponse queryDocument(AdhocQueryRequest request, SOAPHeader soapHeader, EventLog eventLog) throws Exception;

    /**
     * @param request
     * @param soapHeader
     * @param eventLog
     * @param response
     * @throws Exception
     */
    void retrieveDocument(RetrieveDocumentSetRequestType request, SOAPHeader soapHeader, EventLog eventLog, OMElement response) throws Exception;
}
