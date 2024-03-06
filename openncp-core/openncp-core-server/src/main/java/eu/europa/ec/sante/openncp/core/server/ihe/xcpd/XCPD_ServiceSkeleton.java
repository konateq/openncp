package eu.europa.ec.sante.openncp.core.server.ihe.xcpd;


import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.common.org.hl7.v3.PRPAIN201305UV02;
import eu.europa.ec.sante.openncp.core.common.org.hl7.v3.PRPAIN201306UV02;
import org.apache.axiom.soap.SOAPHeader;

/**
 * XCPD_ServiceSkeleton java skeleton for the axisService
 */
public class XCPD_ServiceSkeleton {

    private XCPDServiceInterface service = null;

    public XCPD_ServiceSkeleton() {
    }

    /**
     * Auto generated method signature
     *
     * @param request
     * @param header
     * @param event
     */

    public PRPAIN201306UV02 respondingGateway_PRPA_IN201305UV02(PRPAIN201305UV02 request, SOAPHeader header, EventLog event)
            throws Exception {

        if (service == null) {
            service = new XCPDServiceImpl();
        }

        return service.queryPatient(request, header, event);
    }
}
