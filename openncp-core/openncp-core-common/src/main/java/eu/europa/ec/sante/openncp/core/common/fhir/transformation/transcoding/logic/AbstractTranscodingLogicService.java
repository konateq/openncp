package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

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

public abstract class AbstractTranscodingLogicService<R extends Resource> implements TranscodingLogicService<R> {

    private final TerminologyService terminologyService;

    public AbstractTranscodingLogicService(final TerminologyService terminologyService) {
        this.terminologyService = Validate.notNull(terminologyService);
    }

    @Override
    public boolean accepts(final Resource resource) {
        Validate.notNull(resource);

        return resource.getResourceType() == getResourceType();
    }

    @Override
    public void transcode(final Resource resource) {
        Validate.notNull(resource);

        this.transcodeTypedResource(getTypedResource(resource));
    }

    private R getTypedResource(final Resource resource) {
        return (R) resource;
    }

    protected abstract void transcodeTypedResource(R typedResource);

    protected Optional<Coding> getTranscoding(final Coding coding) {
        final TSAMResponseStructure tsamTranscodingResponse = terminologyService.getTargetConcept(CodeConcept.from(coding));
        final Coding targetCoding = new Coding(CodeSystem.getUrlBasedOnOid(tsamTranscodingResponse.getCodeSystem()),
                                               tsamTranscodingResponse.getCode(), tsamTranscodingResponse.getDesignation());
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
