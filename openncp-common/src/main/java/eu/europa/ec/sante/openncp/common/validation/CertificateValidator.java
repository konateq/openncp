package eu.europa.ec.sante.openncp.common.validation;

public interface CertificateValidator {

    String validate(String certificate, String type, boolean checkRevocation);
}
