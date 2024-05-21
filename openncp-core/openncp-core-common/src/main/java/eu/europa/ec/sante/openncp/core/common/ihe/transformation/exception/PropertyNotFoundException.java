package eu.europa.ec.sante.openncp.core.common.ihe.transformation.exception;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException (String key) {
        super("Could not find property with key " + key);
    }
}
