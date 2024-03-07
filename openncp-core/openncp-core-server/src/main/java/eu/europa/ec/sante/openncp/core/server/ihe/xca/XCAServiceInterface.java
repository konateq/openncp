package eu.europa.ec.sante.openncp.core.server.ihe.xca;

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
