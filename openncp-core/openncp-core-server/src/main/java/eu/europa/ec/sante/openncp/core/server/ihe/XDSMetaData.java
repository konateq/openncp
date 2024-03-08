package eu.europa.ec.sante.openncp.core.server.ihe;

public enum XDSMetaData {

    PATIENT_ID("XDSDocumentEntry.patientId"),
    UNIQUE_ID("XDSDocumentEntry.uniqueId");

    private String name;

    XDSMetaData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
