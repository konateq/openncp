package eu.europa.ec.sante.openncp.tools.tsam.sync.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "value_set")
@SuppressWarnings("unused")
public class ValueSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oid;

    @Column(name = "EPSOS_NAME")
    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "valueSet", orphanRemoval = true)
    private List<ValueSetVersion> versions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
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

    public List<ValueSetVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<ValueSetVersion> versions) {
        this.versions = versions;
    }
}
