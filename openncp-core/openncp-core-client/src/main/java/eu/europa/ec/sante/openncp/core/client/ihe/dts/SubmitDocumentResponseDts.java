package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.client.ObjectFactory;
import eu.europa.ec.sante.openncp.core.client.SubmitDocumentResponse;
import eu.europa.ec.sante.openncp.core.client.ihe.xdr.XdrResponse;

/**
 * This is a Data Transformation Service. This provides functions to transform data into a SubmitDocumentResponseDts object.
 */
public class SubmitDocumentResponseDts {

    /**
     * Private constructor to disable class instantiation.
     */
    private SubmitDocumentResponseDts() {
    }

    static final ObjectFactory objectFactory = new ObjectFactory();

    public static SubmitDocumentResponse newInstance(XdrResponse xdrResponse) {

        final SubmitDocumentResponse result = objectFactory.createSubmitDocumentResponse();
        result.setResponseStatus(xdrResponse.getResponseStatus());
        return result;
    }
}
