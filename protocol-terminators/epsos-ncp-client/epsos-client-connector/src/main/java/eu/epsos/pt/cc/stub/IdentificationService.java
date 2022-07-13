package eu.epsos.pt.cc.stub;

import eu.epsos.exceptions.NoPatientIdDiscoveredException;
import eu.epsos.pt.ws.client.xcpd.XcpdInitGateway;
import eu.europa.ec.sante.ehdsi.constant.assertion.AssertionEnum;
import org.opensaml.saml.saml2.core.Assertion;
import tr.com.srdc.epsos.data.model.PatientDemographics;

import java.util.List;
import java.util.Map;

/**
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public final class IdentificationService {

    private IdentificationService() {
    }

    /**
     * Notify the patient’s country of affiliation on a consent newly given or revoked in the country of care. The
     * consent status modification only applies to the country of care.
     * <p>
     * <br/>
     * <dl>
     * <dt><b>Preconditions</b>
     * <dd>The patient has given a consent that authorises NCP-A to disclose his identity
     * <dd>The patient is able to provide identity traits that are sufficient for a unique identification
     * <dd>Patient's Country must be encoded as defined by ISO 3166-1 alpha-2
     * </dl>
     * <p>
     * <dl>
     * <dt><b>Fault Conditions</b>
     * <dd>Preconditions for a success scenario are not met
     * <dd>Requesting HCP has insufficient rights to query for a patient’s identity
     * <dd>No matching patient is discovered that gave consent to epSOS
     * <dd>ID traits are insufficient for country A to find a matching patient (e.g. provided search criteria are
     * not supported)
     * <dd>The confidence level of the matches is too low with respect to the level required by the requestor
     * <dd>Patient identification is only performed in conjunction with patient authentication (e.g. by
     * providing a secret or a reference to a valid STORK authentication)
     * <dd>Confirming the query would lead to a privacy violation acc. to country A legislation.
     * </dl>
     * <p>
     * Note: PT = Protection Token, ST = Supporting Token (according to [WS SecurityPolicy] definition of security token
     * types)
     *
     * @param patient      List of patient identity traits as provided by the patient to the HCP.
     * @param assertionMap [ST] epSOS HCP Identity Assertion, [PT] X.509 NCP-B service certificate
     * @return The Patients found
     * @throws NoPatientIdDiscoveredException containing the error message
     */
    public static List<PatientDemographics> findIdentityByTraits(final PatientDemographics patient,
                                                                 final Map<AssertionEnum, Assertion> assertionMap,
                                                                 final String countryCode)
            throws NoPatientIdDiscoveredException {

        return XcpdInitGateway.patientDiscovery(patient, assertionMap, countryCode);
    }
}
