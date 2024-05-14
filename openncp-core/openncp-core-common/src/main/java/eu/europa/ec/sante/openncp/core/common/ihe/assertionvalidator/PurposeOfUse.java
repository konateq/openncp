package eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator;

public enum PurposeOfUse {

    EMERGENCY("EMERGENCY"),
    TREATMENT("TREATMENT");

    private final String purpose;

    PurposeOfUse(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return purpose;
    }
}
