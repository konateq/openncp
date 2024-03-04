package eu.europa.ec.sante.openncp.webmanager.backend.security;

public class AuthenticationResponse {

    private String idToken;

    public AuthenticationResponse(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }
}
