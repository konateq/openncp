package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.client.ObjectFactory;
import eu.europa.ec.sante.openncp.core.client.PatientDemographics;
import eu.europa.ec.sante.openncp.core.client.QueryPatientResponse;

import java.util.List;

/**
 * This is a Data Transformation Service. This provides functions to transform
 * data into a QueryPatientResponse object.
 */
public class QueryPatientResponseDts {

    /**
     * Private constructor to disable class instantiation.
     */
    private QueryPatientResponseDts() {
    }

    static final ObjectFactory objectFactory = new ObjectFactory();

    /**
     * Converts a list of {@link PatientDemographics} object into a
     * {@link QueryPatientResponse} new instance.
     *
     * @param patientDemographics the object to be converted.
     * @return {@link QueryPatientResponse} object - the result of the
     * conversion.
     *
     * @see QueryPatientResponse
     * @see PatientDemographics
     * @see List
     */
    public static QueryPatientResponse newInstance(final List<PatientDemographics> patientDemographics) {

        if (patientDemographics == null) {
            return null;
        }
        final QueryPatientResponse result = objectFactory.createQueryPatientResponse();
        result.getReturn().addAll(patientDemographics);
        return result;
    }
}
