package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.soap.SOAPMessage;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.LinkedList;

public class Context {

    private SOAPMessage incomingMsg;
    private Element requestDOM;
    private XMLObject requestXMLObj;
    private EnforcePolicy enforcer;
    private String user;
    private String currentHost;
    private String remoteHost;
    private X509Certificate issuercertificate;
    private X509Certificate sendercertificate;
    private X509Certificate recipientcertificate;

    private DateTime submissionTime;
    private String event;
    private String messageUUID;
    private String authenticationMethod;
    private PrivateKey key;
    private Document icomingMsgAsDocument;
    private LinkedList<String> recipientNamePostalAddress;
    private LinkedList<String> senderNamePostalAddress;

    public Context() {
    }

    public final String getCurrentHost() {
        return currentHost;
    }

    public final void setCurrentHost(final String currentHost) {
        this.currentHost = currentHost;
    }

    public final String getRemoteHost() {
        return remoteHost;
    }

    public final void setRemoteHost(final String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public SOAPMessage getIncomingMsg() {
        return incomingMsg;
    }

    public void setIncomingMsg(final SOAPMessage incomingMsg) {
        this.incomingMsg = incomingMsg;
    }

    public void setIncomingMsg(final Document incomingMsg) {
        this.icomingMsgAsDocument = incomingMsg;
    }

    public Document getIncomingMsgAsDocument() {
        return this.icomingMsgAsDocument;
    }

    public void setRequest(final XMLObject request) {
        this.requestXMLObj = request;
    }

    public XMLObject getRequestAsObject() {
        return this.requestXMLObj;
    }

    public void setRequest(final Element request) {
        this.requestDOM = request;
    }

    public Element getRequest1() {
        return this.requestDOM;
    }

    public EnforcePolicy getEnforcer() {
        return this.enforcer;
    }

    public void setEnforcer(final EnforcePolicy enforcePolicy) {
        this.enforcer = enforcePolicy;
    }

    public String getUsername() {
        return this.user;
    }

    public void setUsername(final String user) {
        this.user = user;
    }

    public X509Certificate getIssuerCertificate() {
        return this.issuercertificate;
    }

    public void setIssuerCertificate(final X509Certificate cert) {
        this.issuercertificate = cert;
    }

    public X509Certificate getSenderCertificate() {
        return this.sendercertificate;
    }

    public void setSenderCertificate(final X509Certificate cert) {
        this.sendercertificate = cert;
    }

    public X509Certificate getRecipientCertificate() {
        return this.recipientcertificate;
    }

    public void setRecipientCertificate(final X509Certificate cert) {
        this.recipientcertificate = cert;
    }

    public DateTime getSubmissionTime() {
        return this.submissionTime;
    }

    public void setSubmissionTime(final DateTime dateTime) {
        this.submissionTime = dateTime;
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(final String string) {
        this.event = string;
    }

    public String getMessageUUID() {
        return this.messageUUID;
    }

    public void setMessageUUID(final String messageUUID) {
        this.messageUUID = messageUUID;
    }

    public String getAuthenticationMethod() {
        return this.authenticationMethod;
    }

    public void setAuthenticationMethod(final String string) {
        this.authenticationMethod = string;
    }

    public PrivateKey getSigningKey() {
        return this.key;
    }

    public void setSigningKey(final PrivateKey key) {
        this.key = key;
    }

    public LinkedList<String> getRecipientNamePostalAddress() {
        return this.recipientNamePostalAddress;
    }

    public void setRecipientNamePostalAddress(final LinkedList<String> namesPostalAddress) {
        this.recipientNamePostalAddress = namesPostalAddress;
    }

    public LinkedList<String> getSenderNamePostalAddress() {
        return this.senderNamePostalAddress;
    }

    public void setSenderNamePostalAddress(final LinkedList<String> sendernamesPostalAddress) {
        this.senderNamePostalAddress = sendernamesPostalAddress;

    }
}
