package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.SpecimenMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class SpecimenResourceTranslationService extends AbstractResourceTranslationService<SpecimenMyHealthEu> {

    public SpecimenResourceTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public SpecimenMyHealthEu translateTypedResource(SpecimenMyHealthEu specimenMyHealthEu, String targetLanguage) {

        /** Type **/
        addTranslation(specimenMyHealthEu.getType(), targetLanguage);

        /** Collection - BodySite **/
        addTranslation(specimenMyHealthEu.getCollection().getBodySite(), targetLanguage);

        return specimenMyHealthEu;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Specimen;
    }
}
