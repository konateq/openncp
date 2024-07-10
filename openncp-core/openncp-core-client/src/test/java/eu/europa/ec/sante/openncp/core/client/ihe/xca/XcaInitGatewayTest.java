package eu.europa.ec.sante.openncp.core.client.ihe.xca;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.FilterParams;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.GenericDocumentCode;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.PatientId;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds.OrCDDocumentMetaData;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds.XDSDocument;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.XCAException;
import eu.europa.ec.sante.openncp.core.common.ihe.transformation.service.CDATransformationService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class XcaInitGatewayTest {
    @Mock
    private CDATransformationService cDATransformationService;

    @InjectMocks
    private XcaInitGateway xcaInitGateway;

    /**
     * Method under test:
     * {@link XcaInitGateway#crossGatewayQuery(PatientId, String, List, FilterParams, Map, String)}
     */
    @Test
    public void testCrossGatewayQuery() throws XCAException {
        // Arrange
        PatientId pid = new PatientId("Root", "Extension");

        ArrayList<GenericDocumentCode> documentCodes = new ArrayList<>();

        FilterParams filterParams = new FilterParams();
        filterParams.setCreatedAfter(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setCreatedBefore(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setMaximumSize(3L);

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> xcaInitGateway.crossGatewayQuery(pid, "GB", documentCodes, filterParams, new HashMap<>(), "Service"));
    }

    /**
     * Method under test:
     * {@link XcaInitGateway#crossGatewayQuery(PatientId, String, List, FilterParams, Map, String)}
     */
    @Test
    public void testCrossGatewayQuery2() throws XCAException {
        // Arrange
        PatientId pid = new PatientId("9.9.9-U", "Extension");

        ArrayList<GenericDocumentCode> documentCodes = new ArrayList<>();

        FilterParams filterParams = new FilterParams();
        filterParams.setCreatedAfter(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setCreatedBefore(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setMaximumSize(3L);

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> xcaInitGateway.crossGatewayQuery(pid, "GB", documentCodes, filterParams, new HashMap<>(), "Service"));
    }

    /**
     * Method under test:
     * {@link XcaInitGateway#crossGatewayQuery(PatientId, String, List, FilterParams, Map, String)}
     */
    @Test
    public void testCrossGatewayQuery3() throws XCAException {
        // Arrange
        PatientId pid = mock(PatientId.class);
        when(pid.getExtension()).thenReturn("Extension");
        when(pid.getRoot()).thenReturn("('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')");
        ArrayList<GenericDocumentCode> documentCodes = new ArrayList<>();

        FilterParams filterParams = new FilterParams();
        filterParams.setCreatedAfter(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setCreatedBefore(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setMaximumSize(3L);

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> xcaInitGateway.crossGatewayQuery(pid, "GB", documentCodes, filterParams, new HashMap<>(), "Service"));
        verify(pid).getExtension();
        verify(pid).getRoot();
    }

    /**
     * Method under test:
     * {@link XcaInitGateway#crossGatewayQuery(PatientId, String, List, FilterParams, Map, String)}
     */
    @Test
    public void testCrossGatewayQuery4() throws XCAException {
        // Arrange
        PatientId pid = mock(PatientId.class);
        when(pid.getExtension()).thenReturn("Extension");
        when(pid.getRoot()).thenReturn("Root");

        GenericDocumentCode genericDocumentCode = new GenericDocumentCode();
        genericDocumentCode.setSchema(" ");
        genericDocumentCode.setValue("42");

        ArrayList<GenericDocumentCode> documentCodes = new ArrayList<>();
        documentCodes.add(genericDocumentCode);

        FilterParams filterParams = new FilterParams();
        filterParams.setCreatedAfter(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setCreatedBefore(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setMaximumSize(3L);

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> xcaInitGateway.crossGatewayQuery(pid, "GB", documentCodes, filterParams, new HashMap<>(), "Service"));
        verify(pid).getExtension();
        verify(pid).getRoot();
    }

    /**
     * Method under test:
     * {@link XcaInitGateway#crossGatewayQuery(PatientId, String, List, FilterParams, Map, String)}
     */
    @Test
    public void testCrossGatewayQuery5() throws XCAException {
        // Arrange
        PatientId pid = mock(PatientId.class);
        when(pid.getExtension()).thenReturn("Extension");
        when(pid.getRoot()).thenReturn("Root");

        GenericDocumentCode genericDocumentCode = new GenericDocumentCode();
        genericDocumentCode.setSchema(" ");
        genericDocumentCode.setValue("42");

        GenericDocumentCode genericDocumentCode2 = new GenericDocumentCode();
        genericDocumentCode2.setSchema("LeafClass");
        genericDocumentCode2.setValue(" ");

        ArrayList<GenericDocumentCode> documentCodes = new ArrayList<>();
        documentCodes.add(genericDocumentCode2);
        documentCodes.add(genericDocumentCode);

        FilterParams filterParams = new FilterParams();
        filterParams.setCreatedAfter(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setCreatedBefore(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setMaximumSize(3L);

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> xcaInitGateway.crossGatewayQuery(pid, "GB", documentCodes, filterParams, new HashMap<>(), "Service"));
        verify(pid).getExtension();
        verify(pid).getRoot();
    }

    /**
     * Method under test:
     * {@link XcaInitGateway#crossGatewayQuery(PatientId, String, List, FilterParams, Map, String)}
     */
    @Test
    public void testCrossGatewayQuery6() throws XCAException {
        // Arrange
        PatientId pid = mock(PatientId.class);
        when(pid.getExtension()).thenReturn(" ");
        when(pid.getRoot()).thenReturn("Root");
        ArrayList<GenericDocumentCode> documentCodes = new ArrayList<>();
        FilterParams filterParams = mock(FilterParams.class);
        when(filterParams.getMaximumSize()).thenReturn(3L);
        when(filterParams.getCreatedAfter())
                .thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        when(filterParams.getCreatedBefore())
                .thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        doNothing().when(filterParams).setCreatedAfter(Mockito.<Instant>any());
        doNothing().when(filterParams).setCreatedBefore(Mockito.<Instant>any());
        doNothing().when(filterParams).setMaximumSize(Mockito.<Long>any());
        filterParams.setCreatedAfter(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setCreatedBefore(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        filterParams.setMaximumSize(3L);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> xcaInitGateway.crossGatewayQuery(pid, "GB", documentCodes, filterParams,
                new HashMap<>(), "java.util.List"));
        verify(filterParams, atLeast(1)).getCreatedAfter();
        verify(filterParams, atLeast(1)).getCreatedBefore();
        verify(filterParams, atLeast(1)).getMaximumSize();
        verify(filterParams).setCreatedAfter(isA(Instant.class));
        verify(filterParams).setCreatedBefore(isA(Instant.class));
        verify(filterParams).setMaximumSize(eq(3L));
        verify(pid).getExtension();
        verify(pid).getRoot();
    }

    /**
     * Method under test:
     * {@link XcaInitGateway#crossGatewayRetrieve(XDSDocument, String, String, String, Map, String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCrossGatewayRetrieve() throws XCAException {

        // Arrange
        GenericDocumentCode classCode = new GenericDocumentCode();
        classCode.setSchema("Schema");
        classCode.setValue("42");

        GenericDocumentCode formatCode = new GenericDocumentCode();
        formatCode.setSchema("Schema");
        formatCode.setValue("42");

        XDSDocument document = new XDSDocument();
        document.setAtcCode("Atc Code");
        document.setAtcText("Atc Text");
        document.setAuthors(new ArrayList<>());
        document.setClassCode(classCode);
        document.setCreationTime("Creation Time");
        document.setDescription("The characteristics of someone or something");
        document.setDispensable(true);
        document.setDocumentUniqueId("42");
        document.setDoseFormCode("Dose Form Code");
        document.setDoseFormText("Dose Form Text");
        document.setEventTime("Event Time");
        document.setFormatCode(formatCode);
        document.setHcid("Hcid");
        document.setHealthcareFacility("Healthcare Facility");
        document.setId("42");
        document.setMimeType("Mime Type");
        document.setName("Name");
        document.setPDF(true);
        document
                .setReasonOfHospitalisation(new OrCDDocumentMetaData.ReasonOfHospitalisation("Code", "Coding Scheme", "Text"));
        document.setRepositoryUniqueId("42");
        document.setSize("Size");
        document.setStrength("Strength");
        document.setSubstitution("Substitution");

        // Act
        xcaInitGateway.crossGatewayRetrieve(document, "42", "GB", "en", new HashMap<>(), "Service");
    }
}
