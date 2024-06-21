package eu.europa.ec.sante.openncp.core.common.fhir.interceptors;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuRequestDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SimpleServerLoggingInterceptor implements FhirCustomInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServerLoggingInterceptor.class);

    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_PROCESSED)
    public void log_SERVER_INCOMING_REQUEST_PRE_PROCESSED(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        LOGGER.info("Incoming request from hook: (SERVER_INCOMING_REQUEST_PRE_PROCESSED) with httpServletRequest [{}] and httpServletResponse [{}]", httpServletRequest, httpServletResponse);
    }

    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLER_SELECTED)
    public void log_SERVER_INCOMING_REQUEST_PRE_HANDLER_SELECTED(final RequestDetails requestDetails, final ServletRequestDetails servletRequestDetails, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        LOGGER.info("Incoming request hook: (SERVER_INCOMING_REQUEST_PRE_HANDLER_SELECTED) with requestDetails [{}], servletRequestDetails [{}], httpServletRequest [{}] and httpServletResponse [{}]", requestDetails, servletRequestDetails, httpServletRequest, httpServletResponse);
    }


    @Hook(Pointcut.SERVER_INCOMING_REQUEST_POST_PROCESSED)
    public void log_SERVER_INCOMING_REQUEST_POST_PROCESSED(final RequestDetails requestDetails, final ServletRequestDetails servletRequestDetails, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        LOGGER.info("Pointcut SERVER_INCOMING_REQUEST_POST_PROCESSED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.CLIENT_REQUEST)
    public void log_CLIENT_REQUEST(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut CLIENT_REQUEST called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.CLIENT_RESPONSE)
    public void log_CLIENT_RESPONSE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut CLIENT_RESPONSE called with requestdetails [{}]", requestDetails);
    }


    @Hook(Pointcut.SERVER_HANDLE_EXCEPTION)
    public void log_SERVER_HANDLE_EXCEPTION(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_HANDLE_EXCEPTION called with requestdetails [{}]", requestDetails);
    }


    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
    public void log_SERVER_INCOMING_REQUEST_PRE_HANDLED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_INCOMING_REQUEST_PRE_HANDLED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SERVER_PRE_PROCESS_OUTGOING_EXCEPTION)
    public void log_SERVER_PRE_PROCESS_OUTGOING_EXCEPTION(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_PRE_PROCESS_OUTGOING_EXCEPTION called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SERVER_OUTGOING_RESPONSE)
    public void log_SERVER_OUTGOING_RESPONSE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_OUTGOING_RESPONSE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SERVER_OUTGOING_WRITER_CREATED)
    public void log_SERVER_OUTGOING_WRITER_CREATED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_OUTGOING_WRITER_CREATED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SERVER_OUTGOING_GRAPHQL_RESPONSE)
    public void log_SERVER_OUTGOING_GRAPHQL_RESPONSE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_OUTGOING_GRAPHQL_RESPONSE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SERVER_OUTGOING_FAILURE_OPERATIONOUTCOME)
    public void log_SERVER_OUTGOING_FAILURE_OPERATIONOUTCOME(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_OUTGOING_FAILURE_OPERATIONOUTCOME called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SERVER_PROCESSING_COMPLETED_NORMALLY)
    public void log_SERVER_PROCESSING_COMPLETED_NORMALLY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_PROCESSING_COMPLETED_NORMALLY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SERVER_PROCESSING_COMPLETED)
    public void log_SERVER_PROCESSING_COMPLETED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SERVER_PROCESSING_COMPLETED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_RESOURCE_MODIFIED)
    public void log_SUBSCRIPTION_RESOURCE_MODIFIED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_RESOURCE_MODIFIED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_RESOURCE_MATCHED)
    public void log_SUBSCRIPTION_RESOURCE_MATCHED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_RESOURCE_MATCHED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_RESOURCE_DID_NOT_MATCH_ANY_SUBSCRIPTIONS)
    public void log_SUBSCRIPTION_RESOURCE_DID_NOT_MATCH_ANY_SUBSCRIPTIONS(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_RESOURCE_DID_NOT_MATCH_ANY_SUBSCRIPTIONS called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_BEFORE_DELIVERY)
    public void log_SUBSCRIPTION_BEFORE_DELIVERY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_BEFORE_DELIVERY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_AFTER_DELIVERY)
    public void log_SUBSCRIPTION_AFTER_DELIVERY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_AFTER_DELIVERY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_AFTER_DELIVERY_FAILED)
    public void log_SUBSCRIPTION_AFTER_DELIVERY_FAILED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_AFTER_DELIVERY_FAILED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_AFTER_REST_HOOK_DELIVERY)
    public void log_SUBSCRIPTION_AFTER_REST_HOOK_DELIVERY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_AFTER_REST_HOOK_DELIVERY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_BEFORE_REST_HOOK_DELIVERY)
    public void log_SUBSCRIPTION_BEFORE_REST_HOOK_DELIVERY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_BEFORE_REST_HOOK_DELIVERY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_AFTER_MESSAGE_DELIVERY)
    public void log_SUBSCRIPTION_AFTER_MESSAGE_DELIVERY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_AFTER_MESSAGE_DELIVERY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_BEFORE_MESSAGE_DELIVERY)
    public void log_SUBSCRIPTION_BEFORE_MESSAGE_DELIVERY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_BEFORE_MESSAGE_DELIVERY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_BEFORE_PERSISTED_RESOURCE_CHECKED)
    public void log_SUBSCRIPTION_BEFORE_PERSISTED_RESOURCE_CHECKED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_BEFORE_PERSISTED_RESOURCE_CHECKED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_AFTER_PERSISTED_RESOURCE_CHECKED)
    public void log_SUBSCRIPTION_AFTER_PERSISTED_RESOURCE_CHECKED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_AFTER_PERSISTED_RESOURCE_CHECKED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_AFTER_ACTIVE_SUBSCRIPTION_REGISTERED)
    public void log_SUBSCRIPTION_AFTER_ACTIVE_SUBSCRIPTION_REGISTERED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_AFTER_ACTIVE_SUBSCRIPTION_REGISTERED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_AFTER_ACTIVE_SUBSCRIPTION_UNREGISTERED)
    public void log_SUBSCRIPTION_AFTER_ACTIVE_SUBSCRIPTION_UNREGISTERED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_AFTER_ACTIVE_SUBSCRIPTION_UNREGISTERED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_CASCADE_DELETE)
    public void log_STORAGE_CASCADE_DELETE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_CASCADE_DELETE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_TOPIC_BEFORE_PERSISTED_RESOURCE_CHECKED)
    public void log_SUBSCRIPTION_TOPIC_BEFORE_PERSISTED_RESOURCE_CHECKED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_TOPIC_BEFORE_PERSISTED_RESOURCE_CHECKED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.SUBSCRIPTION_TOPIC_AFTER_PERSISTED_RESOURCE_CHECKED)
    public void log_SUBSCRIPTION_TOPIC_AFTER_PERSISTED_RESOURCE_CHECKED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut SUBSCRIPTION_TOPIC_AFTER_PERSISTED_RESOURCE_CHECKED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_INITIATE_BULK_EXPORT)
    public void log_STORAGE_INITIATE_BULK_EXPORT(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_INITIATE_BULK_EXPORT called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_BULK_EXPORT_RESOURCE_INCLUSION)
    public void log_STORAGE_BULK_EXPORT_RESOURCE_INCLUSION(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_BULK_EXPORT_RESOURCE_INCLUSION called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRE_DELETE_EXPUNGE)
    public void log_STORAGE_PRE_DELETE_EXPUNGE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRE_DELETE_EXPUNGE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRE_DELETE_EXPUNGE_PID_LIST)
    public void log_STORAGE_PRE_DELETE_EXPUNGE_PID_LIST(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRE_DELETE_EXPUNGE_PID_LIST called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PREACCESS_RESOURCES)
    public void log_STORAGE_PREACCESS_RESOURCES(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PREACCESS_RESOURCES called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRECHECK_FOR_CACHED_SEARCH)
    public void log_STORAGE_PRECHECK_FOR_CACHED_SEARCH(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRECHECK_FOR_CACHED_SEARCH called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESEARCH_REGISTERED)
    public void log_STORAGE_PRESEARCH_REGISTERED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESEARCH_REGISTERED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESHOW_RESOURCES)
    public void log_STORAGE_PRESHOW_RESOURCES(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESHOW_RESOURCES called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_RESOURCE_CREATED)
    public void log_STORAGE_PRESTORAGE_RESOURCE_CREATED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESTORAGE_RESOURCE_CREATED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_CLIENT_ASSIGNED_ID)
    public void log_STORAGE_PRESTORAGE_CLIENT_ASSIGNED_ID(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESTORAGE_CLIENT_ASSIGNED_ID called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_RESOURCE_UPDATED)
    public void log_STORAGE_PRESTORAGE_RESOURCE_UPDATED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESTORAGE_RESOURCE_UPDATED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_RESOURCE_DELETED)
    public void log_STORAGE_PRESTORAGE_RESOURCE_DELETED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESTORAGE_RESOURCE_DELETED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_CREATED)
    public void log_STORAGE_PRECOMMIT_RESOURCE_CREATED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRECOMMIT_RESOURCE_CREATED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_UPDATED)
    public void log_STORAGE_PRECOMMIT_RESOURCE_UPDATED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRECOMMIT_RESOURCE_UPDATED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_DELETED)
    public void log_STORAGE_PRECOMMIT_RESOURCE_DELETED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRECOMMIT_RESOURCE_DELETED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_TRANSACTION_PROCESSING)
    public void log_STORAGE_TRANSACTION_PROCESSING(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_TRANSACTION_PROCESSING called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_TRANSACTION_PROCESSED)
    public void log_STORAGE_TRANSACTION_PROCESSED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_TRANSACTION_PROCESSED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_TRANSACTION_WRITE_OPERATIONS_PRE)
    public void log_STORAGE_TRANSACTION_WRITE_OPERATIONS_PRE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_TRANSACTION_WRITE_OPERATIONS_PRE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_TRANSACTION_WRITE_OPERATIONS_POST)
    public void log_STORAGE_TRANSACTION_WRITE_OPERATIONS_POST(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_TRANSACTION_WRITE_OPERATIONS_POST called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_DELETE_CONFLICTS)
    public void log_STORAGE_PRESTORAGE_DELETE_CONFLICTS(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESTORAGE_DELETE_CONFLICTS called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_EXPUNGE_RESOURCE)
    public void log_STORAGE_PRESTORAGE_EXPUNGE_RESOURCE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESTORAGE_EXPUNGE_RESOURCE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_EXPUNGE_EVERYTHING)
    public void log_STORAGE_PRESTORAGE_EXPUNGE_EVERYTHING(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PRESTORAGE_EXPUNGE_EVERYTHING called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_CREATE)
    public void log_STORAGE_PARTITION_IDENTIFY_CREATE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PARTITION_IDENTIFY_CREATE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_READ)
    public void log_STORAGE_PARTITION_IDENTIFY_READ(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PARTITION_IDENTIFY_READ called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_ANY)
    public void log_STORAGE_PARTITION_IDENTIFY_ANY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PARTITION_IDENTIFY_ANY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PARTITION_CREATED)
    public void log_STORAGE_PARTITION_CREATED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PARTITION_CREATED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_PARTITION_SELECTED)
    public void log_STORAGE_PARTITION_SELECTED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_PARTITION_SELECTED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_VERSION_CONFLICT)
    public void log_STORAGE_VERSION_CONFLICT(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_VERSION_CONFLICT called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.VALIDATION_COMPLETED)
    public void log_VALIDATION_COMPLETED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut VALIDATION_COMPLETED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_BEFORE_PERSISTED_RESOURCE_CHECKED)
    public void log_MDM_BEFORE_PERSISTED_RESOURCE_CHECKED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_BEFORE_PERSISTED_RESOURCE_CHECKED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_AFTER_PERSISTED_RESOURCE_CHECKED)
    public void log_MDM_AFTER_PERSISTED_RESOURCE_CHECKED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_AFTER_PERSISTED_RESOURCE_CHECKED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_POST_CREATE_LINK)
    public void log_MDM_POST_CREATE_LINK(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_POST_CREATE_LINK called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_POST_UPDATE_LINK)
    public void log_MDM_POST_UPDATE_LINK(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_POST_UPDATE_LINK called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_POST_MERGE_GOLDEN_RESOURCES)
    public void log_MDM_POST_MERGE_GOLDEN_RESOURCES(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_POST_MERGE_GOLDEN_RESOURCES called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_POST_LINK_HISTORY)
    public void log_MDM_POST_LINK_HISTORY(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_POST_LINK_HISTORY called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_POST_NOT_DUPLICATE)
    public void log_MDM_POST_NOT_DUPLICATE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_POST_NOT_DUPLICATE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_CLEAR)
    public void log_MDM_CLEAR(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut MDM_CLEAR called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.MDM_SUBMIT)
    public void log_MDM_SUBMIT(final RequestDetails requestDetails) {
        final EuRequestDetails euRequestDetails = EuRequestDetails.of(requestDetails);
        LOGGER.info("Pointcut MDM_SUBMIT called with requestdetails [{}]", euRequestDetails);
    }

    @Hook(Pointcut.JPA_RESOLVE_CROSS_PARTITION_REFERENCE)
    public void log_JPA_RESOLVE_CROSS_PARTITION_REFERENCE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_RESOLVE_CROSS_PARTITION_REFERENCE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_INFO)
    public void log_JPA_PERFTRACE_INFO(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_INFO called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_WARNING)
    public void log_JPA_PERFTRACE_WARNING(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_WARNING called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_SEARCH_FIRST_RESULT_LOADED)
    public void log_JPA_PERFTRACE_SEARCH_FIRST_RESULT_LOADED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_SEARCH_FIRST_RESULT_LOADED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_SEARCH_SELECT_COMPLETE)
    public void log_JPA_PERFTRACE_SEARCH_SELECT_COMPLETE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_SEARCH_SELECT_COMPLETE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_SEARCH_FAILED)
    public void log_JPA_PERFTRACE_SEARCH_FAILED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_SEARCH_FAILED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_SEARCH_PASS_COMPLETE)
    public void log_JPA_PERFTRACE_SEARCH_PASS_COMPLETE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_SEARCH_PASS_COMPLETE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_INDEXSEARCH_QUERY_COMPLETE)
    public void log_JPA_PERFTRACE_INDEXSEARCH_QUERY_COMPLETE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_INDEXSEARCH_QUERY_COMPLETE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_SEARCH_REUSING_CACHED)
    public void log_JPA_PERFTRACE_SEARCH_REUSING_CACHED(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_SEARCH_REUSING_CACHED called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_SEARCH_COMPLETE)
    public void log_JPA_PERFTRACE_SEARCH_COMPLETE(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_SEARCH_COMPLETE called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_SEARCH_FOUND_ID)
    public void log_JPA_PERFTRACE_SEARCH_FOUND_ID(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_SEARCH_FOUND_ID called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.JPA_PERFTRACE_RAW_SQL)
    public void log_JPA_PERFTRACE_RAW_SQL(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut JPA_PERFTRACE_RAW_SQL called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.STORAGE_BINARY_ASSIGN_BLOB_ID_PREFIX)
    public void log_STORAGE_BINARY_ASSIGN_BLOB_ID_PREFIX(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut STORAGE_BINARY_ASSIGN_BLOB_ID_PREFIX called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.TEST_RB)
    public void log_TEST_RB(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut TEST_RB called with requestdetails [{}]", requestDetails);
    }

    @Hook(Pointcut.TEST_RO)
    public void log_TEST_RO(final RequestDetails requestDetails) {
        LOGGER.info("Pointcut TEST_RO called with requestdetails [{}]", requestDetails);
    }
}
