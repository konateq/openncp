package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.client.api.DocumentId;
import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds.XDSDocument;

public class XdsDocumentDts {

    public static XDSDocument newInstance(final DocumentId xdsDocument) {
        if (xdsDocument == null) {
            return null;
        }


        final XDSDocument result = new XDSDocument();
        result.setDocumentUniqueId(xdsDocument.getDocumentUniqueId());
        result.setRepositoryUniqueId(xdsDocument.getRepositoryUniqueId());


        return result;
    }

    private XdsDocumentDts() {
    }
}
