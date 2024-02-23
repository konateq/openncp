package eu.europa.ec.sante.openncp.transformation.exception;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException (String key) {
        super("Could not find property with key " + key);
    }
}
