package eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.QUser;
import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(value = "webmanagerTransactionManager")
public class CustomUserRepositoryImpl extends QuerydslRepositorySupport implements CustomUserRepository {

    public CustomUserRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Page<User> findAllWithRoles(final Predicate predicate, final Pageable pageable) {
        final QUser qUser = QUser.user;
        final JPQLQuery<Long> countQuery = from(qUser)
                .select(qUser.id)
                .where(predicate);
        final List<Long> ids = getQuerydsl().applyPagination(pageable,
                from(qUser)
                        .select(qUser.id)
                        .where(predicate))
                .fetch();
        final JPQLQuery<User> fetchQuery = getQuerydsl().applySorting(pageable.getSort(),
                from(qUser)
                        .leftJoin(qUser.roles).fetchJoin()
                        .where(qUser.id.in(ids)));
        return PageableExecutionUtils.getPage(fetchQuery.fetch(), pageable, countQuery::fetchCount);
    }
}
