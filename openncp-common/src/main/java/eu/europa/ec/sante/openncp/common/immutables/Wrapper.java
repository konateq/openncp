package eu.europa.ec.sante.openncp.common.immutables;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

public abstract class Wrapper<T> {
    @Value.Parameter
    public abstract T value();

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("value", value())
                .toString();
    }
}