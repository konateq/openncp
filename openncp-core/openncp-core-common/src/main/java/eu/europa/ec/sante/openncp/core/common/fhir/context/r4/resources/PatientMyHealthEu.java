package eu.europa.ec.sante.openncp.core.common.fhir.context.r4.resources;

import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.Patient;

@ResourceDef(name = "Patient", profile = PatientMyHealthEu.PROFILE)
public class PatientMyHealthEu extends Patient {

    public static final String PROFILE = "http://fhir.ehdsi.eu/laboratory/StructureDefinition/Patient-lab-myhealtheu";
}
