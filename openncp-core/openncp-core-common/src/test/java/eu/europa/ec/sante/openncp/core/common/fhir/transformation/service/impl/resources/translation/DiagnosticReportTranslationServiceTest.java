package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.DiagnosticReportLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ServiceRequestLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import eu.europa.ec.sante.openncp.core.common.tsam.CodeConcept;
import eu.europa.ec.sante.openncp.core.common.tsam.TSAMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.tsam.service.TerminologyService;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiagnosticReportTranslationServiceTest extends AbstractTranslationServiceTest {

    @Mock
    private TerminologyService mockedTerminologyService;

    private IDomainTranslationService<DiagnosticReportLabMyHealthEu> diagnosticReportTranslationService;


    @BeforeEach
    void init() {
        diagnosticReportTranslationService = new DiagnosticReportTranslationService(mockedTerminologyService);
    }


    @Test
    void translate() throws IOException {
        assertThat(diagnosticReportTranslationService).isNotNull();

        final DiagnosticReportLabMyHealthEu input = parser.parseResource(DiagnosticReportLabMyHealthEu.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("in/diagnosticReport-in.json"),
                StandardCharsets.UTF_8));

        // Language
        //processTranslation(mockedTerminologyService, input.getLanguageElement().castToCoding(input.getLanguageElement()), "Nederlands");

        // Category: studyType
        processTranslation(mockedTerminologyService, input.getCategory().iterator().next().getCoding().iterator().next(), "Laboratoriumverslag");

        // Code
        processTranslation(mockedTerminologyService, input.getCode().getCoding().iterator().next(), "Bloedbankonderzoeken (set)");

        final DiagnosticReportLabMyHealthEu translated = diagnosticReportTranslationService.translate(input, targetLanguageCode);

        final DiagnosticReportLabMyHealthEu expectedOutput = parser.parseResource(DiagnosticReportLabMyHealthEu.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("out/diagnosticReport-out.json"),
                StandardCharsets.UTF_8));
        assertFhirResourcesAreEqual(expectedOutput, translated);
    }
}
