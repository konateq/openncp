package eu.europa.ec.sante.openncp.core.server.nc.mock.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Helper class for loading file resources from the classpath.
 */
public class ResourceLoader {

    private final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

    /**
     * Locates and loads file for the specified filename. Returns contents as a String.
     *
     * @param resourceName Filename (without path)
     * @return
     */
    public String getResource(String resourceName) {

        String resourceStr = "";

        ClassLoader cl = getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(resourceName);

        if (is != null) {
            resourceStr = streamToString(is);
        }

        if (resourceStr.isEmpty()) {
            logger.debug("ERROR - Failed to load resource with name '{}'", resourceName);
        }

        return resourceStr;
    }

    /**
     * @param in
     * @return
     */
    private String streamToString(InputStream in) {

        StringBuilder out = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                out.append(line);
            }
            br.close();
            return out.toString();
        } catch (Exception e) {
            logger.debug("Exception: '{}'", e.getMessage(), e);
        }
        return out.toString();
    }

    public byte[] getResourceAsByteArray(String resourceName) {

        ClassLoader cl = getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(resourceName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[100000];
        int i = 0;
        try {
            while ((i = is.read(buff, 0, buff.length)) > 0) {
                baos.write(buff, 0, i);
            }
            is.close();
        } catch (IOException e) {
            logger.error("IOException: '{}'", e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}
