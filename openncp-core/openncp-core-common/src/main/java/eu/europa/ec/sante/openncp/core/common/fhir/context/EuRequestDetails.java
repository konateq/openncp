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
        return CountryCode.of(countryCode);
    }

    default RestOperationTypeEnum getRestOperationType() {
        return getHapiRequestDetails().getRestOperationType();
    }

    static EuRequestDetails of(final RequestDetails requestDetails) {
        return ImmutableEuRequestDetails.of(requestDetails);
    }
}
