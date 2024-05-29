package eu.europa.ec.sante.openncp.trcsts;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class STSEndpoint {
    private static final String NAMESPACE_URI = "https://ehdsi.eu/";
//    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "IssueToken")
//    @ResponsePayload
//    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
//        GetCountryResponse response = new GetCountryResponse();
//        response.setCountry(countryRepository.findCountry(request.getName()));
//
//        return response;
//    }
}
