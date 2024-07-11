package eu.europa.ec.sante.openncp.common.util;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UUIDHelper.class);

    private UUIDHelper() {
    }

    public static String encodeAsURN(String uuid) {

        uuid = RegExUtils.removeAll(uuid, "urn:uuid:");
        uuid = RegExUtils.removeAll(uuid, "_");
        uuid = RegExUtils.removeAll(uuid, "-");

        final Pattern pattern = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

        final String uuidTemp = pattern.matcher(uuid).replaceAll("$1-$2-$3-$4-$5");

        if (isUUIDValid(uuidTemp)) {
            return "urn:uuid:" + uuidTemp;
        } else {
            return "";
        }
    }

    public static boolean isUUIDValid(final String uuid) {
        if (StringUtils.isNotBlank(uuid) && uuid.length() <= 36) {
            try {
                final UUID parsedUuid = UUID.fromString(uuid);
                LOGGER.debug("Valid UUID: '{}'", uuid);
                return true;
            } catch (final IllegalArgumentException e) {
                LOGGER.error("IllegalArgumentException: '{}'", e.getMessage());
                return false;
            }
        }
        return false;
    }
}
