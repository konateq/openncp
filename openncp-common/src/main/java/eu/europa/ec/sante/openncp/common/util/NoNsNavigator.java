package eu.europa.ec.sante.openncp.common.util;

import org.jaxen.dom.DocumentNavigator;

public class NoNsNavigator extends DocumentNavigator {

    @Override
    public String getElementNamespaceUri(Object obj) {
        return null;
    }
}
