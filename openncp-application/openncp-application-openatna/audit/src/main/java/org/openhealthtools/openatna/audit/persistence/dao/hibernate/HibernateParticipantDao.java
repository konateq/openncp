package org.openhealthtools.openatna.audit.persistence.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhealthtools.openatna.audit.AtnaFactory;
import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.dao.CodeDao;
import org.openhealthtools.openatna.audit.persistence.dao.ParticipantDao;
import org.openhealthtools.openatna.audit.persistence.model.ParticipantEntity;
import org.openhealthtools.openatna.audit.persistence.model.codes.CodeEntity;
import org.openhealthtools.openatna.audit.persistence.model.codes.ParticipantCodeEntity;
import org.openhealthtools.openatna.audit.persistence.util.CodesUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Harrison
 */
@Transactional(rollbackFor = AtnaPersistenceException.class)
public class HibernateParticipantDao extends AbstractHibernateDao<ParticipantEntity> implements ParticipantDao {

    private static final String USER_ID = "userId";
    private static final String ALTERNATIVE_USER_ID = "alternativeUserId";
    private static final String USER_NAME = "userName";
    private static final String PARTICIPANT_TYPE_CODES = "participantTypeCodes";
    private static final String CODE = "code";
    private static final String CODE_SYSTEM = "codeSystem";
    private static final String CODE_SYSTEM_NAME = "codeSystemName";
    private static final String ERROR_CODE_IS_DEFINED_BUT_IS_OF_A_DIFFERENT_TYPE = "code is defined but is of a different type.";

    public HibernateParticipantDao(SessionFactory sessionFactory) {
        super(ParticipantEntity.class, sessionFactory);
    }

    public ParticipantEntity getById(Long id) {
        return get(id);
    }

    public List<ParticipantEntity> getByUserId(String userId) {
        return list(criteria().add(Restrictions.eq(USER_ID, userId)));
    }

    public ParticipantEntity getByAltUserId(String altUserId) {
        return uniqueResult(criteria().add(Restrictions.eq(ALTERNATIVE_USER_ID, altUserId)));
    }

    public ParticipantEntity get(ParticipantEntity other) {

        Criteria c = criteria();
        c.add(Restrictions.eq(USER_ID, other.getUserId()));
        if (other.getAlternativeUserId() != null) {
            c.add(Restrictions.eq(ALTERNATIVE_USER_ID, other.getAlternativeUserId()));
        } else {
            c.add(Restrictions.isNull(ALTERNATIVE_USER_ID));
        }
        if (other.getUserName() != null) {
            c.add(Restrictions.eq(USER_NAME, other.getUserName()));
        } else {
            c.add(Restrictions.isNull(USER_NAME));
        }
        List<ParticipantEntity> ret = list(c);
        if (ret == null || ret.isEmpty()) {
            return null;
        }
        for (ParticipantEntity participantEntity : ret) {
            if (CodesUtils.equivalent(participantEntity.getParticipantTypeCodes(), other.getParticipantTypeCodes())) {
                return participantEntity;
            }
        }
        return null;
    }

    public List<ParticipantEntity> getByCode(ParticipantCodeEntity codeEntity) {

        return list(criteria().createCriteria(PARTICIPANT_TYPE_CODES)
                .add(Restrictions.eq(CODE, codeEntity.getCode()))
                .add(Restrictions.eq(CODE_SYSTEM, codeEntity.getCodeSystem()))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeEntity.getCodeSystemName())));
    }

    public List<ParticipantEntity> getByUserName(String userName) {
        return list(criteria().add(Restrictions.eq(USER_NAME, userName)));
    }

    public List<ParticipantEntity> getAll() throws AtnaPersistenceException {
        return all();
    }

    public List<ParticipantEntity> getAll(int offset, int amount) throws AtnaPersistenceException {
        return all(offset, amount);
    }

    /**
     * This checks for any codes that are NOT in the DB.
     * Codes that are considered to be in the DB should not be added again,
     * while those that are not, should not be in the participant.
     * <p/>
     * For each code in the participant:
     * remove it.
     * find an existing code that maches it.
     * if one is found, add this to the list.
     * <p/>
     * This means codes that have been modified (e.g. display name was changed)
     * will not be persisted in this call. To modify, one would have to call
     * the save on the code itself.
     * <p/>
     * If the participant's version is null, then a matching participant based on the (alt)user id
     * is queried for. If one is found, this throws a DUPLICATE_PARTICIPANT
     * AtnaParticipantException. Otherwise, the save is allowed to proceed.
     *
     * @param pe
     */
    public void save(ParticipantEntity pe, PersistencePolicies policies) throws AtnaPersistenceException {

        Set<ParticipantCodeEntity> codes = pe.getParticipantTypeCodes();
        if (!codes.isEmpty()) {
            ParticipantCodeEntity[] arr = codes.toArray(new ParticipantCodeEntity[codes.size()]);
            CodeDao dao = AtnaFactory.codeDao();
            for (int i = 0; i < arr.length; i++) {
                ParticipantCodeEntity code = arr[i];
                CodeEntity codeEnt = dao.get(code);
                if (codeEnt == null) {
                    saveNewCodeEntityDao(policies, dao, code);
                } else {
                    updateCodeEntityDao(codeEnt, arr, i);
                }
            }
            pe.setParticipantTypeCodes(new HashSet<>(Arrays.asList(arr)));
        }

        if (pe.getVersion() == null) {
            // new one.
            ParticipantEntity existing = get(pe);
            if (existing != null) {
                if (policies.isErrorOnDuplicateInsert()) {
                    throw new AtnaPersistenceException(pe.toString(),
                            AtnaPersistenceException.PersistenceError.DUPLICATE_PARTICIPANT);
                } else {
                    return;
                }
            }
        }
        currentSession().saveOrUpdate(pe);
    }

    private static void updateCodeEntityDao(CodeEntity codeEnt, ParticipantCodeEntity[] arr, int i) throws AtnaPersistenceException {
        if (codeEnt instanceof ParticipantCodeEntity) {
            arr[i] = ((ParticipantCodeEntity) codeEnt);
        } else {
            throw new AtnaPersistenceException(ERROR_CODE_IS_DEFINED_BUT_IS_OF_A_DIFFERENT_TYPE,
                    AtnaPersistenceException.PersistenceError.WRONG_CODE_TYPE);
        }
    }

    private static void saveNewCodeEntityDao(PersistencePolicies policies, CodeDao dao, ParticipantCodeEntity code) throws AtnaPersistenceException {
        if (policies.isAllowNewCodes()) {
            dao.save(code, policies);
        } else {
            throw new AtnaPersistenceException(code.toString(),
                    AtnaPersistenceException.PersistenceError.NON_EXISTENT_CODE);
        }
    }

    public void delete(ParticipantEntity ap) {
        currentSession().delete(ap);
    }
}
