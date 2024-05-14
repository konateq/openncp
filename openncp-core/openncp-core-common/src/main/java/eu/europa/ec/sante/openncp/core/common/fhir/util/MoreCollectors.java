package eu.europa.ec.sante.openncp.core.common.fhir.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class MoreCollectors {

    private MoreCollectors() {}

    public static <T> Collector<T, ?, T> exactlyOne(final Object identifier) {
        return collectingAndThen(toList(), list -> {
            if (list.isEmpty()) {
                throw new NoSuchElementException("Expecting one for identifier [" + identifier + "] but found none.");
            } else if (list.size() > 1) {
                throw new IllegalArgumentException("Expecting one for identifier [" + identifier + "] but found several [" + list + ']');
            } else {
                return list.get(0);
            }
        });
    }

    public static <T> Collector<T, ?, T> exactlyOne(final Object identifier, final Object source) {
        return collectingAndThen(toList(), list -> {
            if (list.isEmpty()) {
                throw new NoSuchElementException("Expecting one for identifier [" + identifier + "] but found none in source [" + source + ']');
            } else if (list.size() > 1) {
                throw new IllegalArgumentException(
                        "Expecting one for identifier [" + identifier + "] but found several [" + list + "] in source [" + source + ']');
            } else {
                return list.get(0);
            }
        });
    }

    public static <T> Collector<T, ?, Optional<T>> oneOrNone(final Object identifier) {
        return collectingAndThen(toList(), list -> {
            if (list.isEmpty()) {
                return Optional.empty();
            } else if (list.size() > 1) {
                throw new IllegalArgumentException("Expecting one or none for identifier [" + identifier + "] but found several [" + list + ']');
            } else {
                return Optional.of(list.get(0));
            }
        });
    }

    public static <T> Collector<T, ?, Optional<T>> oneOrNone(final Object identifier, final Object source) {
        return collectingAndThen(toList(), list -> {
            if (list.isEmpty()) {
                return Optional.empty();
            } else if (list.size() > 1) {
                throw new IllegalArgumentException(
                        "Expecting one or none for identifier [" + identifier + "] but found several [" + list + "] in source [" + source + ']');
            } else {
                return Optional.of(list.get(0));
            }
        });
    }
}
