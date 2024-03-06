package eu.europa.ec.sante.openncp.core.server.ihe.xcpd;

import tr.com.srdc.epsos.data.model.PatientDemographics;

public interface PatientSearchInterfaceWithDemographics extends PatientSearchInterface {

    /**
     * Sets patient demographics for NI
     */
    void setPatientDemographics(PatientDemographics pd);
}
