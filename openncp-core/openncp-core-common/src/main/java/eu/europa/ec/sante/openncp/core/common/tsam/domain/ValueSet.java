package eu.europa.ec.sante.openncp.core.common.tsam.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A value set represents a uniquely identifiable set of valid concept
 * representations (codes), where any concept representation can be tested to
 * determine whether it is a member of the value set. <br>In epSOS project
 * reference value sets are specified in MVC. These value sets will be used in
 * pivot documents. Member states can potentially define their own value sets
 * used for transcoding to epSOS MVC value sets.
 *
 */
@Entity
@Table(name="value_set")
public class ValueSet {

    /**
     * An internal value set identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * An identifier that uniquely identifies the value set (in the form of an
     * ISO OID)
     */
    @Column(name="oid")
    private String oid;

    /**
     * A name for the value set assigned in MVC
     */
    @Column(name="epsos_name")
    private String epsosName;

    /**
     * A description for the value set
     */
    @Column(name="description", length = 4000)
    private String description;

    /**
     * ValueSetVersion binding<br>
     * TODO check
     *
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "valueSet")
    private List<ValueSetVersion> versions;

    /**
     * CodeSystemVersion binding<br>
     * TODO check
     *
     */
    @OneToOne
    @JoinColumn(name = "code_system_id")
    private CodeSystem codeSystem;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getEpsosName() {
        return epsosName;
    }

    public void setEpsosName(String espsosName) {
        this.epsosName = espsosName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersions(List<ValueSetVersion> versions) {
        this.versions = versions;
    }

    public List<ValueSetVersion> getVersions() {
        if (versions == null) {
            versions = new ArrayList<>();
        }
        return versions;
    }

    public CodeSystem getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(CodeSystem codeSystem) {
        this.codeSystem = codeSystem;
    }

    public void addVersion(ValueSetVersion valueSetVersion)  {
        getVersions().add(valueSetVersion);
        valueSetVersion.setValueSet(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueSet valueSet = (ValueSet) o;
        return id == valueSet.id && Objects.equals(oid, valueSet.oid) && Objects.equals(epsosName, valueSet.epsosName) && Objects.equals(description, valueSet.description) && Objects.equals(versions, valueSet.versions) && Objects.equals(codeSystem, valueSet.codeSystem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, oid, epsosName, description, versions, codeSystem);
    }
}
