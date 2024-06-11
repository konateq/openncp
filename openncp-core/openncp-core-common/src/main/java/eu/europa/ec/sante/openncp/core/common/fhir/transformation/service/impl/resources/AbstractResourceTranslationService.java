package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;


import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import eu.europa.ec.sante.openncp.core.common.tsam.CodeConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Resource;

import java.util.Optional;


public abstract class AbstractResourceTranslationService<R extends Resource> implements ResourceTranslationService<R> {

    private final TerminologyService terminologyService;

    public AbstractResourceTranslationService(final TerminologyService terminologyService) {
        this.terminologyService = Validate.notNull(terminologyService);
    }

    @Override
    public boolean accepts(final Resource resource) {
        Validate.notNull(resource);
        return resource.getResourceType() == getResourceType();
    }

    @Override
    public R translate(final Resource resource, String targetLanguage) {
        Validate.notNull(resource);
        return this.translateTypedResource(getTypedResource(resource), targetLanguage);
    }

    private R getTypedResource(final Resource resource) {
        return (R) resource;
    }

    protected abstract R translateTypedResource(R typedResource, String targetLanguage);

    void addTranslation(final CodeableConcept codeableConcept, final String targetLanguage) {
        if (codeableConcept != null) {
            final Optional<Coding> coding = retrieveCoding(codeableConcept);
            coding.ifPresent(value -> {
                var translatedValue = getTranslation(value, targetLanguage);
                ToolingExtensions.addLanguageTranslation(value.getDisplayElement(), targetLanguage, translatedValue);
            });
        }
    }

    String getTranslation(Coding coding, String targetLanguageCode) {
        final CodeConcept codeConcept = CodeConcept.from(coding);
        final TSAMResponseStructure tsamResponse = terminologyService.getDesignation(codeConcept, targetLanguageCode);
        return tsamResponse.getDesignation();
    }

    Optional<Coding> retrieveCoding(CodeableConcept codeableConcept) {

        return codeableConcept.getCoding()
                .stream()
                .filter(coding -> !coding.getUserSelected())
                .findFirst()
                .or(() -> codeableConcept.getCoding().stream().findFirst());
    }
}
