package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import eu.europa.ec.sante.openncp.core.common.tsam.CodeConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Resource;

import java.util.Optional;

public abstract class AbstractResourceTranscodingService<R extends Resource> implements ResourceTranscodingService<R> {

    private final TerminologyService terminologyService;

    public AbstractResourceTranscodingService(final TerminologyService terminologyService) {
        this.terminologyService = Validate.notNull(terminologyService);
    }

    @Override
    public boolean accepts(final Resource resource) {
        Validate.notNull(resource);
        return resource.getResourceType() == getResourceType();
    }

    @Override
    public R transcode(final Resource resource) {
        Validate.notNull(resource);
        return this.transcodeTypedResource(getTypedResource(resource));
    }

    private R getTypedResource(final Resource resource) {
        return (R) resource;
    }

    protected abstract R transcodeTypedResource(R typedResource);

    protected Optional<Coding> getTranscoding(final Coding coding) {
        final TSAMResponseStructure tsamTranscodingResponse = terminologyService.getTargetConcept(CodeConcept.from(coding));
        final Coding targetCoding = new Coding(CodeSystem.getUrlBasedOnOid(tsamTranscodingResponse.getCodeSystem()),
                                               tsamTranscodingResponse.getCode(), tsamTranscodingResponse.getDesignation()).setVersion(tsamTranscodingResponse.getCodeSystemVersion());
        targetCoding.setUserSelected(false);

        final TSAMResponseStructure tsamTranslationResponse = terminologyService.getDesignation(CodeConcept.from(targetCoding), "en-GB");
        ToolingExtensions.addLanguageTranslation(targetCoding.getDisplayElement(), "en-GB", tsamTranslationResponse.getDesignation());
        return Optional.of(targetCoding);
    }

    protected Optional<Coding> retrieveCoding(final CodeableConcept codeableConcept) {
        Validate.notNull(codeableConcept);

        return codeableConcept.getCoding()
                              .stream()
                              .filter(Coding::getUserSelected)
                              .findFirst()
                              .or(() -> codeableConcept.getCoding().stream().findFirst());
    }
}
