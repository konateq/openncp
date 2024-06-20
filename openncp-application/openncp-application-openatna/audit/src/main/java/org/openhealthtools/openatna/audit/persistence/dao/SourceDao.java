package org.openhealthtools.openatna.audit.persistence.dao;

import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.model.SourceEntity;
import org.openhealthtools.openatna.audit.persistence.model.codes.SourceCodeEntity;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version $Revision:$
 */
public interface SourceDao {

    SourceEntity getById(Long id) throws AtnaPersistenceException;

    List<SourceEntity> getBySourceId(String id) throws AtnaPersistenceException;

    SourceEntity getByEnterpriseSiteId(String id) throws AtnaPersistenceException;

    SourceEntity get(SourceEntity other) throws AtnaPersistenceException;

    List<SourceEntity> getByCode(SourceCodeEntity code) throws AtnaPersistenceException;

    List<SourceEntity> getAll() throws AtnaPersistenceException;

    List<SourceEntity> getAll(int offset, int amount) throws AtnaPersistenceException;

    void save(SourceEntity entity, PersistencePolicies policies) throws AtnaPersistenceException;

    void delete(SourceEntity entity) throws AtnaPersistenceException;
}
