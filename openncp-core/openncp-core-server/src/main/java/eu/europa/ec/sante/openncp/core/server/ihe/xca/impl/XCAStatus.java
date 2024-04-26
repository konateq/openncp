package eu.europa.ec.sante.openncp.core.server.ihe.xca.impl;

public enum XCAStatus {

    SUCCESS("Success"), FAILURE("Failure");

    private final String status;

    XCAStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}