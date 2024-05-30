package eu.europa.ec.sante.openncp.config;

import javax.xml.ws.Endpoint;

import eu.europa.ec.sante.openncp.sts.NextOfKinService;
import eu.europa.ec.sante.openncp.sts.STSService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServiceConfig {

    private static final String BINDING_URI = "http://www.w3.org/2003/05/soap/bindings/HTTP/";

    @Bean(name=Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        var loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);

        var springBus = new SpringBus();
        springBus.getFeatures().add(loggingFeature);
        return springBus;
    }
 
    @Bean
    public Endpoint endpoint(Bus bus, STSService stsService) {
        EndpointImpl endpoint = new EndpointImpl(bus, stsService);
        endpoint.setBindingUri(BINDING_URI);
        endpoint.publish("/SecurityTokenService");
        return endpoint;
    }

    @Bean
    public Endpoint endpoint2(Bus bus, NextOfKinService nokService) {
        EndpointImpl endpoint = new EndpointImpl(bus, nokService);
        endpoint.setBindingUri(BINDING_URI);
        endpoint.publish("/NextOfKinTokenService");
        return endpoint;
    }
}