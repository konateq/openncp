package eu.europa.ec.sante.openncp.api.common.interceptors;

import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Component
public class EuCorsInterceptor extends CorsInterceptor {

    public EuCorsInterceptor() {
        super(createConfig());
    }

    private static CorsConfiguration createConfig() {
        final CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("x-fhir-starter");
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Content-Type");

        config.addAllowedOrigin("*");

        config.addExposedHeader("Location");
        config.addExposedHeader("Content-Location");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        return config;
    }
}
