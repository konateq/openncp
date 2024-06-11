package eu.europa.ec.sante.openncp.core.common.fhir;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FhirDispatchingClient {
    private final IGenericClient genericClient;

    public FhirDispatchingClient(final IGenericClient genericClient) {
        this.genericClient = genericClient;
    }

    public Bundle dispatch(final EuRequestDetails requestDetails) {
        final MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>(
                requestDetails.getHapiRequestDetails().getParameters().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue()))));

        final String uri = UriComponentsBuilder.fromHttpUrl(genericClient.getServerBase())
                .path(requestDetails.getHapiRequestDetails().getRequestPath())
                .queryParams(parameterMap)
                .build()
                .toUriString();

        if (requestDetails.getRestOperationType() == RestOperationTypeEnum.SEARCH_TYPE) {
            return genericClient.search().byUrl(uri).returnBundle(Bundle.class).execute();
        }
        throw new UnsupportedOperationException("Currently only the \"search\" operation is supported");
    }
}
