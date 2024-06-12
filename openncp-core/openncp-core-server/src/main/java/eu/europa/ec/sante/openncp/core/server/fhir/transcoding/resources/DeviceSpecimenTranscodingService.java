package eu.europa.ec.sante.openncp.core.server.fhir.transcoding.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DeviceSpecimenMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.hl7.fhir.r4.model.ResourceType;

public class DeviceSpecimenTranscodingService extends AbstractResourceTranscodingService<DeviceSpecimenMyHealthEu> {

    public DeviceSpecimenTranscodingService(final TerminologyService terminologyService) {
        super(terminologyService);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Device;
    }

    @Override
    public DeviceSpecimenMyHealthEu transcodeTypedResource(final DeviceSpecimenMyHealthEu deviceSpecimenMyHealthEu) {

        transcodeCodeableConcept(deviceSpecimenMyHealthEu.getType());

        return deviceSpecimenMyHealthEu;
    }
}
