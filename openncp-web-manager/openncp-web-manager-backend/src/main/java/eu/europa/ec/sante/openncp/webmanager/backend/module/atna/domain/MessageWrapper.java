package eu.europa.ec.sante.openncp.webmanager.backend.module.atna.domain;


import net.RFC3881.dicom.AuditMessage;

public class MessageWrapper {

    private Long id;

    private AuditMessage auditMessage;

    private String xmlMessage;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public AuditMessage getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(final AuditMessage auditMessage) {
        this.auditMessage = auditMessage;
    }

    public String getXmlMessage() {
        return xmlMessage;
    }

    public void setXmlMessage(final String xmlMessage) {
        this.xmlMessage = xmlMessage;
    }
}
