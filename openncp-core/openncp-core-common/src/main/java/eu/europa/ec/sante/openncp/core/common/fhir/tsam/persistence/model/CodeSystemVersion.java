package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Code systems are generally not static entities and change over time.
 * A CodeSystemVersion is a static snapshot of a CodeSystem at a given point of time (and in force for a period until
 * the subsequent version supersedes it), and enables identification of the versions of the code system in which any
 * given concept can be found.
 */
@Entity
@Table(name="code_system_version")
public class CodeSystemVersion {

    /**
     * A version identifier that uniquely identifies each version of a Code System
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * A name that is the official name of the code system as assigned by the terminology provider at the time of
     * the version being issued.
     */
    @Column(name="full_name")
    private String fullName;

    /**
     * A name that the Code system is normally referred to for the life of this version
     */
    @Column(name="local_name")
    private String localName;

    /**
     * Identification of the previous version of the code system, which enables tracking of sequencing of versions,
     * and identification of missing versions on a server instance
     */
    @OneToOne
    @JoinColumn(name = "previous_version_id")
    private CodeSystemVersion previousVersion;

    /**
     * A start date when the version is deemed to be valid for use.
     */
    @Column(name="effective_date")
    private Date effectiveDate;

    /**
     * A date when the version of the Code System became available within a particular domain
     */
    @Column(name="release_date")
    private Date releaseDate;

    /**
     * A status to identify the state of the Code System Version.<br>
     * TODO (enumeration OR catalog) List of status:<br>
     * <li>current version of code system currently in use</li>
     * <li>retired past version of code system</li>
     * <li>not in use  prepared version which is still not in use</li>
     */
    @Column(name="status")
    private String status;

    /**
     * A status date to identify the date the status was set to its current value
     */
    @Column(name="status_date")
    private Date statusDate;

    /**
     * A description that describes the Code System Version
     */
    @Column(name="description", length = 4000)
    private String description;

    /**
     * Copyright information (pertaining to the release (version) of the Code System
     */
    @Column(name="copyright")
    private String copyright;

    /**
     * An attribute that identifies the authority or source of the code system in this version.
     * (i.e. IHTSDO - International Health Terminology Standards Development Organisation).
     */
    @Column(name="source")
    private String source;

    /**
     * binding to CodeSystem.<br>
     * Identification of a code system to which version is related
     */
    @ManyToOne
    @JoinColumn(name = "code_system_id")
    private CodeSystem codeSystem;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codeSystemVersion")
    private List<CodeSystemConcept> codeSystemConcepts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
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

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public CodeSystem getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(CodeSystem codeSystem) {
        this.codeSystem = codeSystem;
    }

    public List<CodeSystemConcept> getConcepts() {

        if (codeSystemConcepts == null) {
            codeSystemConcepts = new ArrayList<>();
        }
        return codeSystemConcepts;
    }

    public void setConcepts(List<CodeSystemConcept> codeSystemConcepts) {
        this.codeSystemConcepts = codeSystemConcepts;
    }

    public void addConcept(CodeSystemConcept codeSystemConcept) {
        getConcepts().add(codeSystemConcept);
        codeSystemConcept.setCodeSystemVersion(this);
    }

    public CodeSystemVersion getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(CodeSystemVersion previousVersion) {
        this.previousVersion = previousVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeSystemVersion that = (CodeSystemVersion) o;
        return id == that.id && Objects.equals(fullName, that.fullName) && Objects.equals(localName, that.localName) && Objects.equals(previousVersion, that.previousVersion) && Objects.equals(effectiveDate, that.effectiveDate) && Objects.equals(releaseDate, that.releaseDate) && Objects.equals(status, that.status) && Objects.equals(statusDate, that.statusDate) && Objects.equals(description, that.description) && Objects.equals(copyright, that.copyright) && Objects.equals(source, that.source) && Objects.equals(codeSystem, that.codeSystem) && Objects.equals(codeSystemConcepts, that.codeSystemConcepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, localName, previousVersion, effectiveDate, releaseDate, status, statusDate, description, copyright, source, codeSystem, codeSystemConcepts);
    }
}
