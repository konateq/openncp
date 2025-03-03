package org.openhealthtools.openatna.syslog;

import org.openhealthtools.openatna.syslog.message.StringLogMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The SyslogMessageFactory is the super class of message factories. It
 * it also the point of configuration for two important options:
 * <p/>
 * 1. LogMessage objects associated with incoming messages
 * 2. The expected Syslog Message format (e.g. BSD, RFC 5424)
 * <p/>
 * LogMessage Objects understand the application specific message part of the syslog message.
 * These are mapped to an idenitfier in the syslog message. Currently 5424 messages use the MSGID to map
 * LogMessages. The MSGID represents a type of message being logged. The BSD implementation uses the TAG
 * value to map LogMessages. typically the TAG is an application name. Therefore the TAG value is required
 * to find a LogMessage of a particular type. If the TAG is not there, the default LogMessage is used.
 * <p/>
 * The default LogMessage, if none is mapped to the above values, is a simple String log message that returns
 * the message as a String.
 * The default LogMessage class can be set using the setDefaultLogMessage(Class<? extends LogMessage> cls) method.
 * <p/>
 * LogMessage objects are registered using the registerLogMessage(String msgId, Class<? extends LogMessage> cls)
 * method. NOTE this means that LogMessage implementations must have a default constructor because they are
 * instantiated using Class.newInstance(). The SyslogListener
 * <p/>
 * <p/>
 * <p/>
 * To set the type of message the server expects, use the setFactory(SyslogMessageFactory factory) method which
 * defines the SyslogMessageFactory that will be used to parse incoming data and generate a SyslogMessage.
 * <p/>
 * The default factory tries to generate RFC 5424 messages.
 * <p/>
 * This class also contains static utility methods for writing out UTF BOM values.
 *
 * @author Andrew Harrison
 */
public abstract class SyslogMessageFactory {

    protected static Map<String, Class<? extends LogMessage>> messages = new HashMap<>();

    private static Class<? extends LogMessage> defaultMessage = StringLogMessage.class;

    private static SyslogMessageFactory currFactory = null;

    public static void registerLogMessage(String msgId, Class<? extends LogMessage> cls) {
        messages.put(msgId, cls);
    }

    public static void setDefaultLogMessage(Class<? extends LogMessage> cls) {
        defaultMessage = cls;
    }

    public static SyslogMessageFactory getFactory() {
        if(currFactory == null) {
            currFactory = new GenericMessageFactory();
        }
        return currFactory;
    }

    public static void setFactory(SyslogMessageFactory factory) {
        currFactory = factory;
    }

    public static LogMessage getLogMessage(String msgId) throws SyslogException {

        Class<? extends LogMessage> m;
        if (msgId == null || msgId.isEmpty() || msgId.equals("-")) {
            m = defaultMessage;
        } else {
            m = messages.get(msgId);
            if (m == null) {
                m = defaultMessage;
            }
        }
        try {
            return m.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new SyslogException(e);
        }
    }

    /**
     * @param in
     * @return
     * @throws java.io.IOException
     */
    public static String readBom(PushbackInputStream in, String expectedEncoding) throws IOException {

        String encoding = expectedEncoding;
        var bom = new byte[4];
        int unread;
        int n = in.read(bom);

        if (n != 4) {
            throw new IOException("Could not read 4 bytes - bad sign. Read in only:" + n);
        }


        if ((bom[0] == Constants.UTF32BE_BOM[0]) && (bom[1] == Constants.UTF32BE_BOM[1]) &&
                (bom[2] == Constants.UTF32BE_BOM[2]) && (bom[3] == Constants.UTF32BE_BOM[3])) {
            encoding = Constants.ENC_UTF32BE;
            unread = n - 4;
        } else if ((bom[0] == Constants.UTF32LE_BOM[0]) && (bom[1] == Constants.UTF32LE_BOM[1]) &&
                (bom[2] == Constants.UTF32LE_BOM[2]) && (bom[3] == Constants.UTF32LE_BOM[3])) {
            encoding = Constants.ENC_UTF32LE;
            unread = n - 4;
        } else if ((bom[0] == Constants.UTF8_BOM[0]) && (bom[1] == Constants.UTF8_BOM[1]) &&
                (bom[2] == Constants.UTF8_BOM[2])) {
            encoding = Constants.ENC_UTF8;
            unread = n - 3;
        } else if ((bom[0] == Constants.UTF16BE_BOM[0]) && (bom[1] == Constants.UTF16BE_BOM[1])) {
            encoding = Constants.ENC_UTF16BE;
            unread = n - 2;
        } else if ((bom[0] == Constants.UTF16LE_BOM[0]) && (bom[1] == Constants.UTF16LE_BOM[1])) {
            encoding = Constants.ENC_UTF16LE;
            unread = n - 2;
        } else {
            unread = n;
        }
        in.unread(bom, (n - unread), unread);
        return encoding;
    }

    public static void writeUtf8Bom(OutputStream out) throws IOException {

        out.write(Constants.UTF8_BOM);
        out.flush();
    }

    public static void writeUtf16LEBom(OutputStream out) throws IOException {

        out.write(Constants.UTF16LE_BOM);
        out.flush();
    }

    public static void writeUtf16BEBom(OutputStream out) throws IOException {

        out.write(Constants.UTF16BE_BOM);
        out.flush();
    }

    public static void writeUtf32LEBom(OutputStream out) throws IOException {

        out.write(Constants.UTF32LE_BOM);
        out.flush();
    }

    public static void writeUtf32BEBom(OutputStream out) throws IOException {

        out.write(Constants.UTF32BE_BOM);
        out.flush();
    }

    public abstract SyslogMessage read(InputStream in) throws SyslogException;
}
