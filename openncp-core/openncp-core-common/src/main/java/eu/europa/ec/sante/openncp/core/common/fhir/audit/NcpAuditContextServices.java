package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.storage.interceptor.balp.IBalpAuditContextServices;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class NcpAuditContextServices implements IBalpAuditContextServices {

    /**
     * Here we are just hard-coding a simple display name. In a real implementation
     * we should use the actual identity of the requesting client.
     */
    @Nonnull
    @Override
    public Reference getAgentClientWho(final RequestDetails theRequestDetails) {
        final Reference client = new Reference();
        client.setDisplay("Growth Chart Application");
        client.getIdentifier().setSystem("http://example.org/clients").setValue("growth_chart");
        return client;
    }

    /**
     * Here we are just hard-coding a simple display name. In a real implementation
     * we should use the actual identity of the requesting user.
     */
    @Nonnull
    @Override
    public Reference getAgentUserWho(final RequestDetails theRequestDetails) {
        final Reference user = new Reference();
        user.getIdentifier().setSystem("http://example.org/users").setValue("my_username");
        return user;
    }
}