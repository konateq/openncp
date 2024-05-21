package eu.europa.ec.sante.openncp.core.common.fhir.services;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WsDispatchingService implements DispatchingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsDispatchingService.class);
    
    private final IGenericClient hapiHL7WebClient;

    private final ValidationService validationService;

    public WsDispatchingService(final IGenericClient hapiHL7WebClient, final ValidationService validationService) {
        this.hapiHL7WebClient = Validate.notNull(hapiHL7WebClient);
        this.validationService = validationService;
    }

    @Override
    public <T extends IBaseResource> T dispatchSearch(final RequestDetails requestDetails) {
        Validate.notNull(requestDetails, "The request details cannot be null");

        final MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>(
                requestDetails.getParameters().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue()))));

        final String uri = UriComponentsBuilder.fromHttpUrl(hapiHL7WebClient.getServerBase())
                                               .path(requestDetails.getRequestPath())
                                               .queryParams(parameterMap)
                                               .build()
                                               .toUriString();

        Bundle result;
        if (Objects.requireNonNull(requestDetails.getRestOperationType()) == RestOperationTypeEnum.SEARCH_TYPE) {
            result =  hapiHL7WebClient.search().byUrl(uri).returnBundle(Bundle.class).execute();
            validationService.validate(result, RestOperationTypeEnum.SEARCH_TYPE);
        } else {
            throw new UnsupportedOperationException("Currently only the \"search\" operation is supported");
        }

        return (T) result;
    }

    @Override
    public <T extends IBaseResource> T dispatchRead(final RequestDetails requestDetails) {
        Validate.notNull(requestDetails, "The request details cannot be null");

        final MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>(
                requestDetails.getParameters().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue()))));

        final String uri = UriComponentsBuilder.fromHttpUrl(hapiHL7WebClient.getServerBase())
                .path(requestDetails.getRequestPath())
                .queryParams(parameterMap)
                .build()
                .toUriString();

        final T result  = (T) hapiHL7WebClient.search().byUrl(uri).execute();
        validationService.validate(result, requestDetails.getRestOperationType());
        return result;
    }
}
