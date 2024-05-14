package eu.europa.ec.sante.openncp.core.common.fhir.handler;

import org.hl7.fhir.r4.model.Bundle;

public interface BundleHandler {

    Bundle handle(Bundle bundle);
}
