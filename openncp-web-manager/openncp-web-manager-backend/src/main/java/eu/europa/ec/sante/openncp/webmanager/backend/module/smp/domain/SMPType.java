package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.domain;

/**
 * @author Inês Garganta
 */

public enum SMPType {

    //SPECIFICATION
    Country_B_Identity_Provider("Country B Identity Provider"),
    Patient_Identification_Authentication("Patient Identification and Authentication"),
    Provisioning_of_Data_Provide("Provisioning of Data - Provide"),
    Provisioning_of_Data_BPPC("Provisioning of Data - BPPC"),
    Request_of_Data_Fetch("Request of Data - Fetch"),
    Request_of_Data_Query("Request of Data - Query"),
    Request_of_Data_Retrieve("Request of Data - Retrieve"),
    International_Search_Mask("International Search Mask"),
    Redirect("Redirect");

    private final String description;

    SMPType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
