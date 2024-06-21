package eu.europa.ec.sante.openncp.core.common.fhir.interceptors;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import eu.europa.ec.sante.openncp.common.context.LogContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.UUID;

/**
 * Sets a correlation id to the request header that should propagate through all the connected services.
 */
@Interceptor(order = Integer.MIN_VALUE)
@Component
public class CorrelationIdInterceptor implements FhirCustomInterceptor, IClientInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdInterceptor.class);
    public static final String X_REQUEST_ID_HEADER_KEY = "X-Request-ID";

    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_PROCESSED)
    public void setIncomingCorrelationIdToLogContext(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        final String correlationId = Optional.ofNullable(httpServletRequest.getHeader(X_REQUEST_ID_HEADER_KEY)).map(correlationIdFromRequest -> {
            LOGGER.debug("The request contains a [{}] header with value [{}], using that as correlation id", X_REQUEST_ID_HEADER_KEY, correlationIdFromRequest);
            return correlationIdFromRequest;
        }).orElseGet(() -> {
            LOGGER.debug("The request did NOT contain a [{}] header, creating a new correlation id", X_REQUEST_ID_HEADER_KEY);
            return UUID.randomUUID().toString();
        });
        LogContext.setCorrelationId(correlationId);
        httpServletResponse.setHeader(X_REQUEST_ID_HEADER_KEY, LogContext.getCorrelationId());
    }

    /**
     * HAPI rewrites the X-Request-ID between the Pointcuts SERVER_OUTGOING_RESPONSE and SERVER_OUTGOING_WRITER_CREATED
     * To be able to have the uniform correlation id we need to rewrite it again.
     *
     * @param writer
     * @param requestDetails
     * @param servletRequestDetails
     */
    @Hook(Pointcut.SERVER_OUTGOING_WRITER_CREATED)
    public void setIncomingCorrelationIdToLogContext(
            final Writer writer,
            final RequestDetails requestDetails,
            final ServletRequestDetails servletRequestDetails
    ) {
        servletRequestDetails.getServletResponse().setHeader(X_REQUEST_ID_HEADER_KEY, LogContext.getCorrelationId());
    }

    @Override
    public void interceptRequest(final IHttpRequest theRequest) {
        theRequest.removeHeaders(X_REQUEST_ID_HEADER_KEY);
        theRequest.addHeader(X_REQUEST_ID_HEADER_KEY, LogContext.getCorrelationId());
    }

    @Override
    public void interceptResponse(final IHttpResponse theResponse) throws IOException {
        LOGGER.info("Received response with header [{}] and value [{}]", X_REQUEST_ID_HEADER_KEY, theResponse.getHeaders(X_REQUEST_ID_HEADER_KEY));
    }
}
