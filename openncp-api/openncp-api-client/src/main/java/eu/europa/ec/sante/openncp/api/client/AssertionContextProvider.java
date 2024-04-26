package eu.europa.ec.sante.openncp.api.client;

import java.util.Optional;

import org.apache.commons.lang3.Validate;

public class AssertionContextProvider {

    private static final ThreadLocal<AssertionContext> threadLocalAssertionContext = new ThreadLocal<>();

    public static Optional<AssertionContext> getAssertionContext() {
        return Optional.ofNullable(threadLocalAssertionContext.get());
    }

    public static void setAssertionContext(final AssertionContext assertionContext) {
        Validate.notNull(assertionContext);
        threadLocalAssertionContext.set(assertionContext);
    }
}
