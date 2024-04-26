package eu.europa.ec.sante.openncp.core.common.evidence;

import org.w3c.dom.Document;

public interface ObligationHandler {

    void discharge() throws ObligationDischargeException;

    Document getMessage();
}
