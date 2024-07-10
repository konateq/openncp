package eu.europa.ec.sante.openncp.core.common.fhir.audit.dispatcher;

import eu.europa.ec.sante.openncp.common.immutables.Domain;
import org.apache.commons.lang3.Validate;

import java.util.Optional;

@Domain
public interface DispatchMetadata {
    Class<?> getDispatcherUsed();
    String getDispatchingDestination();
}
