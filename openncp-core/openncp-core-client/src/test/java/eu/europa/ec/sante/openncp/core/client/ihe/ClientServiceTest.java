package eu.europa.ec.sante.openncp.core.client.ihe;

import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.client.api.*;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.QueryPatientOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.RetrieveDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.service.*;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.NoPatientIdDiscoveredException;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.XCAException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientServiceTest {


    @Mock
    IdentificationService identificationService;

    @Mock
    PatientService patientService;

    @Mock
    OrderService orderService;

    @Mock
    OrCDService orCDService;

    @Mock
    DispensationService dispensationService;


    @InjectMocks
    private ClientServiceImpl clientService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryPatient() throws Exception {
        PatientDemographics patientDemographics = new PatientDemographics();
        patientDemographics.setGivenName("OpenNCP");
        patientDemographics.setCountry("Bel");
        QueryPatientRequest queryPatientRequest = new QueryPatientRequest();
        queryPatientRequest.setCountryCode("1234");
        queryPatientRequest.setPatientDemographics(patientDemographics);

        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        assertions.put(AssertionEnum.CLINICIAN, null);

        List<PatientDemographics> patientDemographicsList = new ArrayList<>();
        QueryPatientOperation queryPatientOperation = new QueryPatientOperation() {
            @Override
            public Map<AssertionEnum, Assertion> getAssertions() {
                return assertions;
            }

            @Override
            public QueryPatientRequest getRequest() {
                return queryPatientRequest;
            }
        };

        when(clientService.queryPatient(queryPatientOperation)).thenReturn(patientDemographicsList);

        List<PatientDemographics> patientDemographicsListExpected = clientService.queryPatient(queryPatientOperation);
        assertEquals(patientDemographicsListExpected, patientDemographicsList);

    }

    @Test
    public void testExceptionOnQueryPatient() throws Exception {
        PatientDemographics patientDemographics = new PatientDemographics();
        QueryPatientRequest queryPatientRequest = new QueryPatientRequest();
        queryPatientRequest.setPatientDemographics(patientDemographics);
        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        QueryPatientOperation queryPatientOperation = new QueryPatientOperation() {
            @Override
            public Map<AssertionEnum, Assertion> getAssertions() {
                return assertions;
            }

            @Override
            public QueryPatientRequest getRequest() {
                return queryPatientRequest;
            }
        };

        when(identificationService.findIdentityByTraits(any(),any(),any())).thenThrow(new NoPatientIdDiscoveredException(OpenNCPErrorCode.ERROR_PI_GENERIC,"Patient Identification generic error"));
        assertThrows(ClientConnectorException.class, () -> clientService.queryPatient(queryPatientOperation));


    }


    @Test
    public void testExceptionOnRetrieveDocument() throws Exception {
        RetrieveDocumentRequest retrieveDocumentRequest = new RetrieveDocumentRequest();
        DocumentId documentId=new DocumentId();
        retrieveDocumentRequest.setDocumentId(documentId);
        GenericDocumentCode genericDocumentCode=new GenericDocumentCode();
        genericDocumentCode.setValue(ClassCode.PS_CLASSCODE.toString());
        genericDocumentCode.setSchema("2.16.840.1.113883.6.1");
        retrieveDocumentRequest.setClassCode(genericDocumentCode);
        final Map<AssertionEnum, Assertion> assertions = new HashMap<>();
        RetrieveDocumentOperation retrieveDocumentOperation = new RetrieveDocumentOperation() {
            @Override
            public Map<AssertionEnum, Assertion> getAssertions() {
                return assertions;
            }

            @Override
            public RetrieveDocumentRequest getRequest() {
                return retrieveDocumentRequest;
            }
        };
        when(patientService.retrieve(any(),any(),any(),any(),any())).thenThrow(new XCAException(OpenNCPErrorCode.ERROR_GENERIC_DOCUMENT_MISSING,"Patient Document is not found",""));
        assertThrows(ClientConnectorException.class, () -> clientService.retrieveDocument(retrieveDocumentOperation));


    }


}
