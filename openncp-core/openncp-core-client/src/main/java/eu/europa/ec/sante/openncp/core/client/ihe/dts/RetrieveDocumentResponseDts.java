package eu.europa.ec.sante.openncp.core.client.ihe.dts;


import eu.europa.ec.sante.openncp.core.client.ObjectFactory;
import eu.europa.ec.sante.openncp.core.client.RetrieveDocumentResponse;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;

/**
 * This is a Data Transformation Service providing functions to transform data into a RetrieveDocumentResponseDTS object.
 */
public class RetrieveDocumentResponseDts {

    static final ObjectFactory objectFactory = new ObjectFactory();

    private RetrieveDocumentResponseDts() {
    }

    public static RetrieveDocumentResponse newInstance(RetrieveDocumentSetResponseType.DocumentResponse documentResponse) {

        if (documentResponse == null) {
            return null;
        }

        final RetrieveDocumentResponse result = objectFactory.createRetrieveDocumentResponse();
        result.setReturn(DocumentDts.newInstance(documentResponse));
        return result;
    }
}
