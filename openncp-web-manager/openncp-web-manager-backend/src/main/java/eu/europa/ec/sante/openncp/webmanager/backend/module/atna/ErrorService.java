package eu.europa.ec.sante.openncp.webmanager.backend.module.atna;

import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.model.Error;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.repository.ErrorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ErrorService {

    private final ErrorRepository errorRepository;

    public ErrorService(ErrorRepository errorRepository) {
        this.errorRepository = errorRepository;
    }

    public Page<Error> findErrors(Pageable pageable) {
        return errorRepository.findAll(pageable);
    }
}
