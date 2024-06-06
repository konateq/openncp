package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class Index {
    private int currentIndex;

    protected Index(final int currentIndex) {
        Validate.isTrue(currentIndex >= 0, "The index must be greater than or equal to 0, negative indexes are not allowed.");

        this.currentIndex = currentIndex;
    }

    public int getCurrent() {
        return currentIndex;
    }

    public void increment() {
        currentIndex++;
    }

    public void incrementBy(final int value) {
        currentIndex += value;
    }

    public int getCurrentAndIncrement() {
        return currentIndex++;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("currentIndex", currentIndex)
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Index index = (Index) o;
        return currentIndex == index.currentIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currentIndex);
    }
}
