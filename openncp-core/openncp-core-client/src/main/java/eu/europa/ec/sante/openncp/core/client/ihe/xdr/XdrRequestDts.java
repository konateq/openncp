package eu.europa.ec.sante.openncp.core.client.ihe.xdr;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.core.client.EpsosDocument1;
import eu.europa.ec.sante.openncp.core.client.PatientDemographics;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.PatientDemographicsDts;

public class XdrRequestDts {

    private XdrRequestDts() {
    }

    public static XdrRequest newInstance(final EpsosDocument1 document, final PatientDemographics patient) throws ParseException {

        if (document == null) {
            return null;
        }
        XdrRequest result = new XdrRequest();
        //  TODO: Review if the GenericDocumentCode is required at this level.
        result.setCda(new String(document.getBase64Binary(), StandardCharsets.UTF_8));
        result.setCdaId(document.getUuid());
        result.setSubmissionSetId(document.getSubmissionSetId());
        result.setPatient(PatientDemographicsDts.newInstance(patient));
        result.setCountryCode(Constants.COUNTRY_CODE);
        result.setCountryName(Constants.COUNTRY_NAME);

        return result;
    }
}
