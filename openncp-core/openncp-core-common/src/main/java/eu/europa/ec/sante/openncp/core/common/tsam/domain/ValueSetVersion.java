package eu.europa.ec.sante.openncp.core.common.tsam.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A Value Set Version represents a point in time view of a Value Set.
 * The Value Set Version identifies the set of concepts that are available in the value set for any specific version
 * of the value set.
 */
@Entity
@Table(name="value_set_version")
public class ValueSetVersion {

    /**
     * A version identifier that uniquely identifies each version of a value set
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * A name that is assigned to the value set by the terminology provider at the time of the version being issued.
     */
    @Column(name="version_name")
    private String versionName;

    /**
     * Identification of the previous version of the value set, which enables tracking of sequencing of versions,
     * and identification of missing versions on a server instance.
     */
    @OneToOne
    @JoinColumn(name = "previous_version_id")
    private ValueSetVersion previousVersion;

    /**
     * A start date when the version is deemed to be valid for use.
     */
    @Column(name="effective_date")
    private Date effectiveDate;

    /**
     * A date when the version became available within a particular domain
     */
    @Column(name="release_date")
    private Date releaseDate;


    /**
     * A status to identify the state of the Value Set Version<br>
     * TODO (enumeration or catalog) List of statuses:<br>
     * <li>current version of value set currently in use</li>
     * <li>retired past version of value set</li>
     * <li>not in use prepared version which is still not in use</li>
     */
    @Column(name="status")
    private String status;

    /**
     * A status date to identify the date the status was set to its current value
     */
    @Column(name="status_date")
    private Date statusDate;

    /**
     * A description that describes the Value Set Version
     */
    @Column(name="description", length = 4000)
    private String description;

    /**
     * ValueSet binding<br>
     * Identification of a code system to which version is related
     */
    @ManyToOne
    @JoinColumn(name = "value_set_id")
    private ValueSet valueSet;

    /**
     * CodeSystemConcept binding
     */
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "x_concept_value_set",
            joinColumns = { @JoinColumn(name = "value_set_version_id") },
            inverseJoinColumns = { @JoinColumn(name = "code_system_concept_id") }
    )
    private List<CodeSystemConcept> concepts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ValueSet getValueSet() {
        return valueSet;
    }

    public void setValueSet(ValueSet valueSet) {
        this.valueSet = valueSet;
    }

    public List<CodeSystemConcept> getConcepts() {

        if (concepts == null) {
            concepts = new ArrayList<>();
        }
        return concepts;
    }

    public void setConcepts(List<CodeSystemConcept> concepts) {
        this.concepts = concepts;
    }

    public void addConcept(CodeSystemConcept codeSystemConcept) {
        getConcepts().add(codeSystemConcept);
    }

    public ValueSetVersion getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(ValueSetVersion previousVersion) {
        this.previousVersion = previousVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueSetVersion that = (ValueSetVersion) o;
        return id == that.id && Objects.equals(versionName, that.versionName) && Objects.equals(previousVersion, that.previousVersion) && Objects.equals(effectiveDate, that.effectiveDate) && Objects.equals(releaseDate, that.releaseDate) && Objects.equals(status, that.status) && Objects.equals(statusDate, that.statusDate) && Objects.equals(description, that.description) && Objects.equals(valueSet, that.valueSet) && Objects.equals(concepts, that.concepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, versionName, previousVersion, effectiveDate, releaseDate, status, statusDate, description, valueSet, concepts);
    }
}
