package eu.europa.ec.sante.openncp.api.common.resourceProvider;

import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.services.DispatchingService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DocumentReferenceResourceProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentReferenceResourceProvider.class);

    private final DispatchingService dispatchingService;

    public DocumentReferenceResourceProvider(final DispatchingService dispatchingService) {
        this.dispatchingService = Validate.notNull(dispatchingService);
    }

    @Override
    public Class<DocumentReference> getResourceType() {
        return DocumentReference.class;
    }

    @Search(allowUnknownParams = true)
    public IBaseBundle search(final HttpServletRequest theServletRequest, final HttpServletResponse theServletResponse,
                              final EuRequestDetails theRequestDetails) {
        final Bundle serverResponse = dispatchingService.dispatchSearch(theRequestDetails);

        return serverResponse;
    }
}
