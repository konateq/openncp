package eu.europa.ec.sante.openncp.api.common.openehr;

import eu.europa.ec.sante.openncp.core.common.openehr.model.AdhocQueryExecute;
import eu.europa.ec.sante.openncp.core.common.openehr.model.ResultSet;
import eu.europa.ec.sante.openncp.core.common.openehr.service.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

/**
 * This controller is responsible for querying an openEHR system.
 *
 * @author Renaud Subiger
 * @see <a href="https://specifications.openehr.org/releases/ITS-REST/Release-1.0.3/query.html">openEHR REST specifications</a>
 * @since 9.0
 */
@RestController
@RequestMapping(path = "/openehr/v1/query")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * Executes an ad-hoc query.
     *
     * @param adhocQueryExecute the query to execute
     * @param request           the web request
     * @return the result set
     */
    @PostMapping(path = "/aql")
    public ResponseEntity<ResultSet> executeAdhocQuery(@RequestBody @Valid AdhocQueryExecute adhocQueryExecute,
                                                       WebRequest request) {
        ResultSet resultSet = queryService.executeAdhocQuery(adhocQueryExecute, request);
        return ResponseEntity.ok(resultSet);
    }
}
