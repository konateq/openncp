package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.common.datamodel.PatientDemographics;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.*;

/**
 * This is an Data Transformation Service. This provide functions to transform data into a PatientDemographics object.
 *
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 * @author Marcelo Fonseca<code> - marcelo.fonseca@iuz.pt</code>
 */
public final class PatientDemographicsDts {

    /**
     * Private constructor to disable class instantiation.
     */
    private PatientDemographicsDts() {
    }

    /**
     * Converts a QueryPatientDocument object into a PatienDemographics Object.
     *
     * @param patientDemographics representing a QueryPatientDocument.
     * @return a PatientDemographics object.
     * @throws ParseException
     * @see PatientDemographics
     * @see eu.europa.ec.sante.openncp.core.client.QueryPatientDocument
     */
    public static PatientDemographics newInstance(final eu.europa.ec.sante.openncp.core.client.PatientDemographics patientDemographics) throws ParseException {

        if (patientDemographics == null) {
            return null;
        }

        final PatientDemographics result = new PatientDemographics();

        if (StringUtils.isNotBlank(patientDemographics.getAdministrativeGender())) {
            result.setAdministrativeGender(PatientDemographics.Gender.parseGender(StringUtils.trim(patientDemographics.getAdministrativeGender())));
        }
        if (patientDemographics.getBirthDate() != null) {
            result.setBirthDate(patientDemographics.getBirthDate().getTime());
        }
        if (StringUtils.isNotBlank(patientDemographics.getCity())) {
            result.setCity(StringUtils.trim(patientDemographics.getCity()));
        }
        if (StringUtils.isNotBlank(patientDemographics.getCountry())) {
            result.setCountry(StringUtils.trim(patientDemographics.getCountry()));
        }
        if (StringUtils.isNotBlank(patientDemographics.getEmail())) {
            result.setEmail(StringUtils.trim(patientDemographics.getEmail()));
        }
        if (StringUtils.isNotBlank(patientDemographics.getFamilyName())) {
            result.setFamilyName(StringUtils.trim(patientDemographics.getFamilyName()));
        }
        if (StringUtils.isNotBlank(patientDemographics.getGivenName())) {
            result.setGivenName(StringUtils.trim(patientDemographics.getGivenName()));
        }
        if (patientDemographics.getPatientIdArray() != null) {
            result.setIdList(PatientIdDts.newInstance(patientDemographics.getPatientIdArray()));
        }
        if (StringUtils.isNotBlank(patientDemographics.getPostalCode())) {
            result.setPostalCode(StringUtils.trim(patientDemographics.getPostalCode()));
        }
        if (StringUtils.isNotBlank(patientDemographics.getStreetAddress())) {
            result.setStreetAddress(StringUtils.trim(patientDemographics.getStreetAddress()));
        }
        if (StringUtils.isNotBlank(patientDemographics.getTelephone())) {
            result.setTelephone(StringUtils.trim(patientDemographics.getTelephone()));
        }

        return result;
    }

    public static eu.europa.ec.sante.openncp.core.client.PatientDemographics newInstance(final PatientDemographics patientDemographics) {

        if (patientDemographics == null) {
            return null;
        }

        final eu.europa.ec.sante.openncp.core.client.PatientDemographics result = eu.europa.ec.sante.openncp.core.client.PatientDemographics.Factory.newInstance();

        if (patientDemographics.getAdministrativeGender() != null) {
            result.setAdministrativeGender(patientDemographics.getAdministrativeGender().toString());
        }

        if (patientDemographics.getBirthDate() != null) {
            final Calendar calendar = new GregorianCalendar();
            calendar.setTime(patientDemographics.getBirthDate());
            result.setBirthDate(calendar);
        }
        result.setCity(patientDemographics.getCity());
        result.setCountry(patientDemographics.getCountry());
        result.setEmail(patientDemographics.getEmail());
        result.setFamilyName(patientDemographics.getFamilyName());
        result.setGivenName(patientDemographics.getGivenName());
        result.setPatientIdArray(PatientIdDts.newInstance(patientDemographics.getIdList()));
        result.setPostalCode(patientDemographics.getPostalCode());
        result.setStreetAddress(patientDemographics.getStreetAddress());
        result.setTelephone(patientDemographics.getTelephone());

        return result;
    }

    public static List<eu.europa.ec.sante.openncp.core.client.PatientDemographics> newInstance(final List<PatientDemographics> patientDemList) {

        if (patientDemList == null) {
            return Collections.emptyList();
        }

        final List<eu.europa.ec.sante.openncp.core.client.PatientDemographics> result = new ArrayList<>(patientDemList.size());

        for (PatientDemographics pd : patientDemList) {
            result.add(newInstance(pd));
        }

        return result;
    }
}
