package tr.com.srdc.epsos.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.epsos.util.Constants;

import java.net.*;
import java.util.Enumeration;

public class IPUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(IPUtil.class);
    @SuppressWarnings("squid:S1313")
    private static final String LOCAL_IP_ADDRESS = "127.0.0.1";
    private static final String ERROR_UNKNOWN_HOST = "UNKNOWN_HOST";

    private IPUtil() {
    }

    /**
     * This returns the SERVER_IP value from the OpenNCP property database.
     *
     * @return IP Address of the server defined into the OpenNCP Configuration Database.
     */
    public static String getStaticServerIP() {
        return Constants.SERVER_IP;
    }

    /**
     * Returns Local IP address of the machine executing the method.
     *
     * @return IP address of the server or "UNKNOWN_HOST" if an IP cannot be retrieved.
     */
    public static String getPrivateServerIp() {

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLinkLocalAddress() && !inetAddress.isLoopbackAddress()
                            && (inetAddress instanceof Inet4Address || inetAddress instanceof Inet6Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.error("Unable to get current IP: '{}'", e.getMessage(), e);
        }
        return ERROR_UNKNOWN_HOST;
    }
}
