package eu.europa.ec.sante.openncp.api.common.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.stereotype.Component;

@Component
public class BundleResourceProvider implements IResourceProvider {

    @Read
    public Bundle find(@IdParam final IdType id) {
        return null;
    }

    @Override
    public Class<Bundle> getResourceType() {
        return Bundle.class;
    }
}
