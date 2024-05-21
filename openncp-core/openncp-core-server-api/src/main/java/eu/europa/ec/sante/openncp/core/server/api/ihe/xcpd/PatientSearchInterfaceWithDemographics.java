package eu.europa.ec.sante.openncp.core.server.api.ihe.xcpd;


import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.PatientDemographics;

public interface PatientSearchInterfaceWithDemographics extends PatientSearchInterface {

    /**
     * Sets patient demographics for NI
     */
    void setPatientDemographics(PatientDemographics pd);
}
