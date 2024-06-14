package org.openhealthtools.openatna.audit.persistence.dao;

import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.model.MessageEntity;
import org.openhealthtools.openatna.audit.persistence.model.Query;
import org.openhealthtools.openatna.audit.persistence.model.codes.*;

import java.util.List;

public interface MessageDao extends Dao {

    List<MessageEntity> getByQuery(Query query) throws AtnaPersistenceException;

    MessageEntity getById(Long id) throws AtnaPersistenceException;

    List<MessageEntity> getAll() throws AtnaPersistenceException;

    List<MessageEntity> getAll(int offset, int amount) throws AtnaPersistenceException;

    List<MessageEntity> getByEventId(EventIdCodeEntity idEntity) throws AtnaPersistenceException;

    List<MessageEntity> getBySourceAddress(String address) throws AtnaPersistenceException;

    List<MessageEntity> getByEventType(EventTypeCodeEntity typeEntity) throws AtnaPersistenceException;

    List<MessageEntity> getByEventOutcome(Integer outcome) throws AtnaPersistenceException;

    List<MessageEntity> getByEventAction(String action) throws AtnaPersistenceException;

    List<MessageEntity> getByParticipantUserId(String id) throws AtnaPersistenceException;

    List<MessageEntity> getByParticipantAltUserId(String id) throws AtnaPersistenceException;

    List<MessageEntity> getByParticipantCode(ParticipantCodeEntity codeEntity) throws AtnaPersistenceException;

    List<MessageEntity> getByAuditSourceId(String id) throws AtnaPersistenceException;

    List<MessageEntity> getByAuditSourceEnterpriseId(String id) throws AtnaPersistenceException;

    List<MessageEntity> getByAuditSourceCode(SourceCodeEntity codeEntity) throws AtnaPersistenceException;

    List<MessageEntity> getByObjectId(String id) throws AtnaPersistenceException;

    List<MessageEntity> getByObjectIdTypeCode(ObjectIdTypeCodeEntity codeEntity) throws AtnaPersistenceException;

    List<MessageEntity> getByObjectTypeCode(Short code) throws AtnaPersistenceException;

    List<MessageEntity> getByObjectTypeCodeRole(Short code) throws AtnaPersistenceException;

    List<MessageEntity> getByObjectSensitivity(String sensitivity) throws AtnaPersistenceException;

    void save(MessageEntity messageEntity, PersistencePolicies policies) throws AtnaPersistenceException;

    void delete(MessageEntity messageEntity) throws AtnaPersistenceException;
}
