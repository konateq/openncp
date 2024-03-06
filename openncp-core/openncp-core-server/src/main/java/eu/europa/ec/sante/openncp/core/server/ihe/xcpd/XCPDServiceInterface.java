package eu.europa.ec.sante.openncp.core.server.ihe.xcpd;

import eu.europa.ec.sante.openncp.common.audit.EventLog;
import net.ihe.gazelle.hl7v3.prpain201305UV02.PRPAIN201305UV02Type;

import net.ihe.gazelle.hl7v3.prpain201306UV02.PRPAIN201306UV02Type;
import org.apache.axiom.soap.SOAPHeader;

public interface XCPDServiceInterface {

    PRPAIN201306UV02Type queryPatient(PRPAIN201305UV02Type request, SOAPHeader soapHeader, EventLog eventLog) throws Exception;
}
