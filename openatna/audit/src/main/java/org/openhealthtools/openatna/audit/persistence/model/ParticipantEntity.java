package org.openhealthtools.openatna.audit.persistence.model;

import org.hibernate.annotations.GenericGenerator;
import org.openhealthtools.openatna.audit.persistence.model.codes.ParticipantCodeEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The userId, userName, and alternativeUserId are used to determine equality
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */
@Entity
@Table(name = "participants")
public class ParticipantEntity extends PersistentEntity {

    private static final long serialVersionUID = -1L;

    private Long id;
    private Integer version;
    private String userId;
    private String alternativeUserId;
    private String userName;

    private Set<ParticipantCodeEntity> participantTypeCodes = new HashSet<>();

    public ParticipantEntity() {
    }

    public ParticipantEntity(String userId) {
        this.userId = userId;
    }

    public ParticipantEntity(String userId, ParticipantCodeEntity code) {
        this.userId = userId;
        addParticipantTypeCode(code);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAlternativeUserId() {
        return alternativeUserId;
    }

    public void setAlternativeUserId(String alternativeUserId) {
        this.alternativeUserId = alternativeUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "participants_codes")
    public Set<ParticipantCodeEntity> getParticipantTypeCodes() {
        return participantTypeCodes;
    }

    public void setParticipantTypeCodes(Set<ParticipantCodeEntity> participantTypeCodes) {
        this.participantTypeCodes = participantTypeCodes;
    }

    public void addParticipantTypeCode(ParticipantCodeEntity entity) {
        getParticipantTypeCodes().add(entity);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (alternativeUserId != null ? alternativeUserId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "[" + getClass().getName() + " id=" + getId() + ", version=" + getVersion() + ", userID=" + getUserId()
                + ", user name=" + getUserName() + ", alternative user id=" + getAlternativeUserId()
                + ", codes=" + getParticipantTypeCodes() + "]";
    }
}
