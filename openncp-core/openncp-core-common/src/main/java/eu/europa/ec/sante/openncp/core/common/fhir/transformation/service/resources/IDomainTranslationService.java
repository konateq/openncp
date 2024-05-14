package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources;

public interface IDomainTranslationService<T> {

    T translate(T resource, String targetLanguage);
}
