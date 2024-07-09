package eu.europa.ec.sante.openncp.core.common.fhir.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import eu.europa.ec.sante.openncp.core.common.fhir.config.FhirProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TokenProvider {

    private final FhirProperties fhirProperties;

    private String secret;


    public TokenProvider(FhirProperties applicationProperties) {
        this.fhirProperties = applicationProperties;
    }

    @PostConstruct
    public void init() {
        secret = fhirProperties.getSecurity().getJwt().getSecret();
    }

    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret))
                .build();
        return verifier.verify(token);
    }
}
