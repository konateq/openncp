package eu.europa.ec.sante.openncp.core.common.openehr.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an ad-hoc query execution.
 *
 * @author Renaud Subiger
 * @since 9.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdhocQueryExecute {

    @NotBlank
    @JsonProperty("q")
    private final String query;

    @JsonProperty("offset")
    private Integer offset = 0;

    @JsonProperty("fetch")
    private Integer fetch;

    @JsonProperty("query_parameters")
    private Map<String, Object> queryParameters = new LinkedHashMap<>();

    @JsonCreator
    public AdhocQueryExecute(@JsonProperty(value = "q", required = true) String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getFetch() {
        return fetch;
    }

    public void setFetch(Integer fetch) {
        this.fetch = fetch;
    }

    public Map<String, Object> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, Object> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public void addQueryParameters(String key, Object value) {
        queryParameters.put(key, value);
    }

    @Override
    public String toString() {
        return "AdhocQueryExecute{" +
                "query='" + query + '\'' +
                ", offset=" + offset +
                ", fetch=" + fetch +
                ", queryParameters=" + queryParameters +
                '}';
    }
}
