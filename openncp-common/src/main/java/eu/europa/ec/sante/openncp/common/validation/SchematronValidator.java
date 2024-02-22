package eu.europa.ec.sante.openncp.common.validation;

public interface SchematronValidator {

    String validateObject(String base64Object, String xmlReferencedStandard, String xmlMetadata);
}
