package eu.epsos.validation.datamodel.hl7v3;

import eu.epsos.validation.datamodel.common.ObjectType;

/**
 * This enumerator gathers all the models used in the HL7v3 Validator at EVS Client.
 *
 * @author Marcelo Fonseca <marcelo.fonseca@iuz.pt>
 * @deprecated
 */
@Deprecated
public enum Hl7v3Model {

    PDQV3_ACCEPT_ACK("PDQv3 - Accept Acknowledgement", ObjectType.PDQ),
    PDQV3_QUERY("PDQv3 - Patient Demographics Query", ObjectType.PDQ),
    PDQV3_HL7V3_CANCELLATION("PDQv3 - Patient Demographics Query HL7V3 Cancellation", ObjectType.PDQ),
    PDQV3_HL7V3_CONTINUATION("PDQv3 - Patient Demographics Query HL7V3 Continuation", ObjectType.PDQ),
    PDQV3_QUERY_RESPONSE("PDQv3 - Patient Demographics Query Response", ObjectType.PDQ),
    XCPD_REQUEST("XCPD - Cross Gateway Patient Discovery Request", ObjectType.XCPD_QUERY_REQUEST),
    XCPD_REQUEST_DEFERRED_OPTION("XCPD - Cross Gateway Patient Discovery Request (Deferred option)", ObjectType.XCPD_QUERY_REQUEST),
    XCPD_PATIENT_LOCATION_QUERY_REQUEST("XCPD - Patient Location Query Request", ObjectType.XCPD_QUERY_REQUEST),
    XCPD_PATIENT_LOCATION_QUERY_RESPONSE("XCPD - Patient Location Query Response", ObjectType.XCPD_QUERY_RESPONSE);

    private String name;
    private ObjectType objectType;

    Hl7v3Model(String s, ObjectType ot) {
        name = s;
        objectType = ot;
    }

    public static Hl7v3Model checkModel(String model) {
        for (Hl7v3Model m : Hl7v3Model.values()) {
            if (model.equals(m.toString())) {
                return m;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public ObjectType getObjectType() {
        return objectType;
    }
}
