package eu.europa.ec.sante.openncp.core.common.fhir.resourceProvider;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.*;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import eu.europa.ec.sante.openncp.core.common.fhir.fhircontext.r4.resources.CompositionLabReportEu;
import eu.europa.ec.sante.openncp.core.common.fhir.services.DispatchingService;
import eu.europa.ec.sante.openncp.core.common.fhir.handler.BundleHandler;
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
        final CompositionLabReportEu handledCompositionLabReportEu = dispatchingService.dispatchRead(theRequestDetails);
//        final CompositionLabReportEu handledCompositionLabReportEu = compositionLabReportEuHandler.handle(serverResponse);

        return handledCompositionLabReportEu;
    }

    @Search(allowUnknownParams=true)
    public IBaseBundle search(
            HttpServletRequest theServletRequest,
            HttpServletResponse theServletResponse,

            ca.uhn.fhir.rest.api.server.RequestDetails theRequestDetails,

            @Description(shortDefinition="Search the contents of the resource's data using a filter")
            @OptionalParam(name= Constants.PARAM_FILTER)
            StringAndListParam theFtFilter,

            @Description(shortDefinition="Search the contents of the resource's data using a fulltext search")
            @OptionalParam(name= Constants.PARAM_CONTENT)
            StringAndListParam theFtContent,

            @Description(shortDefinition="Search the contents of the resource's narrative using a fulltext search")
            @OptionalParam(name= Constants.PARAM_TEXT)
            StringAndListParam theFtText,

            @Description(shortDefinition="Search for resources which have the given tag")
            @OptionalParam(name= Constants.PARAM_TAG)
            TokenAndListParam theSearchForTag,

            @Description(shortDefinition="Search for resources which have the given security labels")
            @OptionalParam(name= Constants.PARAM_SECURITY)
            TokenAndListParam theSearchForSecurity,

            @Description(shortDefinition="Search for resources which have the given profile")
            @OptionalParam(name= Constants.PARAM_PROFILE)
            UriAndListParam theSearchForProfile,

            @Description(shortDefinition="Search for resources which have the given source value (Resource.meta.source)")
            @OptionalParam(name= Constants.PARAM_SOURCE)
            UriAndListParam theSearchForSource,

            @Description(shortDefinition="Return resources linked to by the given target")
            @OptionalParam(name="_has")
            HasAndListParam theHas,



            @Description(shortDefinition="The ID of the resource")
            @OptionalParam(name="_id")
            TokenAndListParam the_id,


            @Description(shortDefinition="Who attested the composition")
            @OptionalParam(name="attester", targetTypes={  } )
            ReferenceAndListParam theAttester,


            @Description(shortDefinition="Who and/or what authored the composition")
            @OptionalParam(name="author", targetTypes={  } )
            ReferenceAndListParam theAuthor,


            @Description(shortDefinition="Categorization of Composition")
            @OptionalParam(name="category")
            TokenAndListParam theCategory,


            @Description(shortDefinition="As defined by affinity domain")
            @OptionalParam(name="confidentiality")
            TokenAndListParam theConfidentiality,


            @Description(shortDefinition="Code(s) that apply to the event being documented")
            @OptionalParam(name="context")
            TokenAndListParam theContext,


            @Description(shortDefinition="Composition editing time")
            @OptionalParam(name="date")
            DateRangeParam theDate,


            @Description(shortDefinition="Context of the Composition")
            @OptionalParam(name="encounter", targetTypes={  } )
            ReferenceAndListParam theEncounter,


            @Description(shortDefinition="A reference to data that supports this section")
            @OptionalParam(name="entry", targetTypes={  } )
            ReferenceAndListParam theEntry,


            @Description(shortDefinition="Version-independent identifier for the Composition")
            @OptionalParam(name="identifier")
            TokenAndListParam theIdentifier,


            @Description(shortDefinition="Who and/or what the composition is about")
            @OptionalParam(name="patient", targetTypes={  } )
            ReferenceAndListParam thePatient,


            @Description(shortDefinition="The period covered by the documentation")
            @OptionalParam(name="period")
            DateRangeParam thePeriod,


            @Description(shortDefinition="Target of the relationship")
            @OptionalParam(name="related-id")
            TokenAndListParam theRelated_id,


            @Description(shortDefinition="Target of the relationship")
            @OptionalParam(name="related-ref", targetTypes={  } )
            ReferenceAndListParam theRelated_ref,


            @Description(shortDefinition="Classification of section (recommended)")
            @OptionalParam(name="section")
            TokenAndListParam theSection,


            @Description(shortDefinition="preliminary | final | amended | entered-in-error")
            @OptionalParam(name="status")
            TokenAndListParam theStatus,


            @Description(shortDefinition="Who and/or what the composition is about")
            @OptionalParam(name="subject", targetTypes={  } )
            ReferenceAndListParam theSubject,


            @Description(shortDefinition="Human Readable name/title")
            @OptionalParam(name="title")
            StringAndListParam theTitle,


            @Description(shortDefinition="Kind of composition (LOINC if possible)")
            @OptionalParam(name="type")
            TokenAndListParam theType,

            @RawParam
            Map<String, List<String>> theAdditionalRawParams,

            @Description(shortDefinition="Only return resources which were last updated as specified by the given range")
            @OptionalParam(name="_lastUpdated")
            DateRangeParam theLastUpdated,

            @IncludeParam
            Set<Include> theIncludes,

            @IncludeParam(reverse=true)
            Set<Include> theRevIncludes,

            @Sort
            SortSpec theSort,

            @Count
            Integer theCount,

            @Offset
            Integer theOffset,

            SummaryEnum theSummaryMode,

            SearchTotalModeEnum theSearchTotalMode,

            SearchContainedModeEnum theSearchContainedMode

    ) {
        final Bundle serverResponse = dispatchingService.dispatchSearch(theRequestDetails);
        final Bundle handledBundle = bundleHandler.handle(serverResponse);

        return handledBundle;
    }

    @Operation(name = "$document", idempotent = true, bundleType = BundleTypeEnum.DOCUMENT)
    public IBaseBundle getDocumentForComposition(
            HttpServletRequest theServletRequest,
            @IdParam IIdType theId,
            @Description(
                    formalDefinition =
                            "Results from this method are returned across multiple pages. This parameter controls the size of those pages.")
            @OperationParam(name = Constants.PARAM_COUNT, typeName = "unsignedInt")
            IPrimitiveType<Integer> theCount,
            @Description(
                    formalDefinition =
                            "Results from this method are returned across multiple pages. This parameter controls the offset when fetching a page.")
            @OperationParam(name = Constants.PARAM_OFFSET, typeName = "unsignedInt")
            IPrimitiveType<Integer> theOffset,
            @Description(
                    shortDefinition =
                            "Only return resources which were last updated as specified by the given range")
            @OperationParam(name = Constants.PARAM_LASTUPDATED, min = 0, max = 1)
            DateRangeParam theLastUpdated,
            @Sort SortSpec theSortSpec,
            RequestDetails theRequestDetails) {

        final Bundle handledCompositionLabReportEu = dispatchingService.dispatchRead(theRequestDetails);
//        final CompositionLabReportEu handledCompositionLabReportEu = compositionLabReportEuHandler.handle(serverResponse);

        return handledCompositionLabReportEu;
    }

}
