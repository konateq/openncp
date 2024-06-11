package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ObservationResultsLaboratoryMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ObservationTranslationServiceTest extends AbstractTranslationServiceTest {

    @Mock
    private TerminologyService mockedTerminologyService;

    private ResourceTranslationService<ObservationResultsLaboratoryMyHealthEu> observationTranslationService;


    @BeforeEach
    void init() {
        observationTranslationService = new ObservationResourceTranslationService(mockedTerminologyService);
    }


    @Test
    void translate() throws IOException {
        assertThat(observationTranslationService).isNotNull();

        final ObservationResultsLaboratoryMyHealthEu input = parser.parseResource(ObservationResultsLaboratoryMyHealthEu.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("in/observation-in.json"),
                StandardCharsets.UTF_8));

        // Category - StudyType
//        processTranslation(mockedTerminologyService, input.getCategory().get(1).getCoding().iterator().next(), "Bloedbankonderzoeken (set)");

        // Code
        mockTranslation(mockedTerminologyService, input.getCode().getCoding().iterator().next(), "Albumine [massa/tijd] in 24-uurs urine");

        // PerformerFunction

        // Value

        // Interpretation

        // Method

        // ReferenceRange - Type

        // ReferenceRange - AppliesTo

        // Component - Code

        // Component - Value

        // Component - Interpretation

        // Component - ReferenceRange - Type

        // Component - ReferenceRange - AppliesTo

        final ObservationResultsLaboratoryMyHealthEu translated = observationTranslationService.translate(input, targetLanguageCode);

        final ObservationResultsLaboratoryMyHealthEu expectedOutput = parser.parseResource(ObservationResultsLaboratoryMyHealthEu.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("out/observation-out.json"),
                StandardCharsets.UTF_8));
        assertFhirResourcesAreEqual(expectedOutput, translated);
    }
}