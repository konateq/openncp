package eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler;

import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import eu.europa.ec.sante.openncp.common.immutables.Domain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Domain
public interface AuditableEvent {
    Pointcut getPointcut();

    Optional<RequestDetails> getRequestDetails();

    Optional<ServletRequestDetails> getServletRequestDetails();

    Optional<HttpServletRequest> getHttpServletRequest();

    Optional<HttpServletResponse> getHttpServletResponse();

    Optional<Throwable> getThrowable();
}
