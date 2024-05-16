package eu.europa.ec.sante.openncp.core.common.tsam;

import eu.europa.ec.sante.openncp.core.common.tsam.domain.CodeSystemConcept;

public class RetrievedConcept extends CodeSystemConcept {
    private String designation;
    private String language;

    public RetrievedConcept(CodeSystemConcept concept) {
        this.id = concept.getId();
        this.code = concept.getCode();
        this.definition = concept.getDefinition();
        this.status = concept.getStatus();
        this.statusDate = concept.getStatusDate();
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RetrievedConcept [");
        if (designation != null) {
            builder.append("designation=");
            builder.append(designation);
            builder.append(", ");
        }
        if (language != null) {
            builder.append("language=");
            builder.append(language);
            builder.append(", ");
        }
        if (code != null) {
            builder.append("code=");
            builder.append(code);
            builder.append(", ");
        }
        builder.append("id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }
}
