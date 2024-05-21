package eu.europa.ec.sante.openncp.core.common.fhir.services;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hl7.fhir.instance.model.api.IBaseResource;

public interface DispatchingService {

    <T extends IBaseResource> T dispatchSearch(RequestDetails requestDetails);

    <T extends IBaseResource> T dispatchRead(RequestDetails requestDetails);
}
