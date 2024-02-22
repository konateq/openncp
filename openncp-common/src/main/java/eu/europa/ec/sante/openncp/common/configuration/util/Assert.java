package eu.europa.ec.sante.openncp.common.configuration.util;

public class Assert {

    private Assert() {
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
