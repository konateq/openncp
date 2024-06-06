package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.SpecimenMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.springframework.stereotype.Service;

@Service
public class SpecimenTranslationService extends AbstractTranslationService implements IDomainTranslationService<SpecimenMyHealthEu> {

    public SpecimenTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public SpecimenMyHealthEu translate(SpecimenMyHealthEu specimenMyHealthEu, String targetLanguage) {

        /** Type **/
        addTranslation(specimenMyHealthEu.getType(), targetLanguage);

        /** Collection - BodySite **/
        addTranslation(specimenMyHealthEu.getCollection().getBodySite(), targetLanguage);

        return specimenMyHealthEu;
    }
}
