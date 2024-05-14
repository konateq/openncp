package eu.europa.ec.sante.openncp.core.common.ihe.transformation.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SchematronResult implements TMConstants {

    private boolean valid;

    private NodeList errors;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public NodeList getErrors() {
        return errors;
    }

    public void setErrors(NodeList errors) {
        this.errors = errors;
    }

    public String toString() {

        int errCount;
        if (errors != null) {
            errCount = errors.getLength();
            return valid + ", " + errCount + NEWLINE + errorsToString();
        } else {
            return "Schematron Result node list is empty";
        }
    }

    private String errorsToString() {

        if (errors != null && errors.getLength() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < errors.getLength(); i++) {
                Element error = (Element) errors.item(i);
                sb.append("location:").append(error.getAttribute("location")).append(NEWLINE);
                sb.append("test:").append(error.getAttribute("test")).append(NEWLINE);
                Element text = (Element) error.getFirstChild();
                sb.append(text.getTextContent()).append(NEWLINE);
            }
            return sb.toString();
        }
        return EMPTY_STRING;
    }
}
