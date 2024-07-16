package eu.europa.ec.sante.openncp.core.common.fhir.interceptors;

import org.opensaml.saml.saml2.core.Assertion;
import org.w3c.dom.Element;

public class AuditInfo {

    private Assertion assertion;

    private Element samlasRoot;

    private String requestIp;

    private String hostIp;

    public AuditInfo(Assertion assertion, Element samlasRoot) {
        this.assertion = assertion;
        this.samlasRoot = samlasRoot;
    }

    public Assertion getAssertion() {
        return assertion;
    }

    public void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    public Element getSamlasRoot() {
        return samlasRoot;
    }

    public void setSamlasRoot(Element samlasRoot) {
        this.samlasRoot = samlasRoot;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}
