package src.main.java.eu.europa.ec.sante.ehdsi.openncp.gateway.security;

import javax.validation.constraints.NotBlank;

public class AuthenticationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
