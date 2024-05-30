package eu.europa.ec.sante.openncp.common.property;

import eu.europa.ec.sante.openncp.common.immutables.Domain;
import org.immutables.value.Value;

@Domain
public interface Property {
    String getKey();
    String getValue();

    @Value.Default
    default boolean isSmp() {
        return false;
    }

    static Property of(final String key, final String value) {
        return ImmutableProperty.builder().key(key).value(value).build();
    }
}
