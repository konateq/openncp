package eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain;

import org.w3c.dom.Document;

public class TranscodeRequest {

    private Document friendlyFhir;

    public Document getFriendlyFhir() {
        return friendlyFhir;
    }

    public void setFriendlyFhir(Document friendlyFhir) {
        this.friendlyFhir = friendlyFhir;
    }
}
