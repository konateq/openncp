package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import org.w3c.dom.Document;

public class NullObligationHandler implements ObligationHandler {

    @Override
    public void discharge() {
    }

    @Override
    public Document getMessage() {
        return null;
    }
}
