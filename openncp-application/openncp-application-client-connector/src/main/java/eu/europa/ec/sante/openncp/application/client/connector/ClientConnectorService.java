package eu.europa.ec.sante.openncp.application.client.connector;

import eu.europa.ec.sante.openncp.core.client.EpsosDocument;
import eu.europa.ec.sante.openncp.core.client.FilterParams;
import eu.europa.ec.sante.openncp.core.client.GenericDocumentCode;
import eu.europa.ec.sante.openncp.core.client.PatientId;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.List;
import java.util.Map;

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
    List<EpsosDocument> queryDocuments(Map<AssertionEnum, Assertion> assertions, String countryCode, PatientId patientId,
                                       List<GenericDocumentCode> classCodes, FilterParams filterParams) throws ClientConnectorConsumerException;
}
