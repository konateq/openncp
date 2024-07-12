package eu.europa.ec.sante.openncp.api.common.resourceProvider;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractResourceProvider {

    public String getJwtFromRequest(final HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header;
        }
        throw new RuntimeException("JWT Token is missing");
    }
}
