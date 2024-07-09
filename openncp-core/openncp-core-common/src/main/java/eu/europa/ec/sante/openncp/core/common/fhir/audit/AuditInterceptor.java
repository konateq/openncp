package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.ResponseDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler.AuditEventProducer;
import eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler.AuditableEvent;
import eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler.FallbackAuditEventProducer;
import eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler.ImmutableAuditableEvent;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.interceptors.FhirCustomInterceptor;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Interceptor(order = 5)
@Component
@Order(Integer.MAX_VALUE)
public class AuditInterceptor implements FhirCustomInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditInterceptor.class);
    private final FhirContext fhirContext;
    private final List<AuditEventProducer> auditEventProducers;
    private final FallbackAuditEventProducer fallbackAuditEventProducer;
    private final AuditDispatcher auditDispatcher;

    public AuditInterceptor(final FhirContext fhirContext, final List<AuditEventProducer> auditEventProducers, final FallbackAuditEventProducer fallbackAuditEventProducer, final AuditDispatcher auditDispatcher) {
        this.fhirContext = Validate.notNull(fhirContext, "fhirContext cannot be null.");
        this.auditEventProducers = Validate.notNull(auditEventProducers, "auditEventProducers cannot be null.");
        this.fallbackAuditEventProducer = Validate.notNull(fallbackAuditEventProducer, "fallbackAuditEventProducer cannot be null.");
        this.auditDispatcher = Validate.notNull(auditDispatcher, "auditDispatcher cannot be null.");
    }

    @Hook(Pointcut.SERVER_OUTGOING_RESPONSE)
    public void auditOutgoingResponse(final RequestDetails requestDetails,
                                      final ServletRequestDetails servletRequestDetails,
                                      final IBaseResource baseResource,
                                      final ResponseDetails responseDetails,
                                      final HttpServletRequest httpServletRequest,
                                      final HttpServletResponse httpServletResponse) {
        final EuRequestDetails euRequestDetails = EuRequestDetails.of(requestDetails);
        final AuditableEvent auditableEvent = ImmutableAuditableEvent.builder()
                .pointcut(Pointcut.SERVER_OUTGOING_RESPONSE)
                .fhirContext(fhirContext)
                .euRequestDetails(euRequestDetails)
                .resource(baseResource)
                .build();
        final List<AuditEvent> auditEvents = auditEventProducers.stream()
                .filter(auditEventProducer -> auditEventProducer.accepts(auditableEvent))
                .findFirst()
                .map(auditEventProducer -> auditEventProducer.produce(auditableEvent))
                .orElseGet(() -> fallbackAuditEventProducer.produce(auditableEvent));

        auditEvents.forEach(auditDispatcher::dispatch);
    }
}
