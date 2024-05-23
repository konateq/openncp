package eu.europa.ec.sante.openncp.common.property;

import eu.europa.ec.sante.openncp.common.immutables.Domain;

@Domain
public interface Property {
    String getKey();
    String getValue();
    boolean isSmp();
}
