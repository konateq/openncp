package eu.europa.ec.sante.openncp.api.common.resourceProvider;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.*;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import eu.europa.ec.sante.openncp.api.common.handler.BundleHandler;
import eu.europa.ec.sante.openncp.core.common.fhir.context.ImmutableEuRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.CompositionLabReportEu;
import eu.europa.ec.sante.openncp.core.common.fhir.services.DispatchingService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CompositionLabReportEuResourceProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositionLabReportEuResourceProvider.class);

    private final DispatchingService dispatchingService;
    private final BundleHandler bundleHandler;


    public CompositionLabReportEuResourceProvider(final DispatchingService dispatchingService, final BundleHandler bundleHandler) {
        this.dispatchingService = Validate.notNull(dispatchingService);
        this.bundleHandler = bundleHandler;
    }

    @Override
    public Class<CompositionLabReportEu> getResourceType() {
        return CompositionLabReportEu.class;
    }


    @Read
    public CompositionLabReportEu find(@IdParam final IdType id, final HttpServletRequest theServletRequest, final HttpServletResponse theServletResponse,
                                       final RequestDetails theRequestDetails) {
        final CompositionLabReportEu handledCompositionLabReportEu = dispatchingService.dispatchRead(ImmutableEuRequestDetails.of(theRequestDetails));
//        final CompositionLabReportEu handledCompositionLabReportEu = compositionLabReportEuHandler.handle(serverResponse);

        return handledCompositionLabReportEu;
    }

    @Search(allowUnknownParams = true)
    public IBaseBundle search(
            final HttpServletRequest theServletRequest,
            final HttpServletResponse theServletResponse,

            final RequestDetails theRequestDetails,

            @Description(shortDefinition = "Search the contents of the resource's data using a filter")
            @OptionalParam(name = Constants.PARAM_FILTER) final
            StringAndListParam theFtFilter,

            @Description(shortDefinition = "Search the contents of the resource's data using a fulltext search")
            @OptionalParam(name = Constants.PARAM_CONTENT) final
            StringAndListParam theFtContent,

            @Description(shortDefinition = "Search the contents of the resource's narrative using a fulltext search")
            @OptionalParam(name = Constants.PARAM_TEXT) final
            StringAndListParam theFtText,

            @Description(shortDefinition = "Search for resources which have the given tag")
            @OptionalParam(name = Constants.PARAM_TAG) final
            TokenAndListParam theSearchForTag,

            @Description(shortDefinition = "Search for resources which have the given security labels")
            @OptionalParam(name = Constants.PARAM_SECURITY) final
            TokenAndListParam theSearchForSecurity,

            @Description(shortDefinition = "Search for resources which have the given profile")
            @OptionalParam(name = Constants.PARAM_PROFILE) final
            UriAndListParam theSearchForProfile,

            @Description(shortDefinition = "Search for resources which have the given source value (Resource.meta.source)")
            @OptionalParam(name = Constants.PARAM_SOURCE) final
            UriAndListParam theSearchForSource,

            @Description(shortDefinition = "Return resources linked to by the given target")
            @OptionalParam(name = "_has") final
            HasAndListParam theHas,


            @Description(shortDefinition = "The ID of the resource")
            @OptionalParam(name = "_id") final
            TokenAndListParam the_id,


            @Description(shortDefinition = "Who attested the composition")
            @OptionalParam(name = "attester", targetTypes = {}) final
            ReferenceAndListParam theAttester,


            @Description(shortDefinition = "Who and/or what authored the composition")
            @OptionalParam(name = "author", targetTypes = {}) final
            ReferenceAndListParam theAuthor,


            @Description(shortDefinition = "Categorization of Composition")
            @OptionalParam(name = "category") final
            TokenAndListParam theCategory,


            @Description(shortDefinition = "As defined by affinity domain")
            @OptionalParam(name = "confidentiality") final
            TokenAndListParam theConfidentiality,


            @Description(shortDefinition = "Code(s) that apply to the event being documented")
            @OptionalParam(name = "context") final
            TokenAndListParam theContext,


            @Description(shortDefinition = "Composition editing time")
            @OptionalParam(name = "date") final
            DateRangeParam theDate,


            @Description(shortDefinition = "Context of the Composition")
            @OptionalParam(name = "encounter", targetTypes = {}) final
            ReferenceAndListParam theEncounter,


            @Description(shortDefinition = "A reference to data that supports this section")
            @OptionalParam(name = "entry", targetTypes = {}) final
            ReferenceAndListParam theEntry,


            @Description(shortDefinition = "Version-independent identifier for the Composition")
            @OptionalParam(name = "identifier") final
            TokenAndListParam theIdentifier,


            @Description(shortDefinition = "Who and/or what the composition is about")
            @OptionalParam(name = "patient", targetTypes = {}) final
            ReferenceAndListParam thePatient,


            @Description(shortDefinition = "The period covered by the documentation")
            @OptionalParam(name = "period") final
            DateRangeParam thePeriod,


            @Description(shortDefinition = "Target of the relationship")
            @OptionalParam(name = "related-id") final
            TokenAndListParam theRelated_id,


            @Description(shortDefinition = "Target of the relationship")
            @OptionalParam(name = "related-ref", targetTypes = {}) final
            ReferenceAndListParam theRelated_ref,


            @Description(shortDefinition = "Classification of section (recommended)")
            @OptionalParam(name = "section") final
            TokenAndListParam theSection,


            @Description(shortDefinition = "preliminary | final | amended | entered-in-error")
            @OptionalParam(name = "status") final
            TokenAndListParam theStatus,


            @Description(shortDefinition = "Who and/or what the composition is about")
            @OptionalParam(name = "subject", targetTypes = {}) final
            ReferenceAndListParam theSubject,


            @Description(shortDefinition = "Human Readable name/title")
            @OptionalParam(name = "title") final
            StringAndListParam theTitle,


            @Description(shortDefinition = "Kind of composition (LOINC if possible)")
            @OptionalParam(name = "type") final
            TokenAndListParam theType,

            @RawParam final
            Map<String, List<String>> theAdditionalRawParams,

            @Description(shortDefinition = "Only return resources which were last updated as specified by the given range")
            @OptionalParam(name = "_lastUpdated") final
            DateRangeParam theLastUpdated,

            @IncludeParam final
            Set<Include> theIncludes,

            @IncludeParam(reverse = true) final
            Set<Include> theRevIncludes,

            @Sort final
            SortSpec theSort,

            @Count final
            Integer theCount,

            @Offset final
            Integer theOffset,

            final SummaryEnum theSummaryMode,

            final SearchTotalModeEnum theSearchTotalMode,

            final SearchContainedModeEnum theSearchContainedMode

    ) {
        final Bundle serverResponse = dispatchingService.dispatchSearch(ImmutableEuRequestDetails.of(theRequestDetails));
        final Bundle handledBundle = bundleHandler.handle(serverResponse);

        return handledBundle;
    }

    @Operation(name = "$document", idempotent = true, bundleType = BundleTypeEnum.DOCUMENT)
    public IBaseBundle getDocumentForComposition(
            final HttpServletRequest theServletRequest,
            @IdParam final IIdType theId,
            @Description(
                    formalDefinition =
                            "Results from this method are returned across multiple pages. This parameter controls the size of those pages.")
            @OperationParam(name = Constants.PARAM_COUNT, typeName = "unsignedInt") final
            IPrimitiveType<Integer> theCount,
            @Description(
                    formalDefinition =
                            "Results from this method are returned across multiple pages. This parameter controls the offset when fetching a page.")
            @OperationParam(name = Constants.PARAM_OFFSET, typeName = "unsignedInt") final
            IPrimitiveType<Integer> theOffset,
            @Description(
                    shortDefinition =
                            "Only return resources which were last updated as specified by the given range")
            @OperationParam(name = Constants.PARAM_LASTUPDATED, min = 0, max = 1) final
            DateRangeParam theLastUpdated,
            @Sort final SortSpec theSortSpec,
            final RequestDetails theRequestDetails) {

        final Bundle handledCompositionLabReportEu = dispatchingService.dispatchRead(ImmutableEuRequestDetails.of(theRequestDetails));
//        final CompositionLabReportEu handledCompositionLabReportEu = compositionLabReportEuHandler.handle(serverResponse);

        return handledCompositionLabReportEu;
    }

}
