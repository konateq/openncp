package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.client.DocumentId;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.XDSDocument;

public class XdsDocumentDts {

    public static XDSDocument newInstance(DocumentId xdsDocument) {
        if (xdsDocument == null) {
            return null;
        }


        XDSDocument result = new XDSDocument();
        result.setDocumentUniqueId(xdsDocument.getDocumentUniqueId());
        result.setRepositoryUniqueId(xdsDocument.getRepositoryUniqueId());


        return result;
    }

    private XdsDocumentDts() {
    }
}
