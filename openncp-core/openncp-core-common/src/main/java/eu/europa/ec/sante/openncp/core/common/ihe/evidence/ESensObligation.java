package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import org.herasaf.xacml.core.policy.impl.AttributeAssignmentType;

import java.util.List;

/**
 * The four methods will be removed if there is no need (e.g., we don't need to
 * pack a single NR token)
 *
 * @author max
 */
public class ESensObligation {

    private List<AttributeAssignmentType> unknown;
    private String obligationID;

    public final List<AttributeAssignmentType> getAttributeAssignments() {
        return unknown;
    }

    public void setAttributeAssignments(final List<AttributeAssignmentType> attrAssignments) {
        this.unknown = attrAssignments;
    }

    public String getObligationID() {
        return obligationID;
    }

    public void setObligationID(final String obligationID) {
        this.obligationID = obligationID;
    }
}
