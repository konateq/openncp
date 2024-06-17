package eu.europa.ec.sante.openncp.api.common.resourceProvider;

import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import eu.europa.ec.sante.openncp.api.common.handler.BundleHandler;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.services.DispatchingService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DiagnosticReportResourceProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticReportResourceProvider.class);

    private final DispatchingService dispatchingService;
    private final BundleHandler bundleHandler;

    public DiagnosticReportResourceProvider(final DispatchingService dispatchingService, final BundleHandler bundleHandler) {
        this.dispatchingService = Validate.notNull(dispatchingService);
        this.bundleHandler = Validate.notNull(bundleHandler);
    }

    @Override
    public Class<DiagnosticReport> getResourceType() {
        return DiagnosticReport.class;
    }

    @Search(allowUnknownParams = true)
    public IBaseBundle search(final HttpServletRequest theServletRequest, final HttpServletResponse theServletResponse,
                              final RequestDetails theRequestDetails,

                              @Description(shortDefinition = "The type of the Document") @OptionalParam(
                                      name = "type") final TokenParam type,

                              @Description(shortDefinition = "The type of the content") @OptionalParam(
                                      name = "contenttype") final TokenParam contentType,

                              @Description(shortDefinition = "Study type") @OptionalParam(
                                      name = "category") final TokenParam studyType,

                              @Description(shortDefinition = "Patient business identifier") @OptionalParam(
                                      name = "patient") final ReferenceParam patient,

                              @Description(shortDefinition = "Date range for the search") @OptionalParam(
                                      name = "date") final DateRangeParam dateRange) {

        final Bundle serverResponse = dispatchingService.dispatchSearch(EuRequestDetails.of(theRequestDetails));
        final Bundle handledBundle = bundleHandler.handle(serverResponse);

        return handledBundle;
    }
}
