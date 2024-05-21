package eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds;

import eu.europa.ec.sante.openncp.common.ClassCode;

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
