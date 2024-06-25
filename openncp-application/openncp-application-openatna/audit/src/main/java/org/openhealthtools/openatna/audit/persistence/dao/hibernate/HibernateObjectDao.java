package org.openhealthtools.openatna.audit.persistence.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhealthtools.openatna.audit.AtnaFactory;
import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.dao.CodeDao;
import org.openhealthtools.openatna.audit.persistence.dao.ObjectDao;
import org.openhealthtools.openatna.audit.persistence.model.ObjectEntity;
import org.openhealthtools.openatna.audit.persistence.model.codes.CodeEntity;
import org.openhealthtools.openatna.audit.persistence.model.codes.ObjectIdTypeCodeEntity;
import org.openhealthtools.openatna.audit.persistence.util.CodesUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Andrew Harrison
 */
@Transactional(rollbackFor = AtnaPersistenceException.class)
public class HibernateObjectDao extends AbstractHibernateDao<ObjectEntity> implements ObjectDao {

    private static final String OBJECT_ID = "objectId";
    private static final String OBJECT_NAME = "objectName";
    private static final String OBJECT_TYPE_CODE = "objectTypeCode";
    private static final String OBJECT_TYPE_CODE_ROLE = "objectTypeCodeRole";
    private static final String OBJECT_SENSITIVITY = "objectSensitivity";
    private static final String OBJECT_ID_TYPE_CODE = "objectIdTypeCode";
    private static final String CODE_SYSTEM = "codeSystem";
    private static final String CODE_SYSTEM_NAME = "codeSystemName";
    private static final String ERROR_NO_OR_UNKNOWN_OBJECT_ID_TYPE_CODE_DEFINED = "no or unknown object id type code defined.";
    private static final String ERROR_CODE_IS_DEFINED_BUT_IS_OF_A_DIFFERENT_TYPE = "code is defined but is of a different type.";
    private static final String ERROR_CODE_IS_NULL_OR_NOT_EXISTING = "code is null or not existing";

    public HibernateObjectDao(SessionFactory sessionFactory) {
        super(ObjectEntity.class, sessionFactory);
    }

    public ObjectEntity getById(Long id) {
        return get(id);
    }

    public ObjectEntity getByObjectId(String id) {
        return uniqueResult(criteria().add(Restrictions.eq(OBJECT_ID, id)));
    }

    public ObjectEntity get(ObjectEntity other) {

        Criteria c = criteria();
        c.add(Restrictions.eq(OBJECT_ID, other.getObjectId()));
        if (other.getObjectName() != null) {
            c.add(Restrictions.eq(OBJECT_NAME, other.getObjectName()));
        } else {
            c.add(Restrictions.isNull(OBJECT_NAME));
        }
        if (other.getObjectTypeCode() != null) {
            c.add(Restrictions.eq(OBJECT_TYPE_CODE, other.getObjectTypeCode()));
        } else {
            c.add(Restrictions.isNull(OBJECT_TYPE_CODE));
        }
        if (other.getObjectTypeCodeRole() != null) {
            c.add(Restrictions.eq(OBJECT_TYPE_CODE_ROLE, other.getObjectTypeCodeRole()));
        } else {
            c.add(Restrictions.isNull(OBJECT_TYPE_CODE_ROLE));
        }
        if (other.getObjectSensitivity() != null) {
            c.add(Restrictions.eq(OBJECT_SENSITIVITY, other.getObjectSensitivity()));
        } else {
            c.add(Restrictions.isNull(OBJECT_SENSITIVITY));
        }

        List<ObjectEntity> ret = list(c);
        if (ret == null || ret.isEmpty()) {
            return null;
        }
        for (ObjectEntity objectEntity : ret) {
            if (CodesUtils.equivalent(objectEntity.getObjectIdTypeCode(), other.getObjectIdTypeCode())) {
                return objectEntity;
            }
        }
        return null;
    }

    public List<ObjectEntity> getByName(String name) {
        return list(criteria().add(Restrictions.eq(OBJECT_NAME, name)));
    }

    public List<ObjectEntity> getByTypeCode(Short type) {
        return list(criteria().add(Restrictions.eq(OBJECT_TYPE_CODE, type)));
    }

    public List<ObjectEntity> getByTypeCodeRole(Short type) {
        return list(criteria().add(Restrictions.eq(OBJECT_TYPE_CODE_ROLE, type)));
    }

    public List<ObjectEntity> getBySensitivity(String sensitivity) {
        return list(criteria().add(Restrictions.eq(OBJECT_SENSITIVITY, sensitivity)));
    }

    public List<ObjectEntity> getAll() throws AtnaPersistenceException {
        return all();
    }

    public List<ObjectEntity> getAll(int offset, int amount) throws AtnaPersistenceException {
        return all(offset, amount);
    }

    public List<ObjectEntity> getByObjectIdTypeCode(ObjectIdTypeCodeEntity codeEntity)
            throws AtnaPersistenceException {
        return list(criteria().createCriteria(OBJECT_ID_TYPE_CODE).add(Restrictions.eq("code", codeEntity.getCode()))
                .add(Restrictions.eq(CODE_SYSTEM, codeEntity.getCodeSystem()))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeEntity.getCodeSystemName())));
    }

    // TODO - check for INCONSISTENT_REPRESENTATION, e.g sensitivity
    public void save(ObjectEntity entity, PersistencePolicies policies) throws AtnaPersistenceException {

        ObjectIdTypeCodeEntity code = entity.getObjectIdTypeCode();
        if (code != null) {
            CodeDao dao = AtnaFactory.codeDao();
            CodeEntity existing = dao.get(code);
            if (existing == null) {
                if (policies.isAllowNewCodes()) {
                    dao.save(code, policies);
                } else {
                    throw new AtnaPersistenceException(ERROR_NO_OR_UNKNOWN_OBJECT_ID_TYPE_CODE_DEFINED,
                            AtnaPersistenceException.PersistenceError.NON_EXISTENT_CODE);
                }
            } else {
                if (existing instanceof ObjectIdTypeCodeEntity) {
                    entity.setObjectIdTypeCode((ObjectIdTypeCodeEntity) existing);
                } else {
                    throw new AtnaPersistenceException(ERROR_CODE_IS_DEFINED_BUT_IS_OF_A_DIFFERENT_TYPE,
                            AtnaPersistenceException.PersistenceError.WRONG_CODE_TYPE);
                }
            }
        } else {
            throw new AtnaPersistenceException(ERROR_CODE_IS_NULL_OR_NOT_EXISTING,
                    AtnaPersistenceException.PersistenceError.NON_EXISTENT_CODE);
        }

        if (entity.getVersion() == null) {
            // new one.
            ObjectEntity existing = get(entity);
            if (existing != null) {
                if (policies.isErrorOnDuplicateInsert()) {
                    throw new AtnaPersistenceException(entity.toString(),
                            AtnaPersistenceException.PersistenceError.DUPLICATE_OBJECT);
                } else {
                    return;
                }
            }
        }
        currentSession().saveOrUpdate(entity);
    }

    public void delete(ObjectEntity entity) {
        currentSession().delete(entity);
    }
}
