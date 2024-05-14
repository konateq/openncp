package eu.europa.ec.sante.openncp.core.common.fhir.transformation.transcoding.logic;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils.ToolingExtensions;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.service.IFHIRTerminologyService;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.cs.CodeSystem;
import eu.europa.ec.sante.openncp.core.common.fhir.tsam.response.TSAMResponse;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Resource;

import java.util.Optional;

public abstract class AbstractTranscodingLogicService<R extends Resource> implements TranscodingLogicService<R> {

    private final IFHIRTerminologyService fhirTerminologyService;

    public AbstractTranscodingLogicService(final IFHIRTerminologyService fhirTerminologyService) {
        this.fhirTerminologyService = Validate.notNull(fhirTerminologyService);
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
        final TSAMResponse tsamTranscodingResponse = fhirTerminologyService.getConceptByCode(coding);
        final Coding targetCoding = new Coding(CodeSystem.getUrlBasedOnOid(tsamTranscodingResponse.getCodeSystem()),
                                               tsamTranscodingResponse.getCode(), tsamTranscodingResponse.getDesignation());
        targetCoding.setUserSelected(false);
        final TSAMResponse tsamTranslationResponse = fhirTerminologyService.getDesignationForConcept(targetCoding, "en-GB");
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
