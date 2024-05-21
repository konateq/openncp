package eu.europa.ec.sante.openncp.core.client.ihe.service;

import eu.europa.ec.sante.openncp.common.configuration.RegisteredService;
import eu.europa.ec.sante.openncp.core.client.ihe.xca.XcaInitGateway;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.constants.AssertionEnum;
import eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode;
import eu.europa.ec.sante.openncp.core.common.datamodel.PatientId;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.QueryResponse;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.XDSDocument;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import eu.europa.ec.sante.openncp.core.common.exception.XCAException;
import org.opensaml.saml.saml2.core.Assertion;


import java.util.List;
import java.util.Map;

public class PatientService {

    private PatientService() {
    }

    public static QueryResponse list(final PatientId pid, final String countryCode, final GenericDocumentCode documentCode,
                                     final Map<AssertionEnum, Assertion> assertionMap) throws XCAException {

        return XcaInitGateway.crossGatewayQuery(pid, countryCode, List.of(documentCode), null, assertionMap,
                RegisteredService.PATIENT_SERVICE.getServiceName());
    }

    public static RetrieveDocumentSetResponseType.DocumentResponse retrieve(final XDSDocument document,
                                                                            final String homeCommunityId,
                                                                            final String countryCode,
                                                                            final String targetLanguage,
                                                                            final Map<AssertionEnum, Assertion> assertionMap) throws XCAException {

        return XcaInitGateway.crossGatewayRetrieve(document, homeCommunityId, countryCode, targetLanguage, assertionMap, RegisteredService.PATIENT_SERVICE.getServiceName());
    }
}