package eu.europa.ec.sante.openncp.core.common.tsam.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="code_system")
public class CodeSystem {

    /**
     * An internal code system identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * An identifier that uniquely identified the Code System (in the form of an
     * ISO OID)
     */
    @Column(name="oid")
    private String oid;

    /**
     * An identifier that uniquely identified the Code System (in the form of an
     * URL)
     */
    @Column(name="url")
    private String url;

    /**
     * A name given in MVC / MTC that is usually the same as official code
     * system name
     */
    @Column(name="name")
    private String name;

    /**
     * A description that describes the Code System. This may include the code
     * system uses and intent
     */
    @Column(name="description", length = 4000)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codeSystem")
    public List<CodeSystemVersion> codeSystemVersions;


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CodeSystemVersion> getCodeSystemVersions() {
        if (codeSystemVersions == null) {
            codeSystemVersions = new ArrayList<>();
        }
        return codeSystemVersions;
    }

    public void setCodeSystemVersions(List<CodeSystemVersion> codeSystemVersions) {
        this.codeSystemVersions = codeSystemVersions;
    }

    public void addVersion(CodeSystemVersion codeSystemVersion)  {
        getCodeSystemVersions().add(codeSystemVersion);
        codeSystemVersion.setCodeSystem(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeSystem that = (CodeSystem) o;
        return id == that.id && Objects.equals(oid, that.oid) && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, oid, name, description);
    }
}
