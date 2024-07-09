package eu.europa.ec.sante.openncp.core.common.fhir.interceptors;

import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import com.auth0.jwt.interfaces.DecodedJWT;
import eu.europa.ec.sante.openncp.core.common.fhir.security.TokenProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtSamlInterceptor extends InterceptorAdapter {

    private final TokenProvider tokenProvider;

    public JwtSamlInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean incomingRequestPreProcessed(HttpServletRequest theRequest, HttpServletResponse theResponse) {
        String authorization = theRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWith(authorization, "Bearer ")) {
            return super.incomingRequestPreProcessed(theRequest, theResponse);
        }

        String token = StringUtils.remove(authorization, "Bearer ");
        DecodedJWT jwt = tokenProvider.verifyToken(token);





        return true;
    }
}
