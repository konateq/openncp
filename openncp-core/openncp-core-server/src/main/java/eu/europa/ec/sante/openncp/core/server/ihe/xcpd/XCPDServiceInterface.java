package eu.europa.ec.sante.openncp.core.server.ihe.xcpd;

import eu.europa.ec.sante.openncp.common.audit.EventLog;

import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.org.hl7.v3.PRPAIN201305UV02;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.org.hl7.v3.PRPAIN201306UV02;
import org.apache.axiom.soap.SOAPHeader;

public interface XCPDServiceInterface {

    PRPAIN201306UV02 queryPatient(PRPAIN201305UV02 request, SOAPHeader soapHeader, EventLog eventLog) throws Exception;
}
