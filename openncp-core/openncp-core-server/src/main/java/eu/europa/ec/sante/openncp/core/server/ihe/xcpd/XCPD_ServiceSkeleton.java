package eu.europa.ec.sante.openncp.core.server.ihe.xcpd;


import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.org.hl7.v3.PRPAIN201305UV02;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.org.hl7.v3.PRPAIN201306UV02;
import org.apache.axiom.soap.SOAPHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * XCPD_ServiceSkeleton java skeleton for the axisService
 */
@Service("xcpdServiceSkeleton")
public class XCPD_ServiceSkeleton {

    @Autowired
    private XCPDServiceInterface xcpdServiceInterface;

    public PRPAIN201306UV02 respondingGateway_PRPA_IN201305UV02(PRPAIN201305UV02 request, SOAPHeader header, EventLog event)
            throws Exception {
        return xcpdServiceInterface.queryPatient(request, header, event);
    }
}
