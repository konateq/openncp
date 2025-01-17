package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import org.herasaf.xacml.core.api.PDP;
import org.herasaf.xacml.core.context.RequestMarshaller;
import org.herasaf.xacml.core.context.ResponseMarshaller;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResponseType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.herasaf.xacml.core.policy.impl.AttributeAssignmentType;
import org.herasaf.xacml.core.policy.impl.EffectType;
import org.herasaf.xacml.core.policy.impl.ObligationType;
import org.herasaf.xacml.core.policy.impl.ObligationsType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.LinkedList;
import java.util.List;

public class EnforcePolicy {

    private final PDP pdp;
    private Document responseAsDocument;
    private ResponseType responseAsObject;
    private LinkedList<ESensObligation> obligationList;


    public EnforcePolicy(final PDP simplePDP) throws EnforcePolicyException {
        if (simplePDP == null) {
            throw new EnforcePolicyException("PDP is null");
        }
        synchronized (this) {
            this.pdp = simplePDP;
        }

    }

    public void decide(final Element request) throws EnforcePolicyException {

        if (request == null) {
            throw new EnforcePolicyException("No request have been passed");
        }
        try {
            final RequestType myrequest = RequestMarshaller.unmarshal(request);
            final ResponseType response;
            synchronized (this) {
                response = pdp.evaluate(myrequest);
            }
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setXIncludeAware(false);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.newDocument();
            ResponseMarshaller.marshal(response, doc);
            this.setResponseAsDocument(doc);
            this.setResponseAsObject(response);

            // now check the obligations. We assume in this implementation a single evaluation per request
            final List<ResultType> results = response.getResults();
            if (results == null || results.size() != 1) {
                throw new EnforcePolicyException("Wrong results size");
            }

            final ResultType result = results.get(0);
            final ObligationsType obligationsType = result.getObligations();
            if (obligationsType != null) {
                parseObligations(obligationsType);
            }
        } catch (final Exception e) {
            throw new EnforcePolicyException("Unable to evaluate: "
                    + e.getMessage(), e);
        }

    }

    private void parseObligations(final ObligationsType obligationsType)
            throws EnforcePolicyException {

        final LinkedList<ESensObligation> obligationList = new LinkedList<>();
        final List<ObligationType> oblType = obligationsType.getObligations();

        for (final ObligationType obl : oblType) {
            final ESensObligation eSensObligation;
            if (obl.getFulfillOn().compareTo(EffectType.PERMIT) == 0) {
                eSensObligation = new PERMITEsensObligation();
            } else if (obl.getFulfillOn().compareTo(EffectType.DENY) == 0) {
                eSensObligation = new DENYEsensObligation();
            } else {
                throw new EnforcePolicyException("Unkonwn effect type: "
                        + obl.getFulfillOn().name());
            }

            final String oblId = obl.getObligationId();

            final List<AttributeAssignmentType> attrAssignments = obl
                    .getAttributeAssignments();
            for (final AttributeAssignmentType assignment : attrAssignments) {
                assignment.getAttributeId();
                assignment.getDataType().getDatatypeURI();
                assignment.getContent();
            }
            eSensObligation.setAttributeAssignments(attrAssignments);
            eSensObligation.setObligationID(oblId);

            obligationList.add(eSensObligation);
        }
        setObligationList(obligationList);
    }

    public List<ESensObligation> getObligationList() {
        return this.obligationList;
    }

    private void setObligationList(final LinkedList<ESensObligation> obligationList) {

        this.obligationList = obligationList;
    }

    public Document getResponseAsDocument() {
        return responseAsDocument;
    }

    public void setResponseAsDocument(final Document responseAsDocument) {
        this.responseAsDocument = responseAsDocument;
    }

    public ResponseType getResponseAsObject() {
        return responseAsObject;
    }

    public void setResponseAsObject(final ResponseType responseAsObject) {
        this.responseAsObject = responseAsObject;
    }

}
