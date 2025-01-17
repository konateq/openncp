/**
 * Copyright (c) 2009-2011 University of Cardiff and others
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * <p>
 * Contributors:
 * University of Cardiff - initial API and implementation
 * -
 */

package org.openhealthtools.openatna.audit.persistence.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.dao.CodeDao;
import org.openhealthtools.openatna.audit.persistence.model.codes.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NOTE: the fields that determine a code's uniqueness are its code, code system AND system name.
 * Two codes with the same code and code system but with different system names are NOT considered equal.
 * Is this right????
 * <p/>
 * Codes with only a code defined which have the same code, are not considered equal.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Sep 5, 2009: 1:26:08 PM
 * @date $Date:$ modified by $Author:$
 */

@Transactional(rollbackFor = AtnaPersistenceException.class)
public class HibernateCodeDao extends AbstractHibernateDao<CodeEntity> implements CodeDao {

    private static final String CODE_SYSTEM_NAME = "codeSystemName";
    private static final String CODE_SYSTEM = "codeSystem";
    private static final String TYPE = "type";
    private static final String CODE = "code";

    public HibernateCodeDao(SessionFactory sessionFactory) {
        super(CodeEntity.class, sessionFactory);
    }

    public CodeEntity getById(Long id) {
        return get(id);
    }

    public List<CodeEntity> getByType(CodeEntity.CodeType type) {
        return list(criteria(fromCodeType(type)));
    }

    public List<CodeEntity> getByCode(String code) {
        return list(criteria().add(Restrictions.eq(CODE, code)));
    }

    public List<CodeEntity> getByCodeAndType(CodeEntity.CodeType type, String code) {

        // first see if someone was lazy and didn't use the name of the RFC
        List<CodeEntity> ce = list(criteria()
                .add(Restrictions.eq(TYPE, type))
                .add(Restrictions.eq(CODE, code))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, "RFC-3881")));
        if (ce != null) {
            return ce;
        }
        // look for codes with an empty system name
        ce = list(criteria()
                .add(Restrictions.eq(TYPE, type))
                .add(Restrictions.eq(CODE, code))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, "")));
        if (ce != null) {
            return ce;
        }
        // look for codes with a null system name
        return list(criteria()
                .add(Restrictions.eq(TYPE, type))
                .add(Restrictions.eq(CODE, code))
                .add(Restrictions.isNull(CODE_SYSTEM_NAME)));
    }

    public List<CodeEntity> getByCodeSystem(String codeSystem) {
        return list(criteria().add(Restrictions.eq(CODE_SYSTEM, codeSystem)));
    }

    public List<CodeEntity> getByCodeSystemName(String codeSystemName) {
        return list(criteria().add(Restrictions.eq(CODE_SYSTEM_NAME, codeSystemName)));
    }

    public CodeEntity getByCodeAndSystem(CodeEntity.CodeType type, String code, String codeSystem) {

        return uniqueResult(criteria()
                .add(Restrictions.eq(TYPE, type))
                .add(Restrictions.eq(CODE, code))
                .add(Restrictions.eq(CODE_SYSTEM, codeSystem)));
    }

    public CodeEntity getByCodeAndSystemName(CodeEntity.CodeType type, String code, String codeSystemName) {

        return uniqueResult(criteria()
                .add(Restrictions.eq(TYPE, type))
                .add(Restrictions.eq(CODE, code))
                .add(Restrictions.eq(CODE_SYSTEM_NAME, codeSystemName)));

    }

    public List<CodeEntity> getBySystemAndType(String codeSystem, CodeEntity.CodeType type) {

        return list(criteria(fromCodeType(type)).add(Restrictions.eq(CODE_SYSTEM, codeSystem)));
    }

    public List<CodeEntity> getBySystemNameAndType(String codeSystemName, CodeEntity.CodeType type) {

        return list(criteria(fromCodeType(type)).add(Restrictions.eq(CODE_SYSTEM_NAME, codeSystemName)));
    }

    public CodeEntity getByCodeAndSystemAndSystemName(CodeEntity.CodeType type, String code, String codeSystem, String codeSystemName) {

        return uniqueResult(criteria().add(Restrictions.eq(CODE_SYSTEM_NAME, codeSystemName))
                .add(Restrictions.eq(CODE, code))
                .add(Restrictions.eq(TYPE, type))
                .add(Restrictions.eq(CODE_SYSTEM, codeSystem)));
    }

    public List<CodeEntity> getAll() throws AtnaPersistenceException {
        return all();
    }

    public List<CodeEntity> getAll(int offset, int amount) throws AtnaPersistenceException {
        return all(offset, amount);
    }

    /**
     * this will only save new codes (based on on code, system and system name), or update codes that have
     * been drawn from the DB. Once a code is stored, it cannot have its code, system or system name changed.
     * Otherwise it will be considered a new code.
     *
     * @param ce
     * @throws AtnaPersistenceException
     */
    public boolean save(CodeEntity ce, PersistencePolicies policies) throws AtnaPersistenceException {
        if (!isDuplicate(ce, policies)) {
            currentSession().saveOrUpdate(ce);
            return true;
        }
        return false;
    }

    public void delete(CodeEntity ce) {
        currentSession().delete(ce);
    }

    /**
     * This does an ever decreasingly strict search. It will match against a matching code and a system name as a last resort.
     *
     * @param code
     * @return
     * @throws AtnaPersistenceException
     */
    public CodeEntity get(CodeEntity code) throws AtnaPersistenceException {

        String c = code.getCode();
        String sys = code.getCodeSystem();
        String name = code.getCodeSystemName();
        if (c == null) {
            throw new AtnaPersistenceException("no code in code entity",
                    AtnaPersistenceException.PersistenceError.NON_EXISTENT_CODE);
        }
        if ((sys == null || sys.isEmpty()) && (name == null || name.isEmpty())) {
            List<CodeEntity> l = getByCodeAndType(code.getType(), c);
            if (l.size() == 1) {
                return l.get(0);
            }
        }
        CodeEntity ret = getCodeEntity(code, sys, name, c);
        if (ret != null) {
            return ret;
        }
        if (sys != null && !sys.isEmpty()) {
            ret = getByCodeAndSystem(code.getType(), c, sys);
        }
        if (ret != null) {
            return ret;
        }
        if (name != null && !name.isEmpty()) {
            ret = getByCodeAndSystemName(code.getType(), c, name);
        }
        if (ret != null) {
            return ret;
        }
        return ret;
    }

    private CodeEntity getCodeEntity(CodeEntity code, String sys, String name, String c) {
        CodeEntity ret = null;
        if ((sys != null && !sys.isEmpty() && (name != null && !name.isEmpty()))) {
            ret = getByCodeAndSystemAndSystemName(code.getType(), c, sys, name);
        }
        return ret;
    }

    private Class<?> fromCodeType(CodeEntity.CodeType type) {

        switch (type) {
            case EVENT_ID:
                return EventIdCodeEntity.class;
            case EVENT_TYPE:
                return EventTypeCodeEntity.class;
            case AUDIT_SOURCE:
                return SourceCodeEntity.class;
            case ACTIVE_PARTICIPANT:
                return ParticipantCodeEntity.class;
            case PARTICIPANT_OBJECT_ID_TYPE:
                return ObjectIdTypeCodeEntity.class;
            default:
                return CodeEntity.class;
        }
    }

    private boolean isDuplicate(CodeEntity entity, PersistencePolicies policies) throws AtnaPersistenceException {

        CodeEntity ce = get(entity);
        if (ce != null) {
            if (policies.isErrorOnDuplicateInsert()) {
                throw new AtnaPersistenceException("Attempt to load duplicate Code Entity",
                        AtnaPersistenceException.PersistenceError.DUPLICATE_CODE);
            }
            return true;
        }
        return false;
    }

    /**
     * returns a matching code in the DB if one is there.
     *
     * @param entity
     * @return
     * @throws org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException
     */
    public CodeEntity find(CodeEntity entity) throws AtnaPersistenceException {
        CodeEntity ce = get(entity);
        if (ce != null) {
            return ce;
        }
        return entity;
    }
}
