package eu.europa.ec.sante.ehdsi.openncp.configmanager.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "property")
public class Property {

    @Id
    @Column(name = "name")
    private String key;

    private String value;

    @Column(name = "is_smp")
    private boolean smp;

    public Property() {
    }

    public Property(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSmp() {
        return smp;
    }

    public void setSmp(boolean smp) {
        this.smp = smp;
    }
}
