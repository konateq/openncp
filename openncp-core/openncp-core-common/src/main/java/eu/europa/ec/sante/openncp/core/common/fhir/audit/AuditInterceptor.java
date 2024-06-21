package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.context.FhirSupportedResourceType;
import eu.europa.ec.sante.openncp.core.common.fhir.interceptors.FhirCustomInterceptor;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Interceptor(order = Integer.MIN_VALUE + 2)
@Component
public class AuditInterceptor implements FhirCustomInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditInterceptor.class);
    private static final String NETWORK_NOT_RESOLVED_MESSAGE = "Network could not be resolved";
    private final FhirContext fhirContext;

    public AuditInterceptor(final FhirContext fhirContext) {
        this.fhirContext = Validate.notNull(fhirContext, "fhirContext cannot be null.");
    }

    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
    public void auditIncomingRequestPreHandled(final RequestDetails requestDetails, final ServletRequestDetails servletRequestDetails, final RestOperationTypeEnum restOperationTypeEnum) {
        final EuRequestDetails euRequestDetails = EuRequestDetails.of(requestDetails);
        final Boolean isPatient = euRequestDetails.getSupportedResourceType().filter(fhirSupportedResourceType -> FhirSupportedResourceType.PATIENT == fhirSupportedResourceType).isPresent();

        LOGGER.info("Incoming request [{}], isPatient [{}]", euRequestDetails, isPatient);
        if (isPatient) {
            final AuditEvent auditEventPatientRead = createAuditEventPatientRead(servletRequestDetails);
            LOGGER.info("Auditevent [{}]", fhirContext.newJsonParser().encodeResourceToString(auditEventPatientRead));
        }
    }

    private AuditEvent createAuditEventPatientRead(final ServletRequestDetails servletRequestDetails) {
        final BalpProfileEnum profile = BalpProfileEnum.PATIENT_READ;
        final AuditEvent auditEvent = createAuditEventCommonRead(servletRequestDetails, profile);
        addEntityPatient(auditEvent, "temp patientId");
        return auditEvent;
    }

    private AuditEvent createAuditEventCommonRead(final ServletRequestDetails servletRequestDetails, final BalpProfileEnum theProfile) {
        final AuditEvent auditEvent = createAuditEventCommon(servletRequestDetails, theProfile);
        addEntityData(auditEvent, "temp dataResourceId");
        return auditEvent;
    }

    private AuditEvent createAuditEventCommon(final ServletRequestDetails requestDetails, final BalpProfileEnum theProfile) {
        RestOperationTypeEnum restOperationType = requestDetails.getRestOperationType();
        if (restOperationType == RestOperationTypeEnum.GET_PAGE) {
            restOperationType = RestOperationTypeEnum.SEARCH_TYPE;
        }

        final AuditEvent auditEvent = new AuditEvent();
        auditEvent.getMeta().addProfile(theProfile.getProfileUrl());
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
        auditEvent.setAction(theProfile.getAction());
        auditEvent.setOutcome(AuditEvent.AuditEventOutcome._0);
        auditEvent.setRecorded(new Date());

        auditEvent.getSource().getObserver().setDisplay(requestDetails.getFhirServerBase());

        final AuditEvent.AuditEventAgentComponent clientAgent = auditEvent.addAgent();
        clientAgent.setWho(getAgentClientWho(requestDetails));
        clientAgent.getType().addCoding(theProfile.getAgentClientTypeCoding());
        clientAgent.getWho().setDisplay(getNetworkAddress(requestDetails).orElse(NETWORK_NOT_RESOLVED_MESSAGE));
        clientAgent
                .getNetwork()
                .setAddress(getNetworkAddress(requestDetails).orElse(NETWORK_NOT_RESOLVED_MESSAGE))
                .setType(BalpConstants.AUDIT_EVENT_AGENT_NETWORK_TYPE_IP_ADDRESS);
        clientAgent.setRequestor(false);

        final AuditEvent.AuditEventAgentComponent serverAgent = auditEvent.addAgent();
        serverAgent.getType().addCoding(theProfile.getAgentServerTypeCoding());
        serverAgent.getWho().setDisplay(requestDetails.getFhirServerBase());
        serverAgent.getNetwork().setAddress(requestDetails.getFhirServerBase());
        serverAgent.setRequestor(false);

        final AuditEvent.AuditEventAgentComponent userAgent = auditEvent.addAgent();
        userAgent
                .getType()
                .addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
                .setCode("IRCP")
                .setDisplay("information recipient");
        userAgent.setWho(getAgentUserWho(requestDetails));
        userAgent.setRequestor(true);

        final AuditEvent.AuditEventEntityComponent entityTransaction = auditEvent.addEntity();
        entityTransaction
                .getType()
                .setSystem("https://profiles.ihe.net/ITI/BALP/CodeSystem/BasicAuditEntityType")
                .setCode("XrequestId");
        entityTransaction.getWhat().getIdentifier().setValue(requestDetails.getRequestId());
        return auditEvent;
    }

    private Reference getAgentUserWho(final ServletRequestDetails theRequestDetails) {
        return null;
    }

    private Reference getAgentClientWho(final ServletRequestDetails theRequestDetails) {
        return null;
    }

    private Optional<String> getNetworkAddress(final RequestDetails theRequestDetails) {
        if (theRequestDetails instanceof ServletRequestDetails) {
            final String remoteAddr = ((ServletRequestDetails) theRequestDetails)
                    .getServletRequest()
                    .getRemoteAddr();
            return Optional.ofNullable(remoteAddr);
        }
        return Optional.empty();
    }

    private static void addEntityPatient(final AuditEvent theAuditEvent, final String thePatientId) {
        final AuditEvent.AuditEventEntityComponent entityPatient = theAuditEvent.addEntity();
        entityPatient
                .getType()
                .setSystem(BalpConstants.CS_AUDIT_ENTITY_TYPE)
                .setCode(BalpConstants.CS_AUDIT_ENTITY_TYPE_1_PERSON)
                .setDisplay(BalpConstants.CS_AUDIT_ENTITY_TYPE_1_PERSON_DISPLAY);
        entityPatient
                .getRole()
                .setSystem(BalpConstants.CS_OBJECT_ROLE)
                .setCode(BalpConstants.CS_OBJECT_ROLE_1_PATIENT)
                .setDisplay(BalpConstants.CS_OBJECT_ROLE_1_PATIENT_DISPLAY);
        entityPatient.getWhat().setReference(thePatientId);
    }

    private static void addEntityData(final AuditEvent theAuditEvent, final String theDataResourceId) {
        final AuditEvent.AuditEventEntityComponent entityData = theAuditEvent.addEntity();
        entityData
                .getType()
                .setSystem(BalpConstants.CS_AUDIT_ENTITY_TYPE)
                .setCode(BalpConstants.CS_AUDIT_ENTITY_TYPE_2_SYSTEM_OBJECT)
                .setDisplay(BalpConstants.CS_AUDIT_ENTITY_TYPE_2_SYSTEM_OBJECT_DISPLAY);
        entityData
                .getRole()
                .setSystem(BalpConstants.CS_OBJECT_ROLE)
                .setCode(BalpConstants.CS_OBJECT_ROLE_4_DOMAIN_RESOURCE)
                .setDisplay(BalpConstants.CS_OBJECT_ROLE_4_DOMAIN_RESOURCE_DISPLAY);
        entityData.getWhat().setReference(theDataResourceId);
    }


}
