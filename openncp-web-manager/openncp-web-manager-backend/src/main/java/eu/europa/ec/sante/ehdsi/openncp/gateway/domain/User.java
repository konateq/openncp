package src.main.java.eu.europa.ec.sante.ehdsi.openncp.gateway.domain;

import java.util.Set;

public class User {

    private String username;

    private Set<String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
