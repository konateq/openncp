package eu.europa.ec.sante.openncp.core.common.datamodel.xds;

import eu.europa.ec.sante.ehdsi.constant.ClassCode;

/**
 * DocumentAssociation which includes XML and PDF versions of DocumentMetaData.
 * @author mimyllyv
 *
 * @param <T>
 */
public interface DocumentAssociation<T extends EPSOSDocumentMetaData> {
	
	T getXMLDocumentMetaData();

	T getPDFDocumentMetaData();

	ClassCode getDocumentClassCode(String documentId);
	
	String getPatientId(String documentId);
}
