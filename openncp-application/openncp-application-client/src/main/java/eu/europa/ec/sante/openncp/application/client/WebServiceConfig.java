package eu.europa.ec.sante.openncp.application.client;

import java.util.HashMap;
import java.util.Map;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.BindingConfiguration;
import org.apache.cxf.binding.soap.SoapBindingConfiguration;
import org.apache.cxf.binding.soap.SoapBindingFactory;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.opensaml.soap.util.SOAPVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServiceConfig {

    @Autowired
    private Bus bus;

    @Bean
    public Endpoint endpoint(final ClientConnectorServicePortType clientConnectorServicePortType) {
        final EndpointImpl endpoint = new EndpointImpl(bus, clientConnectorServicePortType);

//        final Map<String, Object> inProps = new HashMap<>();
//        final WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
//        endpoint.getInInterceptors().add(wssIn);
//
//        final Map<String, Object> outProps = new HashMap<>();
//        final WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
//        endpoint.getOutInterceptors().add(wssOut);

//        final String soap12Binding = SoapBindingFactory.SOAP_12_BINDING;
//        endpoint.getFeatures().add(new WSAddressingFeature());
//        final SoapBindingConfiguration config = new SoapBindingConfiguration();
//        config.setVersion(SOAPVersion.SOAP_1_2);
//        endpoint.setBindingConfig(config);
        endpoint.publish("/" + clientConnectorServicePortType.getClass().getAnnotation(WebService.class).serviceName());

        return endpoint;
    }
}
