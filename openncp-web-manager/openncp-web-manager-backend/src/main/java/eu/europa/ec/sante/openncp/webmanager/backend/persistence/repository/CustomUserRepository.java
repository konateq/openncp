package eu.europa.ec.sante.openncp.webmanager.backend.persistence.repository;

import com.querydsl.core.types.Predicate;
import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {

    Page<User> findAllWithRoles(Predicate predicate, Pageable pageable);
}
