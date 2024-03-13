package eu.europa.ec.sante.openncp.application.client;

import eu.europa.ec.sante.openncp.core.client.ClientConnectorServicePortType;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@Configuration
public class WebServiceConfig {
    @Autowired
    private Bus bus;

    @Bean
    public Endpoint endpoint(final ClientConnectorServicePortType clientConnectorServicePortType) {
        EndpointImpl endpoint = new EndpointImpl(bus, clientConnectorServicePortType);
        endpoint.publish("/" + clientConnectorServicePortType.getClass().getAnnotation(WebService.class).serviceName());
        return endpoint;
    }
}