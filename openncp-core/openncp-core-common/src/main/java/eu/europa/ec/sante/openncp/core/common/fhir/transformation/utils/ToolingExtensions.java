package eu.europa.ec.sante.openncp.core.common.fhir.transformation.utils;

import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.utilities.Utilities;

public class ToolingExtensions {

    public static final String EXT_TRANSLATION = "http://hl7.org/fhir/StructureDefinition/translation";

    public static void addLanguageTranslation(Element element, String lang, String value) {
        if (Utilities.noString(lang) || Utilities.noString(value))
            return;

        Extension extension = new Extension().setUrl(EXT_TRANSLATION);
        extension.addExtension().setUrl("lang").setValue(new StringType(lang));
        extension.addExtension().setUrl("content").setValue(new StringType(value));
        element.addExtension(extension);
    }
}
