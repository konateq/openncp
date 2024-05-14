package eu.europa.ec.sante.openncp.core.common.fhir.tsam.util;

public interface Error {
    String getCode();


    /**
     * @return String - Description
     */
    String getDescription();


    /*
     * @return String in format code:description
     */
    @Override
    String toString();
}
