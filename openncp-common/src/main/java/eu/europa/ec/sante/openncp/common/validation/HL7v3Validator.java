package eu.europa.ec.sante.openncp.common.validation;


import eu.europa.ec.sante.openncp.common.NcpSide;

public interface HL7v3Validator {

    String validateDocument(String document, String validator, NcpSide ncpSide);

    String validateBase64Document(String base64Document, String validator, NcpSide ncpSide);
}
