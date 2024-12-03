package eu.europa.ec.sante.openncp.core.common.openehr.service;

import eu.europa.ec.sante.openncp.core.common.openehr.model.AdhocQueryExecute;
import eu.europa.ec.sante.openncp.core.common.openehr.model.ResultSet;
import org.springframework.web.context.request.WebRequest;

/**
 * This service is responsible for querying an openEHR system.
 *
 * @author Renaud Subiger
 * @since 9.0
 */
public interface QueryService {

    /**
     * Executes an ad-hoc AQL query.
     *
     * @param adhocQueryExecute the ad-hoc query to execute
     * @param request           the web request
     * @return the result set
     */
    ResultSet executeAdhocQuery(AdhocQueryExecute adhocQueryExecute, WebRequest request);
}
