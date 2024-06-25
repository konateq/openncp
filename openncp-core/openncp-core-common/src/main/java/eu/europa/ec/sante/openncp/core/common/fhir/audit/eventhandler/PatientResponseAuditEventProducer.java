package eu.europa.ec.sante.openncp.core.common.fhir.audit.eventhandler;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import ca.uhn.fhir.util.UrlUtil;
import eu.europa.ec.sante.openncp.common.context.LogContext;
import eu.europa.ec.sante.openncp.core.common.fhir.audit.BalpConstants;
import eu.europa.ec.sante.openncp.core.common.fhir.audit.BalpProfileEnum;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.context.FhirSupportedResourceType;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class PatientResponseAuditEventProducer implements AuditEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientResponseAuditEventProducer.class);

    @Override
    public boolean accepts(final AuditableEvent auditableEvent) {
        final boolean accepts = auditableEvent != null
                && auditableEvent.getRequestDetails().isPatient()
                && auditableEvent.resourceIsOfType(FhirSupportedResourceType.BUNDLE, FhirSupportedResourceType.PATIENT);

        LOGGER.debug("[{}] auditable event [{}]", BooleanUtils.toString(accepts, "Accepted", "Rejected"), auditableEvent);
        return accepts;
    }

    @Override
    public List<AuditEvent> produce(final AuditableEvent auditableEvent) {
        switch (auditableEvent.getRequestDetails().getRestOperationType()) {
            case SEARCH_TYPE:
            case SEARCH_SYSTEM:
            case GET_PAGE:
                return List.of(handleSearch(auditableEvent));
            case VREAD:
            case READ:
                return handleRead(auditableEvent);
            default:
                LOGGER.error("Unsupported fhir REST operation type [{}]", auditableEvent.getRequestDetails().getRestOperationType());
                //TODO what to do here exactly? create a file with the error? we cannot let the audit event create exceptions that will interfere with the response.
                return Collections.emptyList();
        }
    }

    private AuditEvent handleSearch(final AuditableEvent auditableEvent) {
        final Set<String> patientIds = auditableEvent.extractResourceIds();
        if (patientIds.isEmpty()) {
            return createAuditEventCommonQuery(auditableEvent.getRequestDetails(), BalpProfileEnum.BASIC_QUERY);
        } else {
            return createAuditEventPatientQuery(auditableEvent.getRequestDetails(), patientIds);
        }
    }

    private AuditEvent createAuditEventPatientQuery(
            final EuRequestDetails euRequestDetails, final Set<String> patientIds) {
        final BalpProfileEnum profile = BalpProfileEnum.PATIENT_QUERY;
        final AuditEvent auditEvent = createAuditEventCommonQuery(euRequestDetails, profile);
        patientIds.forEach(patientId -> addEntityPatient(auditEvent, patientId));
        return auditEvent;
    }

    private List<AuditEvent> handleRead(final AuditableEvent auditableEvent) {
        return auditableEvent.getResource().map(resource -> {
            final String dataResourceId = auditableEvent.getRequestDetails().createFullyQualifiedResourceReference(resource.getIdElement());
            final Set<String> patientIds = auditableEvent.extractResourceIds();
            final List<AuditEvent> auditEvents1 = new ArrayList<>();
            // If the resource is in the Patient compartment, create one audit event for each compartment owner
            for (final String patientId : patientIds) {
                final AuditEvent auditEvent = createAuditEventPatientRead(auditableEvent.getRequestDetails(), dataResourceId, patientId);
                auditEvents1.add(auditEvent);
            }

            // Otherwise, this is a basic read so create a basic read audit event
            if (patientIds.isEmpty()) {
                final AuditEvent auditEvent = createAuditEventBasicRead(auditableEvent.getRequestDetails(), dataResourceId);
                auditEvents1.add(auditEvent);
            }
            return auditEvents1;
        }).orElse(Collections.emptyList());
    }


    private AuditEvent createAuditEventPatientRead(
            final EuRequestDetails euRequestDetails, final String dataResourceId, final String patientId) {
        final BalpProfileEnum profile = BalpProfileEnum.PATIENT_READ;
        final AuditEvent auditEvent = createAuditEventCommonRead(euRequestDetails, dataResourceId, profile);
        addEntityPatient(auditEvent, patientId);
        return auditEvent;
    }

    private AuditEvent createAuditEventBasicRead(final EuRequestDetails euRequestDetails, final String dataResourceId) {
        return createAuditEventCommonRead(euRequestDetails, dataResourceId, BalpProfileEnum.BASIC_READ);
    }

    private AuditEvent createAuditEventCommonRead(
            final EuRequestDetails euRequestDetails, final String theDataResourceId, final BalpProfileEnum theProfile) {
        final AuditEvent auditEvent = createAuditEventCommon(euRequestDetails, theProfile);
        addEntityData(auditEvent, theDataResourceId);
        return auditEvent;
    }

    private AuditEvent createAuditEventCommonQuery(final EuRequestDetails euRequestDetails, final BalpProfileEnum profile) {
        final AuditEvent auditEvent = createAuditEventCommon(euRequestDetails, profile);
        final RequestDetails hapiRequestDetails = euRequestDetails.getHapiRequestDetails();

        final AuditEvent.AuditEventEntityComponent queryEntity = auditEvent.addEntity();
        queryEntity
                .getType()
                .setSystem(BalpConstants.CS_AUDIT_ENTITY_TYPE)
                .setCode(BalpConstants.CS_AUDIT_ENTITY_TYPE_2_SYSTEM_OBJECT)
                .setDisplay(BalpConstants.CS_AUDIT_ENTITY_TYPE_2_SYSTEM_OBJECT_DISPLAY);
        queryEntity
                .getRole()
                .setSystem(BalpConstants.CS_OBJECT_ROLE)
                .setCode(BalpConstants.CS_OBJECT_ROLE_24_QUERY)
                .setDisplay(BalpConstants.CS_OBJECT_ROLE_24_QUERY_DISPLAY);

        // Description
        final StringBuilder description = new StringBuilder();
        description.append(hapiRequestDetails.getRequestType().name());
        description.append(" ");
        description.append(hapiRequestDetails.getCompleteUrl());
        queryEntity.setDescription(description.toString());

        // Query String
        final StringBuilder queryString = new StringBuilder();
        queryString.append(hapiRequestDetails.getFhirServerBase());
        queryString.append("/");
        queryString.append(hapiRequestDetails.getRequestPath());
        boolean first = true;
        for (final Map.Entry<String, String[]> nextEntrySet :
                hapiRequestDetails.getParameters().entrySet()) {
            for (final String nextValue : nextEntrySet.getValue()) {
                if (first) {
                    queryString.append("?");
                    first = false;
                } else {
                    queryString.append("&");
                }
                queryString.append(UrlUtil.escapeUrlParam(nextEntrySet.getKey()));
                queryString.append("=");
                queryString.append(UrlUtil.escapeUrlParam(nextValue));
            }
        }

        queryEntity.getQueryElement().setValue(queryString.toString().getBytes(StandardCharsets.UTF_8));
        return auditEvent;
    }

    private AuditEvent createAuditEventCommon(final EuRequestDetails euRequestDetails, final BalpProfileEnum theProfile) {
        final RequestDetails requestDetails = euRequestDetails.getHapiRequestDetails();
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
        auditEvent.setRecorded(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        auditEvent.getSource().getObserver().setDisplay(requestDetails.getFhirServerBase());

        final AuditEvent.AuditEventAgentComponent clientAgent = auditEvent.addAgent();
        clientAgent.setWho(getAgentReference(requestDetails));
        clientAgent.getType().addCoding(theProfile.getAgentClientTypeCoding());
        clientAgent.getWho().setDisplay(getNetworkAddress(requestDetails));
        clientAgent
                .getNetwork()
                .setAddress(getNetworkAddress(requestDetails))
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
        userAgent.setWho(getAgentReference(requestDetails));
        userAgent.setRequestor(true);

        final AuditEvent.AuditEventEntityComponent entityTransaction = auditEvent.addEntity();
        entityTransaction
                .getType()
                .setSystem("https://profiles.ihe.net/ITI/BALP/CodeSystem/BasicAuditEntityType")
                .setCode("XrequestId");
        entityTransaction.getWhat().getIdentifier().setValue(LogContext.getCorrelationId());
        return auditEvent;
    }

    private Reference getAgentReference(final RequestDetails theRequestDetails) {
        final String userAgent = StringUtils.defaultString(theRequestDetails.getHeader("User-Agent"), "Unknown User Agent");
        final Reference retVal = new Reference();
        retVal.setDisplay(userAgent);
        return retVal;
    }

    private String getNetworkAddress(final RequestDetails theRequestDetails) {
        String remoteAddr = null;
        if (theRequestDetails instanceof ServletRequestDetails) {
            remoteAddr = ((ServletRequestDetails) theRequestDetails)
                    .getServletRequest()
                    .getRemoteAddr();
        }
        return StringUtils.defaultString(remoteAddr, "UNKNOWN");
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
