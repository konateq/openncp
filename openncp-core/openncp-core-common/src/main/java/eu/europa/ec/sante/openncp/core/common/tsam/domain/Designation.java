package eu.europa.ec.sante.openncp.core.common.tsam.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Each Designation is a representation of the Concept and is assigned a unique
 * designation identifier. <br/>In context of epSOS project designations are the
 * subject of translation to member state languages. Designations in different
 * languages are used to produce translation of pivot documents.
 */

@Entity
@Table(name="designation")
public class Designation {

    /**
     * A unique identifier for the designation
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * A display name for the designation
     */
    @Column(name="designation", length = 4000)
    private String designation;

    /**
     * A code of language in which the designation is expressed
     */


    @Column(name="language_code")
    private String languageCode;

    /**
     * A designation type that describes the nature or usage of the designation.<br>
     * TODO (enumeration or catalog)<br>
     * List of type values:<br>
     * <li>unspecified</li> <li>preferred term</li> <li>synonym</li> <li>fully
     * specified name</li> <li>code</li>
     */
    @Column(name="type")
    private String type;

    /**
     * An optional attribute that identifies whether an attribute has a type of
     * usage preference
     */
    @Column(name="is_preferred")
    private Boolean preferred;

    /**
     * A status to identify the state of designation<br>
     * List of statuses:<br>
     * <li>current designation of concept currently in use</li> <li>retired
     * past version of the designation not valid anymore</li>
     */
    @Column(name="status")
    private String status;

    /**
     * A status date to identify the date the status was set to its current
     * value
     */
    @Column(name="status_date")
    private Date statusDate;

    /**
     * A code system concept to which designation is related
     */
    @ManyToOne
    @JoinColumn(name = "code_system_concept_id")
    private CodeSystemConcept codeSystemConcept;

    public CodeSystemConcept getConcept() {
        return codeSystemConcept;
    }

    public void setConcept(CodeSystemConcept concept) {
        this.codeSystemConcept = concept;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(designation.length()+6);
        if (languageCode != null) {
            builder.append(languageCode);
            builder.append(": ");
        }
        if (designation != null) {
            builder.append(designation);
            builder.append(" ");
        }
        builder.append(status);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Designation that = (Designation) o;
        return id == that.id && Objects.equals(designation, that.designation) && Objects.equals(languageCode, that.languageCode) && Objects.equals(type, that.type) && Objects.equals(preferred, that.preferred) && Objects.equals(status, that.status) && Objects.equals(statusDate, that.statusDate) && Objects.equals(codeSystemConcept, that.codeSystemConcept);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, designation, languageCode, type, preferred, status, statusDate, codeSystemConcept);
    }
}

