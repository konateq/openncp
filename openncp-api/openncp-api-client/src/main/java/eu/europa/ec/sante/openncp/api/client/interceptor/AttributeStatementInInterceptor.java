package eu.europa.ec.sante.openncp.api.client.interceptor;

import java.security.Principal;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;

public class AttributeStatementInInterceptor extends AbstractPhaseInterceptor<Message> {

    public AttributeStatementInInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    public void handleMessage(final Message message) {
        final SecurityContext sc = message.get(SecurityContext.class);
        final Principal userPrincipal = sc.getUserPrincipal();
    }

    public void handleFault(final Message messageParam) {
    }
}
