package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.client.GenericDocumentCode;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a Data Transformation Service. This provides functions to transform
 * data into an GenericDocumentCodeDts object.
 */
public class GenericDocumentCodeDts {

    public static GenericDocumentCode newInstance(eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode genericDocCode) {
        final GenericDocumentCode result = new GenericDocumentCode();
        result.setSchema(genericDocCode.getSchema());
        result.setValue(genericDocCode.getValue());
        result.setNodeRepresentation(genericDocCode.getValue());
        return result;
    }

    public static List<eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode> newInstance(List<GenericDocumentCode> documentCodes) {
        final List<eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode> result = new ArrayList<>();

        for (GenericDocumentCode documentCode: documentCodes) {
            eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode genericDocumentCode = new eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode();
            genericDocumentCode.setSchema(documentCode.getSchema());
            genericDocumentCode.setValue(documentCode.getNodeRepresentation());
            result.add(genericDocumentCode);
        }
        return result;
    }

    public static eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode newInstance(GenericDocumentCode documentCode) {
        final eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode result = new eu.europa.ec.sante.openncp.core.common.datamodel.GenericDocumentCode();

        result.setSchema(documentCode.getSchema());
        result.setValue(documentCode.getNodeRepresentation());
        return result;
    }

    /**

    /**
     * Private constructor to disable class instantiation.
     */
    private GenericDocumentCodeDts(){}
}
