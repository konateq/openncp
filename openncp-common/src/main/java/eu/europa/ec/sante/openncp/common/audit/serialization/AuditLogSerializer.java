package eu.europa.ec.sante.openncp.common.audit.serialization;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public interface AuditLogSerializer {

    List<File> listFiles();

    Serializable readObjectFromFile(File inFile) throws IOException, ClassNotFoundException;

    void writeObjectToFile(Serializable message);

    enum Type {
        
        ATNA("ATNA"),
        AUDIT_MANAGER("AM");

        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        private long counterValue = 0;
        private String filePrefix;

        Type(String prefix) {
            this.filePrefix = prefix;
        }

        public String getDir() {
            return "audit-backup/";
        }
        
        public String getTrashDir() {
            return getDir() + "audit-trash/";
        }

        public String getFilePrefix() {
            return filePrefix + "-AuditLogBackup-";
        }

        public String getFileSuffix() {
            return ".ser";
        }

        public String getNewFileName() {
            return getDir() + getFilePrefix() + getTimeStamp() + "-" + getCounterValue() + getFileSuffix();
        }

        public long getCounterValue() {
            if (counterValue > 9999) {
                counterValue = 0;
            }
            return ++counterValue;
        }

        public String getTimeStamp() {
            return sdf.format(new Date());
        }
    }

	void moveFile(File file);
}
