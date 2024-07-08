package eu.europa.ec.sante.openncp.common.configuration;

import eu.europa.ec.sante.openncp.common.immutables.Domain;
import org.immutables.value.Value;

import java.util.Optional;

@Domain
public interface ProxySettings {
    boolean isEnabled();

    @Value.Default
    default boolean isAuthenticated() {
        return false;
    }

    Optional<String> getHost();

    Optional<Integer> getPort();

    Optional<String> getUsername();

    Optional<String> getPassword();

    static ProxySettings none() {
        return ImmutableProxySettings.builder().enabled(false).build();
    }
}
