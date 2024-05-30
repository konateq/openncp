package eu.europa.ec.sante.openncp.api.common.handler;


import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.CompositionLabReportEu;

public interface CompositionLabReportEuHandler {

    CompositionLabReportEu handle(CompositionLabReportEu bundle);
}
