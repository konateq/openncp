package eu.europa.ec.sante.openncp.core.common.fhir.interceptors;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Interceptor
@Component
public class SimpleServerLoggingInterceptor implements FhirCustomInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServerLoggingInterceptor.class);

    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
    public void logRequests(RequestDetails theRequest) {
        LOGGER.info("Pointcuts");
        Arrays.stream(Pointcut.values()).forEach(pointcut -> {
            LOGGER.info("@Hook(Pointcut." + pointcut.name() + ")");
        });
        LOGGER.info("Request of type {} with request ID: {}", theRequest.getOperation(), theRequest.getRequestId());
    }
}