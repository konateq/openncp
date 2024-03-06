package eu.europa.ec.sante.openncp.core.common;

import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.rs._3.RegistryResponseType;
import org.apache.axiom.soap.SOAPHeader;

public interface XDRServiceInterface {

    /**
     * @param request
     * @param soapHeader
     * @param eventLog
     * @return
     * @throws Exception
     */
    RegistryResponseType saveDocument(ProvideAndRegisterDocumentSetRequestType request, SOAPHeader soapHeader, EventLog eventLog) throws Exception;
}
