package eu.europa.ec.sante.openncp.core.common.fhir.interceptors;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.ResponseDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import eu.europa.ec.sante.openncp.common.context.LogContext;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Clears the context related to a specific FHIR request
 */
@Interceptor(order = Integer.MAX_VALUE)
@Component
public class ClearContextInterceptor implements FhirCustomInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClearContextInterceptor.class);


    @Hook(Pointcut.SERVER_PROCESSING_COMPLETED)
    public void setOutgoingCorrelationId(
            final RequestDetails requestDetails,
            final ServletRequestDetails servletRequestDetails,
            final IBaseResource baseResource,
            final ResponseDetails responseDetails,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse
    ) {
        LOGGER.debug("Clearing the log context");
        LogContext.clear();
    }
}
