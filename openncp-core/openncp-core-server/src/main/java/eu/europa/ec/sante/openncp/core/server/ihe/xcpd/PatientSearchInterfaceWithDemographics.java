package eu.europa.ec.sante.openncp.core.server.ihe.xcpd;


import eu.europa.ec.sante.openncp.core.common.datamodel.PatientDemographics;

public interface PatientSearchInterfaceWithDemographics extends PatientSearchInterface {

    /**
     * Sets patient demographics for NI
     */
    void setPatientDemographics(PatientDemographics pd);
}
