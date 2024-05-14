package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="code_system_concept")
public class CodeSystemConcept {

    /**
     * An identifier that uniquely identified the Concept within Code System
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    /**
     * A concept code that according to terminology best practices is unique within the context of the Code System,
     * although some code systems do allow reuse of codes over time.
     */
    @Column(name="code")
    protected String code;

    /**
     * A definition of concept's meaning described in text
     */
    @Column(name="definition", length = 4000)
    protected String definition;

    /**
     * A status to identify the state of the Code System Concept<br>
     * TODO (enumeration or catalog). List of statuses:<br>
     * <li>Current</li> <li>Non-Current</li> <li>Duplicated</li> <li>Outdated</li>
     * <li>Erroneous</li> <li>Limited</li> <li>Inappropriate</li> <li>Concept
     * non-current</li> <li>Moved elsewhere</li> <li>Pending move</li>
     */
    @Column(name="status")
    protected String status;

    /**
     * A status date to identify the date the status was set to its current value
     */
    @Column(name="status_date")
    protected Date statusDate;


    /**
     * ValueSetVersion binding
     */
    @ManyToMany(mappedBy = "concepts")
    protected List<ValueSetVersion> valueSetVersions;

    /**
     * Code system version in which concept was introduced first
     */
    @ManyToOne
    @JoinColumn(name = "code_system_version_id")
    protected CodeSystemVersion codeSystemVersion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codeSystemConcept")
    private List<Designation> designations;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<ValueSetVersion> getValueSetVersions() {

        if (valueSetVersions == null) {
            valueSetVersions = new ArrayList<>();
        }
        return valueSetVersions;
    }

    public void setValueSetVersion(List<ValueSetVersion> valueSetVersions) {
        this.valueSetVersions = valueSetVersions;
    }

    public void addValueSetVersion(ValueSetVersion valueSetVersion) {
        getValueSetVersions().add(valueSetVersion);
        valueSetVersion.addConcept(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CodeSystemVersion getCodeSystemVersion() {
        return codeSystemVersion;
    }

    public void setCodeSystemVersion(CodeSystemVersion codeSystemVersion) {
        this.codeSystemVersion = codeSystemVersion;
    }

    public List<Designation> getDesignations() {

        if (designations == null) {
            designations = new ArrayList<>();
        }
        return designations;
    }

    public void setDesignations(List<Designation> designations) {
        this.designations = designations;
    }

    public void addDesignation(Designation designation) {
        getDesignations().add(designation);
        designation.setConcept(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeSystemConcept that = (CodeSystemConcept) o;
        return id == that.id && Objects.equals(code, that.code) && Objects.equals(definition, that.definition) && Objects.equals(status, that.status) && Objects.equals(statusDate, that.statusDate) && Objects.equals(valueSetVersions, that.valueSetVersions) && Objects.equals(codeSystemVersion, that.codeSystemVersion) && Objects.equals(designations, that.designations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, definition, status, statusDate, valueSetVersions, codeSystemVersion, designations);
    }
}
