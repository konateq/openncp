package eu.europa.ec.sante.openncp.core.client.ihe.service;

import eu.europa.ec.sante.openncp.common.configuration.RegisteredService;
import eu.europa.ec.sante.openncp.core.client.api.AssertionEnum;
import eu.europa.ec.sante.openncp.core.client.ihe.xca.XcaInitGateway;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.GenericDocumentCode;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.PatientId;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds.QueryResponse;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds.XDSDocument;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.XCAException;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.List;
import java.util.Map;

/**
 * TODO: Insert description for OrderService class.
 */
public class OrderService {

    private OrderService() {
    }

    public static QueryResponse list(final PatientId pid, final String countryCode, final GenericDocumentCode documentCode,
                                     final Map<AssertionEnum, Assertion> assertionMap) throws XCAException {

        return XcaInitGateway.crossGatewayQuery(pid, countryCode, List.of(documentCode), null, assertionMap,
                RegisteredService.ORDER_SERVICE.getServiceName());
    }

    public static RetrieveDocumentSetResponseType.DocumentResponse retrieve(final XDSDocument document,
                                                                            final String homeCommunityId,
                                                                            final String countryCode,
                                                                            final String targetLanguage,
                                                                            final Map<AssertionEnum, Assertion> assertionMap)
            throws XCAException {

        return XcaInitGateway.crossGatewayRetrieve(document, homeCommunityId, countryCode, targetLanguage, assertionMap, RegisteredService.ORDER_SERVICE.getServiceName());
    }
}
