package eu.europa.ec.sante.openncp.core.common.fhir.services;

import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import org.hl7.fhir.instance.model.api.IBaseResource;

public interface DispatchingService {

    <T extends IBaseResource> T dispatchSearch(EuRequestDetails requestDetails);

    <T extends IBaseResource> T dispatchRead(EuRequestDetails requestDetails);
}
