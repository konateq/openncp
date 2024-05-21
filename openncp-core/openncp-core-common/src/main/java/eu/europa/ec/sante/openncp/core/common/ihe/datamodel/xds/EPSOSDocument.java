package eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds;

import eu.europa.ec.sante.openncp.common.ClassCode;

/**
 * EPSOSDocument interface. Includes EPSOSDocumentMetaData and includes DOM Document.
 */
public interface EPSOSDocument {

    String getPatientId();

    ClassCode getClassCode();

    org.w3c.dom.Document getDocument();

    boolean matchesCriteria(SearchCriteria searchCriteria);
}
