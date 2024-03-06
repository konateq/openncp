package eu.europa.ec.sante.openncp.core.client.ihe.service;

import eu.europa.ec.sante.openncp.common.configuration.RegisteredService;
import eu.europa.ec.sante.openncp.core.client.ihe.xca.XcaInitGateway;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.datamodel.FilterParams;
import eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode;
import eu.europa.ec.sante.openncp.core.common.datamodel.PatientId;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.QueryResponse;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.XDSDocument;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import eu.europa.ec.sante.openncp.core.common.exception.XCAException;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.List;
import java.util.Map;

/**
 *  * TODO: Insert description for OrCDService class.
 *
 * @author Mathias Ghys <mathias.ghys@ext.ec.europa.eu>
 */
public class OrCDService {

    private OrCDService() {
    }

    public static QueryResponse list(final PatientId pid,
                                     final String countryCode,
                                     final List<GenericDocumentCode> documentCodes,
                                     final FilterParams filterParams,
                                     final Map<AssertionEnum, Assertion> assertionMap) throws XCAException {

        return XcaInitGateway.crossGatewayQuery(pid, countryCode, documentCodes, filterParams, assertionMap,
                RegisteredService.ORCD_SERVICE.getServiceName());
    }

    public static RetrieveDocumentSetResponseType.DocumentResponse retrieve(final XDSDocument document,
                                                                            final String homeCommunityId,
                                                                            final String countryCode,
                                                                            final String targetLanguage,
                                                                            final Map<AssertionEnum, Assertion> assertionMap)
            throws XCAException {

        return XcaInitGateway.crossGatewayRetrieve(document, homeCommunityId, countryCode, targetLanguage, assertionMap, RegisteredService.ORCD_SERVICE.getServiceName());
    }
}
