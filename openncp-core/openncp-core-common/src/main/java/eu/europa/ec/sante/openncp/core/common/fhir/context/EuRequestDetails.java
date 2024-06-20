package eu.europa.ec.sante.openncp.core.common.fhir.context;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import eu.europa.ec.sante.openncp.common.immutables.Domain;
import eu.europa.ec.sante.openncp.core.common.CountryCode;

@Domain
public interface EuRequestDetails {
    RequestDetails getHapiRequestDetails();

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

    static EuRequestDetails of(final RequestDetails requestDetails) {
        return ImmutableEuRequestDetails.of(requestDetails);
    }
}
