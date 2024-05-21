package eu.europa.ec.sante.openncp.application.client.ihe;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import eu.europa.ec.sante.openncp.api.client.interceptor.AssertionsInInterceptor;
import eu.europa.ec.sante.openncp.api.client.interceptor.TransportTokenInInterceptor;
import eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.rt.security.SecurityConstants;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServiceConfig {

    @Bean
    public LoggingFeature loggingFeature() {
        final LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        loggingFeature.setVerbose(true);
        return loggingFeature;
    }

    @Bean
    public Endpoint endpoint(final Bus bus, final ClientConnectorServicePortType clientConnectorServicePortType,
                             final LoggingFeature loggingFeature) {
        final EndpointImpl endpoint = new EndpointImpl(bus, clientConnectorServicePortType);
        endpoint.getFeatures().add(loggingFeature);
        endpoint.getFeatures().add(new WSAddressingFeature());

        endpoint.getProperties().put(SecurityConstants.SIGNATURE_PROPERTIES, "signature.properties");

        endpoint.getInInterceptors().add(new AssertionsInInterceptor());
        endpoint.getInInterceptors().add(new TransportTokenInInterceptor());

        endpoint.publish("/" + clientConnectorServicePortType.getClass().getAnnotation(WebService.class).serviceName());

        return endpoint;
    }
}