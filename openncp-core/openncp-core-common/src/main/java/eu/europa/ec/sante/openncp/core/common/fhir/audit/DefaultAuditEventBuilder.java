package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuditEventBuilder implements AuditEventBuilder {
    @Override
    public AuditEvent build(final AuditEventData auditEventData) {
        Validate.notNull(auditEventData, "Audit event data must not be null.");

        // This is based on the code at https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-storage/src/main/java/ca/uhn/fhir/storage/interceptor/balp/BalpAuditCaptureInterceptor.java#L173
        RestOperationTypeEnum restOperationType = auditEventData.getRestOperationType();
        if (restOperationType == RestOperationTypeEnum.GET_PAGE) {
            restOperationType = RestOperationTypeEnum.SEARCH_TYPE;
        }

        final AuditEvent auditEvent = new AuditEvent();
        final BalpProfileEnum eventProfile = auditEventData.getProfile();
        auditEvent.getMeta().addProfile(eventProfile.getProfileUrl());
        auditEvent
                .getText()
                .setDiv(new XhtmlNode().setValue("<div>Audit Event</div>"))
                .setStatus(org.hl7.fhir.r4.model.Narrative.NarrativeStatus.GENERATED);
        auditEvent
                .getType()
                .setSystem(BalpConstants.CS_AUDIT_EVENT_TYPE)
                .setCode("rest")
                .setDisplay("Restful Operation");
        auditEvent
                .addSubtype()
                .setSystem(BalpConstants.CS_RESTFUL_INTERACTION)
                .setCode(restOperationType.getCode())
                .setDisplay(restOperationType.getCode());
        auditEvent.setAction(eventProfile.getAction());
        auditEvent.setOutcome(AuditEvent.AuditEventOutcome._0);
        auditEvent.getRecordedElement().setValueAsString(auditEventData.getMetaData().getRecordDateTime().toString());

        auditEvent.getSource().getObserver().setDisplay(auditEventData.getFhirServerBase());

        auditEventData.getParticipants().forEach(participantData -> {
            final AuditEvent.AuditEventAgentComponent agent = auditEvent.addAgent();
            final Coding agentTypeCode;
            if (participantData.isRequestor()) {
                agentTypeCode = eventProfile.getAgentClientTypeCoding();
            } else {
                agentTypeCode = eventProfile.getAgentServerTypeCoding();
            }
            agent.getType().addCoding(agentTypeCode);
            participantData.getDisplay().ifPresent(participantDisplay -> agent.getWho().setDisplay(participantDisplay));
            agent.getWho().getIdentifier().setValue(participantData.getId());
            agent.setRequestor(participantData.isRequestor());
            participantData.getNetwork().ifPresent(participantNetwork -> agent.getNetwork()
                    .setAddress(participantNetwork)
                    .setType(BalpConstants.AUDIT_EVENT_AGENT_NETWORK_TYPE_IP_ADDRESS));

        });

//        final AuditEvent.AuditEventAgentComponent userAgent = auditEvent.addAgent();
//        userAgent
//                .getType()
//                .addCoding()
//                .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
//                .setCode("IRCP")
//                .setDisplay("information recipient");
//        userAgent.setWho(getAgentReference(requestDetails));
//        userAgent.setRequestor(true);
        
        final AuditEvent.AuditEventEntityComponent entityCorrelationId = auditEvent.addEntity();
        entityCorrelationId
                .getType()
                .setSystem("https://profiles.ihe.net/ITI/BALP/CodeSystem/BasicAuditEntityType")
                .setCode("X-Correlation-ID");
        entityCorrelationId.getWhat().getIdentifier().setValue(auditEventData.getMetaData().getCorrelationId());

        auditEventData.getEntities().forEach(entityData -> {
            final AuditEvent.AuditEventEntityComponent entity = auditEvent.addEntity();
            entity.getType()
                    .setSystem(BalpConstants.CS_AUDIT_ENTITY_TYPE)
                    .setCode(entityData.getType().getCode())
                    .setDisplay(entityData.getType().getDisplay().orElse(null));
            entity.getRole()
                    .setSystem(BalpConstants.CS_OBJECT_ROLE)
                    .setCode(entityData.getRole().getCode())
                    .setDisplay(entityData.getRole().getDisplay().orElse(null));
            entity.getWhat().setReference(entityData.getId());
            entityData.getDisplay().ifPresent(entityDisplay -> entity.getWhat().setDisplay(entityDisplay));

        });

        return auditEvent;
    }
}
