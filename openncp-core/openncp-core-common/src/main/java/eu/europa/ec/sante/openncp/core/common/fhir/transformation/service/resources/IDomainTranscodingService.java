package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources;

public interface IDomainTranscodingService<T> {

    T transcode(T resource);
}
