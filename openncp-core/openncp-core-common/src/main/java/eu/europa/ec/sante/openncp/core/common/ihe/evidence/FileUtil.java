package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
    private static final String LOG_FMT_MESSAGE_IOEXCEPTION = "IOException: {}";

    private FileUtil() {
    }

    /**
     * Constructs a new file with the given content and filePath. If a file with the same name already exists,
     * simply overwrites the content.
     *
     * @param filePath
     * @param content  : expressed as byte[]
     * @throws IOException : [approved by gunes] when the file cannot be
     *                     created. possible causes: 1) invalid filePath, 2) already existing file
     *                     cannot be deleted due to read-write locks
     * @author Gunes
     */
    public static void constructNewFile(final String filePath, final byte[] content) throws IOException {

        final File file = new File(filePath);
        final boolean dirCreated = file.mkdirs();
        boolean fileDeleted = false;
        final boolean fileCreated;

        if (file.exists()) {

            Files.delete(Paths.get(filePath));
            fileDeleted = true;
        }
        fileCreated = file.createNewFile();
        LOGGER.debug("New File result: Folder created: '{}' - Old File Deleted: '{}' - New File Created: '{}'",
                dirCreated, fileDeleted, fileCreated);

        try (final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        } catch (final IOException e) {
            LOGGER.error(LOG_FMT_MESSAGE_IOEXCEPTION, e.getMessage(), e);
        }
    }

    private static byte[] getBytes(final File file) throws IOException {

        try (final InputStream is = new FileInputStream(file)) {

            // Get the size of the file
            final long length = file.length();
            if (length > Integer.MAX_VALUE) {
                LOGGER.error("File is too large to process");
                return new byte[0];
            }
            // Create the byte array to hold the data
            final byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead;
            while ((offset < bytes.length) && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {

                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            return bytes;
        }
    }

    public static byte[] getBytesFromFile(final String fileURI) throws IOException {
        final File file = new File(fileURI);
        return getBytes(file);
    }

    public static byte[] readFromURI(final URI uri) throws IOException {

        if (uri.toString().contains("http:")) {
            final URL url = uri.toURL();
            final URLConnection urlConnection = url.openConnection();
            final int length = urlConnection.getContentLength();
            LOGGER.info("length of content in URL = '{}'", length);
            if (length > -1) {
                final byte[] pureContent = new byte[length];
                final DataInputStream dis = new DataInputStream(urlConnection.getInputStream());
                dis.readFully(pureContent, 0, length);
                dis.close();

                return pureContent;
            } else {
                throw new IOException("Unable to determine the content-length of the document pointed at " + url.toString());
            }
        } else {
            final String file = readWholeFile(uri);
            if (file == null) {
                throw new IllegalArgumentException("Content of the file is null");
            }
            return file.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static List<String> readFileLines(final String filePath) {

        try (final FileInputStream fis = new FileInputStream(filePath)) {
            final BufferedReader buf;
            final List<String> rules = new ArrayList<>();

            final InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            buf = new BufferedReader(inputStreamReader);

            String temp;
            while ((temp = buf.readLine()) != null) {
                rules.add(temp);
            }
            buf.close();

            return rules;
        } catch (final IOException e) {
            LOGGER.error(LOG_FMT_MESSAGE_IOEXCEPTION, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public static String readWholeFile(final File file) {

        return readInputStream(file, StandardCharsets.UTF_8);
    }

    public static String readWholeFile(final String filePath) {

        return readInputStream(new File(filePath), StandardCharsets.UTF_8);
    }

    public static String readWholeFile(final String filePath, final String encoding) {

        return readInputStream(new File(filePath), Charset.forName(encoding));
    }

    private static String readWholeFile(final URI uri) {

        return readInputStream(new File(uri), StandardCharsets.UTF_8);
    }

    public static void writeToFile(final File file, final String content) {


        try (final FileOutputStream fos = new FileOutputStream(file)) {

            final OutputStreamWriter outStreamWriter = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            final BufferedWriter bufferedWriter = new BufferedWriter(outStreamWriter);
            bufferedWriter.write(content);
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (final IOException e) {
            LOGGER.error(LOG_FMT_MESSAGE_IOEXCEPTION, e.getMessage(), e);
        }
    }

    private static String readInputStream(final File file, final Charset charset) {

        try (final FileInputStream fis = new FileInputStream(file)) {
            final BufferedReader buf;
            final StringBuilder rules = new StringBuilder();

            final InputStreamReader inputStreamReader = new InputStreamReader(fis, charset);
            buf = new BufferedReader(inputStreamReader);
            String temp;
            while ((temp = buf.readLine()) != null) {
                rules.append(temp).append("\n");
            }
            buf.close();
            return rules.toString();
        } catch (final IOException e) {
            LOGGER.error(LOG_FMT_MESSAGE_IOEXCEPTION, e.getMessage(), e);
            return null;
        }
    }
}
