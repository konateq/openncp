package eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.impl;

import eu.europa.ec.sante.openncp.core.common.fhir.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.ITranslationService;
import eu.europa.ec.sante.openncp.core.common.fhir.transformation.service.resources.IDomainTranslationService;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class TranslationService implements ITranslationService {

    @Autowired
    private IDomainTranslationService<Patient> patientTranslationService;

    @Autowired
    private IDomainTranslationService<DiagnosticReport> diagnosticReportTranslationService;

    @Autowired
    private IDomainTranslationService<Observation> observationTranslationService;

    @Autowired
    private IDomainTranslationService<ServiceRequest> serviceRequestTranslationService;

    @Autowired
    private IDomainTranslationService<Composition> compositionTranslationService;

    @Override
    public TMResponseStructure translate(Bundle fhirDocument, String targetLanguage) {

        Composition composition;
        DiagnosticReport diagnosticReport;
        Patient patient;
        Observation observation;
        ServiceRequest serviceRequest;


        for (Bundle.BundleEntryComponent bundleEntryComponent: fhirDocument.getEntry()) {
            switch (bundleEntryComponent.getResource().getResourceType()) {
                case Composition:
                    composition = (Composition) bundleEntryComponent.getResource();
                    compositionTranslationService.translate(composition, targetLanguage);
                    break;
                case DiagnosticReport:
                    diagnosticReport = (DiagnosticReport) bundleEntryComponent.getResource();
                    diagnosticReportTranslationService.translate(diagnosticReport, targetLanguage);
                    break;
                case Patient:
                    patient = (Patient) bundleEntryComponent.getResource();
                    patientTranslationService.translate(patient, targetLanguage);
                    break;
                case Observation:
                    observation = (Observation) bundleEntryComponent.getResource();
                    observationTranslationService.translate(observation, targetLanguage);
                    break;
                case ServiceRequest:
                    serviceRequest = (ServiceRequest) bundleEntryComponent.getResource();
                    serviceRequestTranslationService.translate(serviceRequest, targetLanguage);
                    break;
            }
        }
        TMResponseStructure tmResponseStructure = new TMResponseStructure(fhirDocument, "success", Collections.emptyList(), Collections.emptyList());
        return tmResponseStructure;
    }
}
