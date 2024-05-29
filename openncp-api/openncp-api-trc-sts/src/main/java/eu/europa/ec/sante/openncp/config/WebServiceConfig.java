package eu.europa.ec.sante.openncp.config;

import javax.xml.ws.Endpoint;

import eu.europa.ec.sante.openncp.trcsts.STSEndpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServiceConfig {
    @Bean
    public SpringBus springBus() {
        var loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);

        var springBus = new SpringBus();
        springBus.getFeatures().add(loggingFeature);
        return springBus;
    }
 
    @Bean
    public Endpoint endpoint(Bus bus) {
        EndpointImpl endpoint = new EndpointImpl(bus, new STSEndpoint());
        endpoint.publish("/SecurityTokenService");
        return endpoint;
    }
}