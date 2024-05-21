package eu.europa.ec.sante.openncp.application.client.connector;

import java.util.List;
import java.util.Map;

import eu.europa.ec.sante.openncp.core.client.*;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.opensaml.saml.saml2.core.Assertion;

public interface ClientConnectorService {

    /**
     * Returns a list of clinical documents related to the patient demographics provided.
     *
     * @param assertions   - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param countryCode  - ISO Country code of the patient country of origin.
     * @param patientId    - Unique Patient Identifier retrieved from NCP-A.
     * @param classCodes   - Class Codes of the documents to retrieve.
     * @param filterParams - Extra parameters for search filtering.
     * @return List of clinical documents and metadata searched by the clinician.
     */
    List<EpsosDocument> queryDocuments(final Map<AssertionEnum, Assertion> assertions, final String countryCode, final PatientId patientId,
                                       final List<GenericDocumentCode> classCodes, final FilterParams filterParams) throws ClientConnectorException;

    /**
     * Returns demographics of the patient corresponding to the identity traits provided.
     *
     * @param assertions          - Map of assertions required by the transaction (HCP, TRC and NoK optional)
     * @param countryCode         - ISO Country code of the patient country of origin.
     * @param patientDemographics - Identifiers of the requested patient
     * @return List of patients found (only 1 patient is expected in MyHealth@EU)
     */
    List<PatientDemographics> queryPatient(final Map<AssertionEnum, Assertion> assertions, final String countryCode, final PatientDemographics patientDemographics)
            throws ClientConnectorException;

    /**
     * Retrieves the clinical document of an identified patient (prescription, patient summary or original clinical document).
     *
     * @param assertions      - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param countryCode     - ISO Country code of the patient country of origin.
     * @param documentId      - Unique identifier of the CDA document.
     * @param homeCommunityId - HL7 Home Community ID of the country of origin.
     * @param classCode       - HL7 ClassCode of the document type to be retrieved.
     * @param targetLanguage  - Expected target language of the CDA translation.
     * @return Clinical Document and metadata returned by the Country of Origin.
     */
    EpsosDocument retrieveDocument(final Map<AssertionEnum, Assertion> assertions, final String countryCode, final DocumentId documentId,
                                          final String homeCommunityId, final GenericDocumentCode classCode, final String targetLanguage)
            throws ClientConnectorException;

    /**
     * Submits Clinical Document to the patient country of origin (dispense and discard).
     *
     * @param assertions          - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param countryCode         - ISO Country code of the patient country of origin.
     * @param document            - Clinical document and metadata to be submitted to the patient country of origin.
     * @param patientDemographics - Demographics of the patient linked to the document submission.
     * @return Acknowledge and status of the document submission.
     */
    SubmitDocumentResponse submitDocument(final Map<AssertionEnum, Assertion> assertions, final String countryCode, final EpsosDocument document,
                          final PatientDemographics patientDemographics) throws ClientConnectorException;

    /**
     * Default Webservice test method available mainly for configuration and testing purpose.
     *
     * @param assertions - Map of assertions required by the transaction (HCP, TRC and NoK optional).
     * @param name       - Token sent for testing.
     * @return Hello message concatenated with the token passed as parameter.
     */
    String sayHello(final Map<AssertionEnum, Assertion> assertions, final String name);
}