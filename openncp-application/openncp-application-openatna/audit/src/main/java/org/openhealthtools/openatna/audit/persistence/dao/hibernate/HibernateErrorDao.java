package org.openhealthtools.openatna.audit.persistence.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.dao.ErrorDao;
import org.openhealthtools.openatna.audit.persistence.model.ErrorEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0
 */
@Transactional(rollbackFor = AtnaPersistenceException.class)
public class HibernateErrorDao extends AbstractHibernateDao<ErrorEntity> implements ErrorDao {

    private static final String SOURCE_IP = "sourceIp";
    private static final String ERROR_TIMESTAMP = "errorTimestamp";

    public HibernateErrorDao(SessionFactory sessionFactory) {
        super(ErrorEntity.class, sessionFactory);
    }

    public ErrorEntity getById(Long id) {
        return get(id);
    }

    public List<ErrorEntity> getAll() throws AtnaPersistenceException {
        return all();
    }

    public List<ErrorEntity> getAll(int offset, int amount) throws AtnaPersistenceException {
        return all(offset, amount);
    }

    public List<ErrorEntity> getBySourceIp(String ip) {
        return list(criteria().add(Restrictions.eq(SOURCE_IP, ip)));
    }

    public List<ErrorEntity> getAfter(Date date) {
        return list(criteria().add(Restrictions.ge(ERROR_TIMESTAMP, date)));
    }

    public List<ErrorEntity> getAfter(String ip, Date date) {
        return list(criteria().add(Restrictions.eq(SOURCE_IP, ip)).add(Restrictions.ge(ERROR_TIMESTAMP, date)));
    }

    public List<ErrorEntity> getBefore(Date date) {
        return list(criteria().add(Restrictions.le(ERROR_TIMESTAMP, date)));
    }

    public List<ErrorEntity> getBefore(String ip, Date date) {
        return list(criteria().add(Restrictions.eq(SOURCE_IP, ip)).add(Restrictions.le(ERROR_TIMESTAMP, date)));
    }

    public List<ErrorEntity> getBetween(Date first, Date second) {
        return list(criteria().add(Restrictions.ge(ERROR_TIMESTAMP, first)).add(Restrictions.le(ERROR_TIMESTAMP, second)));
    }

    public List<ErrorEntity> getBetween(String ip, Date first, Date second) {
        return list(criteria().add(Restrictions.eq(SOURCE_IP, ip)).add(Restrictions.ge(ERROR_TIMESTAMP, first))
                .add(Restrictions.le(ERROR_TIMESTAMP, second)));
    }

    public void save(ErrorEntity entity) {
        currentSession().saveOrUpdate(entity);
    }
}
