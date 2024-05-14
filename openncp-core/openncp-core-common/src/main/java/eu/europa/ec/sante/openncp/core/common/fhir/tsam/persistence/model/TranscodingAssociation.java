package eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * This explicitly defines the mapping (transcoding) between the coded concepts
 * used within the epSOS reference Value Set and the local one for a specific
 * scope. This information is used for pivot document transcoding. This table
 * should be used to represent transcodings from local code systems to epSOS
 * code systems having N:1 relationship only.
 */
@Entity
@Table(name="transcoding_association")
public class TranscodingAssociation {

    /**
     * An identifier than uniquely identifies the association
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * A reference to a target concept, usually from epSOS code system
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "target_concept_id")
    private CodeSystemConcept targedConcept;

    /**
     * A reference to a source concept, usually from local code system
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "source_concept_id")
    private CodeSystemConcept sourceConcept;

    /**
     * A quality attribute that describes if the semantic equivalence is given
     * or if there are granularity differences still to be resolved (narrower,
     * broader, equivalent attributes)
     */
    @Column(name="quality")
    private String quality;

    /**
     * A status to identify the state of association<br>
     * TODO (enumeration or catalog)<br>
     * List of statuses:<br>
     * <li>valid</li> <li>invalid</li>
     */
    @Column(name="status")
    private String status;

    /**
     * A status date to identify the date the status was set to its current
     * value
     */
    @Column(name="status_date")
    private Date statusDate;

    public long getTranscodingAssociationId() {
        return transcodingAssociationId;
    }

    public void setTranscodingAssociationId(long transcodingAssociationId) {
        this.transcodingAssociationId = transcodingAssociationId;
    }

    @Column(name="transcoding_association_id")
    private long transcodingAssociationId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CodeSystemConcept getTargedConcept() {
        return targedConcept;
    }

    public void setTargedConcept(CodeSystemConcept targedConcept) {
        this.targedConcept = targedConcept;
    }

    public CodeSystemConcept getSourceConcept() {
        return sourceConcept;
    }

    public void setSourceConcept(CodeSystemConcept sourceConcept) {
        this.sourceConcept = sourceConcept;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscodingAssociation that = (TranscodingAssociation) o;
        return id == that.id && Objects.equals(targedConcept, that.targedConcept) && Objects.equals(sourceConcept, that.sourceConcept) && Objects.equals(quality, that.quality) && Objects.equals(status, that.status) && Objects.equals(statusDate, that.statusDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, targedConcept, sourceConcept, quality, status, statusDate);
    }
}
