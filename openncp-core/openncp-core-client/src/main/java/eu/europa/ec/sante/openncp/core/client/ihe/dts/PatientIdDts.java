package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.PatientId;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a Data Transformation Service. This provides functions to transform data into a PatientDemographics object.
 */
public final class PatientIdDts {

    public static PatientId toDataModel(final eu.europa.ec.sante.openncp.core.client.PatientId patientId) {

        if (patientId == null) {
            return null;
        }

        PatientId result = new PatientId();
        result.setRoot(StringUtils.trim(patientId.getRoot()));
        result.setExtension(StringUtils.trim(patientId.getExtension()));
        return result;
    }

    public static eu.europa.ec.sante.openncp.core.client.PatientId fromDataModel(final PatientId patientId) {

        if (patientId == null) {
            return null;
        }

        eu.europa.ec.sante.openncp.core.client.PatientId result = new eu.europa.ec.sante.openncp.core.client.PatientId();
        result.setRoot(patientId.getRoot());
        result.setExtension(patientId.getExtension());
        return result;
    }

    public static List<PatientId> toDataModel(final List<eu.europa.ec.sante.openncp.core.client.PatientId> patientIds) {

        if (patientIds == null) {
            return Collections.emptyList();
        }
        List<PatientId> result = new ArrayList<>();
        for (eu.europa.ec.sante.openncp.core.client.PatientId patientId : patientIds) {
            result.add(toDataModel(patientId));
        }
        return result;
    }

    public static List<eu.europa.ec.sante.openncp.core.client.PatientId> fromDataModel(final List<PatientId> patientIds) {

        if (patientIds == null) {
            return Collections.emptyList();
        }
        List<eu.europa.ec.sante.openncp.core.client.PatientId> result = new ArrayList<>();
        for(PatientId patientId: patientIds) {
            result.add(fromDataModel(patientId));
        }
        return result;
    }
}
