package eu.europa.ec.sante.openncp.core.client.ihe.xdr;

import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.core.client.api.EpsosDocument;
import eu.europa.ec.sante.openncp.core.client.api.PatientDemographics;
import eu.europa.ec.sante.openncp.core.client.ihe.dts.PatientDemographicsDts;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class XdrRequestDts {

    private XdrRequestDts() {
    }

    public static XdrRequest newInstance(final EpsosDocument document, final PatientDemographics patient) throws ParseException {

        if (document == null) {
            return null;
        }
        final XdrRequest result = new XdrRequest();
        // Review if the GenericDocumentCode is required at this level.
        result.setCda(new String(document.getBase64Binary(), StandardCharsets.UTF_8));
        result.setCdaId(document.getUuid());
        result.setSubmissionSetId(document.getSubmissionSetId());
        result.setPatient(PatientDemographicsDts.toDataModel(patient));
        result.setCountryCode(Constants.COUNTRY_CODE);
        result.setCountryName(Constants.COUNTRY_NAME);

        return result;
    }
}
