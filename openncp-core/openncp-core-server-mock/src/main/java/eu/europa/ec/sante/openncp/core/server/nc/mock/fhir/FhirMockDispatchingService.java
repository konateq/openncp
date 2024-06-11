package eu.europa.ec.sante.openncp.core.server.nc.mock.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.BundleBuilder;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.services.DispatchingService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FhirMockDispatchingService implements DispatchingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FhirMockDispatchingService.class);

    private final FhirContext fhirContext;


    public FhirMockDispatchingService(final FhirContext fhirContext) {
        this.fhirContext = Validate.notNull(fhirContext, "FhirContext cannot be null");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IBaseResource> T dispatchSearch(final EuRequestDetails requestDetails) {
        final BundleBuilder bundleBuilder = new BundleBuilder(fhirContext);
        final IBaseBundle bundle = bundleBuilder.getBundle();

        return (T) bundle;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T extends IBaseResource> T dispatchRead(final EuRequestDetails requestDetails) {
        final BundleBuilder bundleBuilder = new BundleBuilder(fhirContext);
        final IBaseBundle bundle = bundleBuilder.getBundle();

        return (T) bundle;
    }
}
