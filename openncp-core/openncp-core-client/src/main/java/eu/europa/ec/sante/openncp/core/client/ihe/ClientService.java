package eu.europa.ec.sante.openncp.core.client.ihe;

import java.util.List;

import eu.europa.ec.sante.openncp.core.client.EpsosDocument;
import eu.europa.ec.sante.openncp.core.client.PatientDemographics;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.QueryDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.QueryPatientOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.RetrieveDocumentOperation;
import eu.europa.ec.sante.openncp.core.client.ihe.dto.SubmitDocumentOperation;


public interface ClientService {
    String submitDocument(SubmitDocumentOperation submitDocumentOperation);

    List<PatientDemographics> queryPatient(QueryPatientOperation queryPatientOperation);

    List<EpsosDocument> queryDocuments(QueryDocumentOperation queryDocumentsOperation);

    EpsosDocument retrieveDocument(RetrieveDocumentOperation retrieveDocumentOperation);

    String sayHello(String who);
}
