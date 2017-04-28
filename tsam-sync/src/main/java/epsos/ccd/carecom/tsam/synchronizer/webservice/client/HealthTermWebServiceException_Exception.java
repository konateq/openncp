package epsos.ccd.carecom.tsam.synchronizer.webservice.client;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 */
@WebFault(name = "HealthTermWebServiceException", targetNamespace = "http://cts2.webservice.ht.carecom.dk/")
public class HealthTermWebServiceException_Exception extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private HealthTermWebServiceException faultInfo;

    /**
     * @param message
     * @param faultInfo
     */
    public HealthTermWebServiceException_Exception(String message, HealthTermWebServiceException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * @param message
     * @param faultInfo
     * @param cause
     */
    public HealthTermWebServiceException_Exception(String message, HealthTermWebServiceException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * @return returns fault bean: epsos.ccd.carecom.tsam.synchronizer.webservice.client.HealthTermWebServiceException
     */
    public HealthTermWebServiceException getFaultInfo() {
        return faultInfo;
    }

}
