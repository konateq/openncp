package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.common.datamodel.PatientId;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is an Data Transformation Service. This provide functions to transform data into a PatientDemographics object.
 *
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public final class PatientIdDts {

    private PatientIdDts() {
    }

    public static List<PatientId> newInstance(final eu.europa.ec.sante.openncp.core.client.PatientId[] patientIdList) {

        if (patientIdList == null) {
            return Collections.emptyList();
        }

        List<PatientId> result = new ArrayList<>(patientIdList.length);
        for (eu.europa.ec.sante.openncp.core.client.PatientId patientId : patientIdList) {
            result.add(newInstance(patientId));
        }

        return result;
    }

    public static PatientId newInstance(final eu.europa.ec.sante.openncp.core.client.PatientId patientId) {

        if (patientId == null) {
            return null;
        }

        PatientId result = new PatientId();
        result.setRoot(StringUtils.trim(patientId.getRoot()));
        result.setExtension(StringUtils.trim(patientId.getExtension()));

        return result;
    }

    public static eu.europa.ec.sante.openncp.core.client.PatientId[] newInstance(final List<PatientId> patientId) {
        if (patientId == null) {
            return null;
        }

        eu.europa.ec.sante.openncp.core.client.PatientId[] result = new eu.europa.ec.sante.openncp.core.client.PatientId[patientId.size()];
        for(int i = 0; i < patientId.size(); i++) {
            result[i] = newInstance(patientId.get(i));
        }

        return result;
    }

    public static eu.europa.ec.sante.openncp.core.client.PatientId newInstance(final PatientId patientId) {
        if (patientId == null) {
            return null;
        }

        eu.europa.ec.sante.openncp.core.client.PatientId result = eu.europa.ec.sante.openncp.core.client.PatientId.Factory.newInstance();
        result.setRoot(patientId.getRoot());
        result.setExtension(patientId.getExtension());

        return result;
    }
}
