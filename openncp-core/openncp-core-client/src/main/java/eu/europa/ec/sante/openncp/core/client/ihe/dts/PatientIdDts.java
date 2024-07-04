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

    public static PatientId toDataModel(final eu.europa.ec.sante.openncp.core.client.api.PatientId patientId) {

        if (patientId == null) {
            return null;
        }

        final PatientId result = new PatientId();
        result.setRoot(StringUtils.trim(patientId.getRoot()));
        result.setExtension(StringUtils.trim(patientId.getExtension()));
        return result;
    }

    public static eu.europa.ec.sante.openncp.core.client.api.PatientId fromDataModel(final PatientId patientId) {

        if (patientId == null) {
            return null;
        }

        final eu.europa.ec.sante.openncp.core.client.api.PatientId result = new eu.europa.ec.sante.openncp.core.client.api.PatientId();
        result.setRoot(patientId.getRoot());
        result.setExtension(patientId.getExtension());
        return result;
    }

    public static List<PatientId> toDataModel(final List<eu.europa.ec.sante.openncp.core.client.api.PatientId> patientIds) {

        if (patientIds == null) {
            return Collections.emptyList();
        }
        final List<PatientId> result = new ArrayList<>();
        for (final eu.europa.ec.sante.openncp.core.client.api.PatientId patientId : patientIds) {
            result.add(toDataModel(patientId));
        }
        return result;
    }

    public static List<eu.europa.ec.sante.openncp.core.client.api.PatientId> fromDataModel(final List<PatientId> patientIds) {

        if (patientIds == null) {
            return Collections.emptyList();
        }
        final List<eu.europa.ec.sante.openncp.core.client.api.PatientId> result = new ArrayList<>();
        for (final PatientId patientId : patientIds) {
            result.add(fromDataModel(patientId));
        }
        return result;
    }
}
