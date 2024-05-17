package eu.europa.ec.sante.openncp.api.common.interceptors;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.DateTimeType;
import org.springframework.stereotype.Component;

@Component
public class CapabilityStatementCustomizer extends InterceptorAdapter {

    @Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
    public void customize(final IBaseConformance theCapabilityStatement) {
        final CapabilityStatement cs = (CapabilityStatement) theCapabilityStatement;

        // This is an example on how to customize the CapabilityStatement.
        cs.getSoftware().setName("myHealth@EU").setVersion("0.0.1").setReleaseDateElement(new DateTimeType("2024-01-01"));
    }
}
