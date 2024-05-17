package eu.europa.ec.sante.openncp.api.common.interceptors;

import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.Collections;

//TODO [KJW] WIP: use this interceptor if we need to secure the openapi docs.
public class SecuredOpenApiInterceptor extends OpenApiInterceptor {

    @Override
    protected OpenAPI generateOpenApi(final ServletRequestDetails theRequestDetails) {
        final OpenAPI openApi = super.generateOpenApi(theRequestDetails);
        // Add Authentication to OAS spec.
        openApi.getComponents().addSecuritySchemes("oauth2schema", oauth2ImplicitSecurityScheme());
        final SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("oauth2schema");
        openApi.security(Collections.singletonList(securityRequirement));
        return openApi;
    }

    private SecurityScheme oauth2ImplicitSecurityScheme() {
        //FIXME todo
        return null;
    }
}

