package eu.europa.ec.sante.openncp.application.client.connector.interceptor;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.apache.wss4j.policy.SP12Constants;

import java.util.Collection;

public class TransportTokenInInterceptor extends AbstractPhaseInterceptor<Message> {

    public TransportTokenInInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    public void handleMessage(final Message message) {
        final AssertionInfoMap assertionInfoMap = message.get(AssertionInfoMap.class);
        final Collection<AssertionInfo> transportTokenAssertions = assertionInfoMap.getAssertionInfo(SP12Constants.TRANSPORT_TOKEN);
        transportTokenAssertions.forEach(assertionInfo -> assertionInfo.setAsserted(true));
    }

    public void handleFault(final Message messageParam) {
        // to be implemented
    }
}
