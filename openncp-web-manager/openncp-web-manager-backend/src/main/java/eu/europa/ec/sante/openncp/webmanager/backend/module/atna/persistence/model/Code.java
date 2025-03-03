package eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "codes")
public class Code {

    @Id
    private Long id;

    // use of codeName instead of code as variable name to avoid issue with Qcode entity generated by querydsl.
    @Column(name = "code")
    private String codeName;

    private String displayName;

    private String codeSystem;

    private String codeSystemName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
