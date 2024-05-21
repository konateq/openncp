package eu.europa.ec.sante.openncp.api.common.fhircontext.r4.resources;

import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.Composition;

@ResourceDef(name = "Composition", profile = CompositionLabReportEu.PROFILE)
public class CompositionLabReportEu extends Composition {

    public static final String PROFILE = "http://hl7.eu/fhir/laboratory/StructureDefinition/Composition-eu-lab";

//    @Child(name = "text", type = { Narrative.class }, order = 5, min = 0, max = 1, modifier = false, summary = false)
//    @Description(shortDefinition = "Narrative text", value = "Narrative text")
//    protected Narrative text;

    //TODO rest of the lab result
}
