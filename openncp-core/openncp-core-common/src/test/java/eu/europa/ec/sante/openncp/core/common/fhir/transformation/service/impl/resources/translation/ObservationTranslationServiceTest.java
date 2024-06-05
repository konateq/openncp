package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ObservationResultsLaboratoryMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ObservationTranslationServiceTest {

    @Mock
    private TerminologyService mockedTerminologyService;

    private IDomainTranslationService<ObservationResultsLaboratoryMyHealthEu> observationTranscodingLogicService;


    @BeforeEach
    void init() {
        observationTranscodingLogicService = new ObservationTranslationService(mockedTerminologyService);
    }


    @Test
    void translate() {
        assertThat(observationTranscodingLogicService).isNotNull();
    }
}