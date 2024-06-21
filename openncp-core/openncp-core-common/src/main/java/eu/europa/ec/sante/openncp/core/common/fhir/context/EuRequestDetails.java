package eu.europa.ec.sante.openncp.core.common.fhir.context;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import eu.europa.ec.sante.openncp.common.immutables.Domain;
import eu.europa.ec.sante.openncp.core.common.CountryCode;
import org.immutables.value.Value;

import java.util.Optional;

@Domain
public interface EuRequestDetails {
    RequestDetails getHapiRequestDetails();

    @Value.Derived
    default CountryCode getCountryCode() {
        final String countryCode = getHapiRequestDetails().getHeader("CountryCode");
        if (countryCode == null) {
            throw new IllegalArgumentException("There was no 'CountryCode' header found, please add a header with key 'CountryCode' that contains a valid ISO 3166-1 alpha-2 code.");
        }
        return CountryCode.of(countryCode);
    }

    default RestOperationTypeEnum getRestOperationType() {
        return getHapiRequestDetails().getRestOperationType();
    }

    @Value.Derived
    default Optional<FhirSupportedResourceType> getSupportedResourceType() {
        return FhirSupportedResourceType.ofRequestPath(getHapiRequestDetails().getRequestPath());
    }

    @Value.Derived
    default String getResource() {
        return getSupportedResourceType()
                .map(FhirSupportedResourceType::getRestRequestPath)
                .map(FhirSupportedResourceType.RestRequestPath::getValue)
                .orElseGet(() -> getHapiRequestDetails().getRequestPath());
    }

    default Optional<String> getResourceId() {
        return Optional.empty();
    }

    static EuRequestDetails of(final RequestDetails requestDetails) {
        return ImmutableEuRequestDetails.of(requestDetails);
    }
}
