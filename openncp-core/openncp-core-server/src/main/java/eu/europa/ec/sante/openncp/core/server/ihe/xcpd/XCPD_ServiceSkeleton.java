package eu.europa.ec.sante.openncp.core.server.ihe.xcpd;


import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.core.server.ihe.xcpd.impl.XCPDServiceImpl;
import net.ihe.gazelle.hl7v3.prpain201305UV02.PRPAIN201305UV02Type;
import net.ihe.gazelle.hl7v3.prpain201306UV02.PRPAIN201306UV02Type;
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

    public PRPAIN201306UV02Type respondingGateway_PRPA_IN201305UV02(PRPAIN201305UV02Type request, SOAPHeader header, EventLog event)
            throws Exception {

        if (service == null) {
            service = new XCPDServiceImpl();
        }

        return service.queryPatient(request, header, event);
    }
}
