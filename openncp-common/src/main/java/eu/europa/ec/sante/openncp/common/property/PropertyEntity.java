package eu.europa.ec.sante.openncp.common.property;

import javax.persistence.*;
import java.beans.Transient;

@Entity
@Table(name = "EHNCP_PROPERTY")
public class PropertyEntity {

    @Id
    @Column(name = "NAME")
    private String key;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "IS_SMP")
    private boolean smp;

    public PropertyEntity() {
    }

    public PropertyEntity(String key, String value) {
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

    @Transient
    public Property asProperty() {
        return ImmutableProperty.builder().key(getKey()).value(getValue()).smp(isSmp()).build();
    }
}
