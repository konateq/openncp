package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DeviceSpecimenMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class DeviceSpecimenResourceTranslationService extends AbstractResourceTranslationService<DeviceSpecimenMyHealthEu>  {

    public DeviceSpecimenResourceTranslationService(TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public DeviceSpecimenMyHealthEu translateTypedResource(DeviceSpecimenMyHealthEu deviceSpecimenMyHealthEu, String targetLanguage) {

        /** Type **/
        addTranslation(deviceSpecimenMyHealthEu.getType(), targetLanguage);

        return deviceSpecimenMyHealthEu;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Device;
    }

}