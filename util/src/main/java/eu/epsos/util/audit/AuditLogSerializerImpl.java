package eu.epsos.util.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogSerializerImpl implements AuditLogSerializer {

    private static final Logger log = LoggerFactory.getLogger(AuditLogSerializer.class);
    private Type type;

    public AuditLogSerializerImpl(Type type) {
        this.type = type;
    }

    public List<File> listFiles() {

        List<File> files = new ArrayList<>();
        File path = getPath();
        if (isPathValid(path)) {
            File[] srcFiles = path.listFiles();
            if (srcFiles == null) {
                return new ArrayList<>();
            }
            for (File file : srcFiles) {
                if (isAuditLogBackupWriterFile(file)) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    public Serializable readObjectFromFile(File inFile) throws IOException, ClassNotFoundException {

        InputStream buffer = null;
        ObjectInput input = null;
        try (InputStream file = new FileInputStream(inFile)) {

            buffer = new BufferedInputStream(file);
            input = new ObjectInputStream(buffer);
            return (Serializable) input.readObject();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.warn("Unable to close: '{}'", e.getMessage(), e);
                }
            }
            close(buffer);
        }
    }

    public void writeObjectToFile(Serializable message) {

        String path = System.getenv("EPSOS_PROPS_PATH") + type.getNewFileName();
        OutputStream buffer = null;
        ObjectOutput output = null;

        try (OutputStream file = new FileOutputStream(path)) {

            if (message != null) {

                buffer = new BufferedOutputStream(file);
                output = new ObjectOutputStream(buffer);
                output.writeObject(message);
                log.error("Error occurred while writing AuditLog to OpenATNA! AuditLog saved to: '{}'", path);
            }
        } catch (Exception e) {
            log.error("Unable to send AuditLog to OpenATNA nor able write auditLog backup! Dumping to log: '{}'", message.toString(), e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.warn("Unable to close: " + e.getMessage(), e);
                }
            }
            close(buffer);
        }
    }

    private File getPath() {

        return new File(System.getenv("EPSOS_PROPS_PATH") + type.getDir());
    }

    private boolean isAuditLogBackupWriterFile(File file) {

        String fileName = file.getName();
        return fileName.startsWith(type.getFilePrefix()) && fileName.endsWith(type.getFileSuffix());
    }

    private boolean isPathValid(File path) {

        if (!path.exists()) {
            log.error("Source path ('{}') does not exist!", path);
            return false;
        } else if (!path.isDirectory()) {
            log.error("Source path ('{}') is not a diredtory!", path);
            return false;
        }

        return true;
    }

    private void close(Closeable c) {

        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            log.warn("Unable to close closeable: '{}'", e.getMessage(), e);
        }
    }
}
