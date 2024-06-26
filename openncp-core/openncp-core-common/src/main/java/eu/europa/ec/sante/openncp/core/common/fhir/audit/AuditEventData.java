package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import eu.europa.ec.sante.openncp.common.immutables.Domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Domain
public interface AuditEventData {
    RestOperationTypeEnum getRestOperationType();

    BalpProfileEnum getProfile();

    ZonedDateTime getRecordData();

    String getFhirServerBase();

    List<ParticipantData> getParticipants();

    List<EntityData> getEntities();

    @Domain
    interface ParticipantData {
        //AuditEvent.agent.who.identifier
        String getId();

        //AuditEvent.agent.who.display
        Optional<String> getDisplay();

        // [KJW] AuditEvent.agent.role, RoleIDCode in IHE
        String getRoleCode();

        boolean isRequestor();

        Optional<String> getNetwork();
    }

    @Domain
    interface EntityData {
        String getId();

        Optional<String> getDisplay();

        EntityType getType();

        EntityRole getRole();

        @Domain
        interface EntityType {
            String getCode();

            Optional<String> getDisplay();
        }

        @Domain
        interface EntityRole {
            String getCode();

            Optional<String> getDisplay();
        }
    }
}
