package org.openhealthtools.openatna.audit.persistence.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhealthtools.openatna.audit.AtnaFactory;
import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.dao.*;
import org.openhealthtools.openatna.audit.persistence.model.*;
import org.openhealthtools.openatna.audit.persistence.model.codes.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Andrew Harrison
 * @version $Revision:$
 */
@Transactional(rollbackFor = AtnaPersistenceException.class)
public class HibernateMessageDao extends AbstractHibernateDao<MessageEntity> implements MessageDao {

    private static final String EVENT_ID = "eventId";
    private static final String CODE = "code";
    private static final String CODE_SYSTEM = "codeSystem";
    private static final String CODE_SYSTEM_NAME = "codeSystemName";
    private static final String SOURCE_ADDRESS = "sourceAddress";
    private static final String EVENT_TYPE_CODES = "eventTypeCodes";
    private static final String EVENT_OUTCOME = "eventOutcome";
    private static final String EVENT_ACTION_CODE = "eventActionCode";
    private static final String MESSAGE_PARTICIPANTS = "messageParticipants";
    private static final String PARTICIPANT = "participant";
    private static final String USER_ID = "userId";
    private static final String ALT_USER_ID = "altUserId";
    private static final String PARTICIPANT_TYPE_CODES = "participantTypeCodes";
    private static final String MESSAGE_SOURCES = "messageSources";
    private static final String SOURCE = "source";
    private static final String SOURCE_ID = "sourceId";
    private static final String ENTERPRISE_SITE_ID = "enterpriseSiteId";
    private static final String SOURCE_TYPE_CODES = "sourceTypeCodes";
    private static final String MESSAGE_OBJECTS = "messageObjects";
    private static final String OBJECT = "object";
    private static final String OBJECT_ID = "objectId";
    private static final String OBJECT_ID_TYPE_CODE = "objectIdTypeCode";
    private static final String OBJECT_TYPE_CODE = "objectTypeCode";
    private static final String ERROR_BAD_OBJECT_DETAIL_KEY = "bad object detail key.";
    private static final String ERROR_NO_AUDIT_SOURCE_DEFINED = "no audit source defined.";
    private static final String ERROR_AUDIT_MESSAGES_CANNOT_BE_MODIFIED = "audit messages cannot be modified.";
    private static final String NO_EVENT_ID_CODE_DEFINED = "no event id code defined.";

    public HibernateMessageDao(SessionFactory sessionFactory) {
        super(MessageEntity.class, sessionFactory);
    }

    public List<MessageEntity> getByQuery(Query query) {

        HibernateQueryBuilder builder = new HibernateQueryBuilder(this);
        Criteria c = builder.build(query);
        if (c == null) {
            return new ArrayList<>();
        }
        return list(c);
    }

    public MessageEntity getById(Long id) {
        return get(id);
    }

    public List<MessageEntity> getAll() throws AtnaPersistenceException {
        return all();
    }

    public List<MessageEntity> getAll(int offset, int amount) throws AtnaPersistenceException {
        return all(offset, amount);
    }

    public List<MessageEntity> getByEventId(EventIdCodeEntity codeEntity) {

        return list(criteria().createCriteria(EVENT_ID).add(Restrictions.eq(CODE, codeEntity.getCode()))
                .add(Restrictions.eq(CODE_SYSTEM, codeEntity.getCodeSystem()))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeEntity.getCodeSystemName())));
    }

    public List<MessageEntity> getBySourceAddress(String address) {

        return list(criteria().add(Restrictions.eq(SOURCE_ADDRESS, address)));
    }

    public List<MessageEntity> getByEventType(EventTypeCodeEntity codeEntity) {

        return list(criteria().createCriteria(EVENT_TYPE_CODES).add(Restrictions.eq(CODE, codeEntity.getCode()))
                .add(Restrictions.eq(CODE_SYSTEM, codeEntity.getCodeSystem()))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeEntity.getCodeSystemName())));
    }

    public List<MessageEntity> getByEventOutcome(Integer outcome) {

        return list(criteria().add(Restrictions.eq(EVENT_OUTCOME, outcome)));
    }

    public List<MessageEntity> getByEventAction(String action) {

        return list(criteria().add(Restrictions.eq(EVENT_ACTION_CODE, action)));
    }

    public List<MessageEntity> getByParticipantUserId(String id) {

        return list(criteria().createCriteria(MESSAGE_PARTICIPANTS).createCriteria(PARTICIPANT)
                .add(Restrictions.eq(USER_ID, id)));
    }

    public List<MessageEntity> getByParticipantAltUserId(String id) {

        return list(criteria().createCriteria(MESSAGE_PARTICIPANTS).createCriteria(PARTICIPANT)
                .add(Restrictions.eq(ALT_USER_ID, id)));
    }

    public List<MessageEntity> getByParticipantCode(ParticipantCodeEntity codeEntity) {

        return list(criteria().createCriteria(MESSAGE_PARTICIPANTS).createCriteria(PARTICIPANT)
                .createCriteria(PARTICIPANT_TYPE_CODES)
                .add(Restrictions.eq(CODE, codeEntity.getCode()))
                .add(Restrictions.eq(CODE_SYSTEM, codeEntity.getCodeSystem()))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeEntity.getCodeSystemName())));
    }

    public List<MessageEntity> getByAuditSourceId(String id) {

        return list(criteria().createCriteria(MESSAGE_SOURCES).createCriteria(SOURCE)
                .add(Restrictions.eq(SOURCE_ID, id)));
    }

    public List<MessageEntity> getByAuditSourceEnterpriseId(String id) {

        return list(criteria().createCriteria(MESSAGE_SOURCES).createCriteria(SOURCE)
                .add(Restrictions.eq(ENTERPRISE_SITE_ID, id)));

    }

    public List<MessageEntity> getByAuditSourceCode(SourceCodeEntity codeEntity) {

        return list(criteria().createCriteria(MESSAGE_SOURCES).createCriteria(SOURCE)
                .createCriteria(SOURCE_TYPE_CODES)
                .add(Restrictions.eq(CODE, codeEntity.getCode()))
                .add(Restrictions.eq(CODE_SYSTEM, codeEntity.getCodeSystem()))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeEntity.getCodeSystemName())));
    }

    public List<MessageEntity> getByObjectId(String id) {

        return list(criteria().createCriteria(MESSAGE_OBJECTS).createCriteria(OBJECT)
                .add(Restrictions.eq(OBJECT_ID, id)));
    }

    public List<MessageEntity> getByObjectIdTypeCode(ObjectIdTypeCodeEntity codeEntity) {

        return list(criteria().createCriteria(MESSAGE_OBJECTS).createCriteria(OBJECT)
                .createCriteria(OBJECT_ID_TYPE_CODE)
                .add(Restrictions.eq(CODE, codeEntity.getCode()))
                .add(Restrictions.eq(CODE_SYSTEM, codeEntity.getCodeSystem()))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeEntity.getCodeSystemName())));
    }

    public List<MessageEntity> getByObjectTypeCode(Short code) {

        return list(criteria().createCriteria(MESSAGE_OBJECTS).createCriteria(OBJECT)
                .add(Restrictions.eq(OBJECT_TYPE_CODE, code)));
    }

    public List<MessageEntity> getByObjectTypeCodeRole(Short code) {

        return list(criteria().createCriteria(MESSAGE_OBJECTS).createCriteria(OBJECT).add(Restrictions.eq("objectTypeCodeRole", code)));
    }

    public List<MessageEntity> getByObjectSensitivity(String sensitivity) {

        return list(criteria().createCriteria(MESSAGE_OBJECTS).createCriteria(OBJECT).add(Restrictions.eq("objectSensitivity", sensitivity)));
    }

    /**
     * is this right?
     *
     * @param messageEntity
     * @throws AtnaPersistenceException
     */
    public void save(MessageEntity messageEntity, PersistencePolicies policies) throws AtnaPersistenceException {

        normalize(messageEntity, policies);
        currentSession().saveOrUpdate(messageEntity);
    }

    //TODO: will this remove everything?
    public void delete(MessageEntity messageEntity) {
        currentSession().delete(messageEntity);
    }

    private void normalize(MessageEntity messageEntity, PersistencePolicies policies) throws AtnaPersistenceException {

        if (messageEntity.getEventId() == null) {
            throw new AtnaPersistenceException(ERROR_NO_AUDIT_SOURCE_DEFINED,
                    AtnaPersistenceException.PersistenceError.NO_EVENT_ID);
        }
        if (messageEntity.getId() != null && !policies.isAllowModifyMessages()) {
            throw new AtnaPersistenceException(ERROR_AUDIT_MESSAGES_CANNOT_BE_MODIFIED,
                    AtnaPersistenceException.PersistenceError.UNMODIFIABLE);
        }
        EventIdCodeEntity ce = messageEntity.getEventId();
        CodeDao dao = AtnaFactory.codeDao();
        CodeEntity existing = dao.get(ce);
        if (existing == null) {
            saveNewCodeEntityDao(policies, dao, ce);
        } else {
            setMessageEventId(messageEntity, existing);
        }

        Set<EventTypeCodeEntity> codes = messageEntity.getEventTypeCodes();
        if (!codes.isEmpty()) {
            EventTypeCodeEntity[] arr = codes.toArray(new EventTypeCodeEntity[codes.size()]);
            for (int i = 0; i < arr.length; i++) {
                manageCodeEntity(policies, arr, i, dao);
            }
            messageEntity.setEventTypeCodes(new HashSet<>(Arrays.asList(arr)));
        }
        Set<MessageParticipantEntity> messageParticipants = messageEntity.getMessageParticipants();
        if (messageParticipants.isEmpty()) {
            throw new AtnaPersistenceException("no participants defined",
                    AtnaPersistenceException.PersistenceError.NO_PARTICIPANT);
        }
        for (MessageParticipantEntity entity : messageParticipants) {
            normalize(entity, policies);
        }
        Set<MessageSourceEntity> atnaSources = messageEntity.getMessageSources();
        if (atnaSources.isEmpty()) {
            throw new AtnaPersistenceException("no sources defined", AtnaPersistenceException.PersistenceError.NO_SOURCE);
        }
        for (MessageSourceEntity entity : atnaSources) {
            normalize(entity, policies);
        }
        Set<MessageObjectEntity> messageObjects = messageEntity.getMessageObjects();
        for (MessageObjectEntity entity : messageObjects) {
            normalize(entity, policies);
        }
    }

    private static void manageCodeEntity(PersistencePolicies policies, EventTypeCodeEntity[] arr, int i, CodeDao dao) throws AtnaPersistenceException {
        EventTypeCodeEntity code = arr[i];
        CodeEntity codeEnt = dao.get(code);
        if (codeEnt == null) {
            saveNewTypeCodeEntityDao(policies, dao, code, code.toString());
        } else {
            setEventTypeCodeEntity(codeEnt, arr, i);
        }
    }

    private static void setEventTypeCodeEntity(CodeEntity codeEnt, EventTypeCodeEntity[] arr, int i) throws AtnaPersistenceException {
        if (codeEnt instanceof EventTypeCodeEntity) {
            arr[i] = ((EventTypeCodeEntity) codeEnt);
        } else {
            throw new AtnaPersistenceException("code is defined but is of a different type.",
                    AtnaPersistenceException.PersistenceError.WRONG_CODE_TYPE);
        }
    }

    private static void setMessageEventId(MessageEntity messageEntity, CodeEntity existing) throws AtnaPersistenceException {
        if (existing instanceof EventIdCodeEntity) {
            messageEntity.setEventId((EventIdCodeEntity) existing);
        } else {
            throw new AtnaPersistenceException("code is defined but is of a different type.",
                    AtnaPersistenceException.PersistenceError.WRONG_CODE_TYPE);
        }
    }

    private static void saveNewCodeEntityDao(PersistencePolicies policies, CodeDao dao, EventIdCodeEntity ce) throws AtnaPersistenceException {
        if (policies.isAllowNewCodes()) {
            dao.save(ce, policies);
        } else {
            throw new AtnaPersistenceException(HibernateMessageDao.NO_EVENT_ID_CODE_DEFINED,
                    AtnaPersistenceException.PersistenceError.NON_EXISTENT_CODE);
        }
    }

    private static void saveNewTypeCodeEntityDao(PersistencePolicies policies, CodeDao dao, EventTypeCodeEntity code, String errorMessage) throws AtnaPersistenceException {
        if (policies.isAllowNewCodes()) {
            dao.save(code, policies);
        } else {
            throw new AtnaPersistenceException(errorMessage,
                    AtnaPersistenceException.PersistenceError.NON_EXISTENT_CODE);
        }
    }

    private void normalize(MessageParticipantEntity ap, PersistencePolicies policies) throws AtnaPersistenceException {

        if (ap.getParticipant() == null) {
            throw new AtnaPersistenceException("no active participant defined.",
                    AtnaPersistenceException.PersistenceError.NO_PARTICIPANT);
        }
        if (ap.getId() != null && !policies.isAllowModifyMessages()) {
            throw new AtnaPersistenceException(ERROR_AUDIT_MESSAGES_CANNOT_BE_MODIFIED,
                    AtnaPersistenceException.PersistenceError.UNMODIFIABLE);
        }
        ParticipantEntity pe = ap.getParticipant();
        ParticipantDao dao = AtnaFactory.participantDao();
        ParticipantEntity existing = dao.get(pe);
        if (existing == null) {
            saveNewParticipantDao(policies, dao, pe);
        } else {
            ap.setParticipant(existing);
        }
        NetworkAccessPointEntity net = ap.getNetworkAccessPoint();
        if (net != null) {
            NetworkAccessPointDao netdao = AtnaFactory.networkAccessPointDao();
            NetworkAccessPointEntity there = netdao.getByTypeAndIdentifier(net.getType(), net.getIdentifier());
            saveNewParticipantNetDao(ap, policies, there, netdao, net);
        }
    }

    private static void saveNewParticipantNetDao(MessageParticipantEntity ap, PersistencePolicies policies, NetworkAccessPointEntity there, NetworkAccessPointDao netdao, NetworkAccessPointEntity net) throws AtnaPersistenceException {
        if (there == null) {
            if (policies.isAllowNewNetworkAccessPoints()) {
                netdao.save(net, policies);
            } else {
                throw new AtnaPersistenceException("unknown network access point.",
                        AtnaPersistenceException.PersistenceError.NON_EXISTENT_NETWORK_ACCESS_POINT);
            }
        } else {
            ap.setNetworkAccessPoint(there);
        }
    }

    private static void saveNewParticipantDao(PersistencePolicies policies, ParticipantDao dao, ParticipantEntity pe) throws AtnaPersistenceException {
        if (policies.isAllowNewParticipants()) {
            dao.save(pe, policies);
        } else {
            throw new AtnaPersistenceException("unknown participant.",
                    AtnaPersistenceException.PersistenceError.NON_EXISTENT_PARTICIPANT);
        }
    }

    private boolean isParticipantNonUniquelyEqual(ParticipantEntity update, ParticipantEntity existing) {

        if (update.getUserName() != null && !update.getUserName().equals(existing.getUserName())) {
            return false;
        }
        return update.getParticipantTypeCodes().equals(existing.getParticipantTypeCodes());
    }

    private boolean isSourceNonUniquelyEqual(SourceEntity update, SourceEntity existing) {

        return update.getSourceTypeCodes().equals(existing.getSourceTypeCodes());
    }

    private boolean isObjectNonUniquelyEqual(ObjectEntity update, ObjectEntity existing) {

        if (update.getObjectName() != null && !update.getObjectName().equals(existing.getObjectName())) {
            return false;
        }
        if (!update.getObjectIdTypeCode().equals(existing.getObjectIdTypeCode())) {
            return false;
        }
        if (!update.getObjectSensitivity().equals(existing.getObjectSensitivity())) {
            return false;
        }
        if (!update.getObjectTypeCode().equals(existing.getObjectTypeCode())) {
            return false;
        }
        if (!update.getObjectTypeCodeRole().equals(existing.getObjectTypeCodeRole())) {
            return false;
        }
        if (!update.getObjectDetailTypes().equals(existing.getObjectDetailTypes())) {
            return false;
        }
        // TODO: doesn't include SopClasses
        return update.getObjectDescriptions().equals(existing.getObjectDescriptions());
    }

    private void updateParticipant(ParticipantDao dao, PersistencePolicies policies, MessageParticipantEntity ap,
                                   ParticipantEntity update, ParticipantEntity existing) throws AtnaPersistenceException {

        if (!isParticipantNonUniquelyEqual(update, existing)) {
            update.setVersion(existing.getVersion() + 1);
            dao.save(existing, policies);
            dao.save(update, policies);
            ap.setParticipant(update);
        } else {
            ap.setParticipant(existing);
        }
    }

    private void updateSource(SourceDao dao, PersistencePolicies policies, MessageSourceEntity ap, SourceEntity update,
                              SourceEntity existing) throws AtnaPersistenceException {

        if (!isSourceNonUniquelyEqual(update, existing)) {
            update.setVersion(existing.getVersion() + 1);
            dao.save(existing, policies);
            dao.save(update, policies);
            ap.setSource(update);
        } else {
            ap.setSource(existing);
        }
    }

    private void updateObject(ObjectDao dao, PersistencePolicies policies, MessageObjectEntity ap, ObjectEntity update,
                              ObjectEntity existing) throws AtnaPersistenceException {

        if (!isObjectNonUniquelyEqual(update, existing)) {
            update.setVersion(existing.getVersion() + 1);
            dao.save(existing, policies);
            dao.save(update, policies);
            ap.setObject(update);
            Set<ObjectDetailEntity> details = ap.getDetails();
            for (ObjectDetailEntity detail : details) {
                if (!update.containsDetailType(detail.getType())
                        && !policies.isAllowUnknownDetailTypes()) {
                    throw new AtnaPersistenceException(ERROR_BAD_OBJECT_DETAIL_KEY,
                            AtnaPersistenceException.PersistenceError.UNKNOWN_DETAIL_TYPE);
                }
            }
        } else {
            ap.setObject(existing);
        }
    }

    private void normalize(MessageSourceEntity as, PersistencePolicies policies) throws AtnaPersistenceException {

        if (as.getSource() == null) {
            throw new AtnaPersistenceException(ERROR_NO_AUDIT_SOURCE_DEFINED,
                    AtnaPersistenceException.PersistenceError.NO_SOURCE);
        }
        if (as.getId() != null && !policies.isAllowModifyMessages()) {
            throw new AtnaPersistenceException(ERROR_AUDIT_MESSAGES_CANNOT_BE_MODIFIED,
                    AtnaPersistenceException.PersistenceError.UNMODIFIABLE);
        }
        SourceEntity se = as.getSource();
        SourceDao dao = AtnaFactory.sourceDao();
        SourceEntity existing = dao.get(se);
        if (existing == null) {
            if (policies.isAllowNewSources()) {
                dao.save(se, policies);
            } else {
                throw new AtnaPersistenceException(ERROR_NO_AUDIT_SOURCE_DEFINED,
                        AtnaPersistenceException.PersistenceError.NON_EXISTENT_SOURCE);
            }
        } else {
            as.setSource(existing);
        }
    }

    private void normalize(MessageObjectEntity ao, PersistencePolicies policies) throws AtnaPersistenceException {
        if (ao.getObject() == null) {
            throw new AtnaPersistenceException("no participant object defined.",
                    AtnaPersistenceException.PersistenceError.NO_OBJECT);
        }
        if (ao.getId() != null && !policies.isAllowModifyMessages()) {
            throw new AtnaPersistenceException(ERROR_AUDIT_MESSAGES_CANNOT_BE_MODIFIED,
                    AtnaPersistenceException.PersistenceError.UNMODIFIABLE);
        }
        ObjectEntity oe = ao.getObject();
        ObjectDao dao = AtnaFactory.objectDao();
        ObjectEntity existing = dao.getByObjectId(oe.getObjectId());
        if (existing == null) {
            if (policies.isAllowNewObjects()) {
                dao.save(oe, policies);
            } else {
                throw new AtnaPersistenceException("no object defined.",
                        AtnaPersistenceException.PersistenceError.NON_EXISTENT_OBJECT);
            }
        } else {
            ao.setObject(existing);
            Set<ObjectDetailEntity> details = ao.getDetails();
            for (ObjectDetailEntity detail : details) {
                if (!existing.containsDetailType(detail.getType())
                        && !policies.isAllowUnknownDetailTypes()) {
                    throw new AtnaPersistenceException(ERROR_BAD_OBJECT_DETAIL_KEY,
                            AtnaPersistenceException.PersistenceError.UNKNOWN_DETAIL_TYPE);
                }
            }
        }
    }
}
