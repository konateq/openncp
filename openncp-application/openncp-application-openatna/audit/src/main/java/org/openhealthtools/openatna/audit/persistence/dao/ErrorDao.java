package org.openhealthtools.openatna.audit.persistence.dao;

import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.model.ErrorEntity;

import java.util.Date;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0
 */
public interface ErrorDao extends Dao {

    ErrorEntity getById(Long id) throws AtnaPersistenceException;

    List<ErrorEntity> getAll() throws AtnaPersistenceException;

    List<ErrorEntity> getAll(int offset, int amount) throws AtnaPersistenceException;

    List<ErrorEntity> getBySourceIp(String ip) throws AtnaPersistenceException;

    List<ErrorEntity> getAfter(Date date) throws AtnaPersistenceException;

    List<ErrorEntity> getAfter(String ip, Date date) throws AtnaPersistenceException;

    List<ErrorEntity> getBefore(Date date) throws AtnaPersistenceException;

    List<ErrorEntity> getBefore(String ip, Date date) throws AtnaPersistenceException;

    List<ErrorEntity> getBetween(Date first, Date second) throws AtnaPersistenceException;

    List<ErrorEntity> getBetween(String ip, Date first, Date second) throws AtnaPersistenceException;

    void save(ErrorEntity entity) throws AtnaPersistenceException;
}
