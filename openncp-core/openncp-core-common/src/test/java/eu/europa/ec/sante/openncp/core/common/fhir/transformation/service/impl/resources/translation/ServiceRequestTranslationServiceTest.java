package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl.resources.translation;

import eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources.ServiceRequestLabMyHealthEu;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
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
class ServiceRequestTranslationServiceTest extends AbstractTranslationServiceTest {

    @Mock
    private TerminologyService mockedTerminologyService;

    private IDomainTranslationService<ServiceRequestLabMyHealthEu> serviceRequestTranslationService;


    @BeforeEach
    void init() {
        serviceRequestTranslationService = new ServiceRequestTranslationService(mockedTerminologyService);
    }


    @Test
    void translate() throws IOException {
        assertThat(serviceRequestTranslationService).isNotNull();

        final ServiceRequestLabMyHealthEu input = parser.parseResource(ServiceRequestLabMyHealthEu.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("in/serviceRequest-in.json"),
                StandardCharsets.UTF_8));

        // Code
        processTranslation(mockedTerminologyService, input.getCode().getCoding().iterator().next(), "Hemoglobine- en hematocrietpaneel - Bloed");

        // ReasonCode
        processTranslation(mockedTerminologyService, input.getReasonCode().iterator().next().getCoding().iterator().next(), "Gele koorts");

        final ServiceRequestLabMyHealthEu translated = serviceRequestTranslationService.translate(input, targetLanguageCode);

        final ServiceRequestLabMyHealthEu expectedOutput = parser.parseResource(ServiceRequestLabMyHealthEu.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("out/serviceRequest-out.json"),
                StandardCharsets.UTF_8));
        assertFhirResourcesAreEqual(expectedOutput, translated);
    }
}
