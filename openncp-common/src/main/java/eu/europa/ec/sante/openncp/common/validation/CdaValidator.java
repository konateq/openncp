package eu.europa.ec.sante.openncp.common.validation;

public interface CdaValidator {

    String validateDocument(String document, String validator, NcpSide ncpSide);

    String validateBase64Document(String base64Document, String validator, NcpSide ncpSide);
}
