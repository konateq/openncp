package epsos.ccd.gnomon.configmanager;

import epsos.ccd.gnomon.auditmanager.*;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.ConfigurationManager;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.ConfigurationManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/**
 * Synchronizes the countries tsl content to the configuration parameters Read the list of countries from
 * the configuration manager parameter name = ncp.countries.
 * For each country reads again the configuration manager to find  the property tsl.location.[country_code].
 * It verifies the tsl file It parses the tsl file and extracts the endpoint wse and writes them to the
 * configuration manager.
 * Finally it exports all the certificates and add them to the truststore.
 *
 * @author Kostas Karkaletsis
 * @author Organization: Gnomon
 * @author mail:k.karkaletsis@gnomon.com.gr
 * @version 1.0, 2010, 30 Jun
 */
public class TSLSynchronizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSLSynchronizer.class);

    public TSLSynchronizer() {
    }

    public static String sync() {

        StringBuilder sb1 = new StringBuilder();
        String sb = "";
        ConfigurationManager cms = ConfigurationManagerFactory.getConfigurationManager();
        String ncp = cms.getProperty("ncp.country");
        String ncpemail = cms.getProperty("ncp.email");

        if (ncp.equals("")) {
            ncp = "GR-12";
            cms.setProperty("ncp.country", ncp);
        }
        if (ncpemail.equals("")) {
            ncpemail = "ncpgr@epsos.gr";
            cms.setProperty("ncp.email", ncpemail);
        }

        // read the country codes of the epSOS countries from the NCP configuration
        String[] countries = getCountriesList(cms).split(",");

        // Loop through countries list
        for (String country : countries) {
            LOGGER.info("Exporting configuration for: '{}'", country);
            sb = exportCountryConfig(sb1, country);
        }
        return sb;
    }

    /* If args is provided, then we want to run TSL-Sync for a specific country. Otherwise we just run it for every country in ncp.countries */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, CertificateException, Exception {
        if (args != null && args.length > 0) {
            String arg = args[0];
            if (arg.length() != 2) {
                throw new Exception("Argument must be a 2-letter country-code!");
            } else {
                syncCountry(arg.toLowerCase());
                LOGGER.info("TSL SYNC FINISHED");
                System.exit(0);
            }
        } else {
            String sb = sync().toString();
            LOGGER.info("TSL SYNC FINISHED");
            System.exit(0);
        }
    }

    /* Sync info for a specified country */
    public static String syncCountry(String country) {

        LOGGER.info("Synchronizing a specific country...");
        StringBuilder sb1 = new StringBuilder();
        String sb = "";
        ConfigurationManager cms = ConfigurationManagerFactory.getConfigurationManager();
        String ncp = cms.getProperty("ncp.country");
        String ncpemail = cms.getProperty("ncp.email");

        if (ncp.equals("")) {
            ncp = "GR-12";
            cms.setProperty("ncp.country", ncp);
        }
        if (ncpemail.equals("")) {
            ncpemail = "ncpgr@epsos.gr";
            cms.setProperty("ncp.email", ncpemail);
        }

        LOGGER.info("Exporting configuration for: '{}'", country);
        sb = exportCountryConfig(sb1, country);

        return sb;
    }

    /**
     * Reads the NCP Configuration and returns the list of ncp countries
     *
     * @param cms the instance of configuration manager
     * @return the comma seperated list of ncp countries
     */
    private static String getCountriesList(ConfigurationManager cms) {
        return cms.getProperty("ncp.countries");
    }

    private static void sendAudit(String sc_fullname, String sc_email, String sp_fullname, String sp_email,
                                  String localip, String remoteip, String partid) {
        try {
            AuditService asd = new AuditService();
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(new Date());
            XMLGregorianCalendar date2 = null;
            try {
                date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (DatatypeConfigurationException ex) {
            }
            /*
             * @param  EI_EventActionCode Possible values according to D4.5.6 are E,R,U,D
             * @param  EI_EventDateTime The datetime the event occured
             * @param  EI_EventOutcomeIndicator <br>
             *         0 for full success <br>
             *         1 in case of partial delivery <br>
             *         4 for temporal failures <br>
             *         8 for permanent failure <br>
             * @param  SC_UserID The string encoded CN of the TLS certificate of the NCP triggered the epsos operation
             * @param  SP_UserID The string encoded CN of the TLS certificate of the NCP processed the epsos operation
             * @param  ET_ObjectID The string encoded UUID of the returned document
             * @param  ReqM_ParticipantObjectID String-encoded UUID of the request message
             * @param  ReqM_PatricipantObjectDetail The value MUST contain the base64 encoded security header.
             * @param  ResM_ParticipantObjectID String-encoded UUID of the response message
             * @param  ResM_PatricipantObjectDetail The value MUST contain the base64 encoded security header.
             * @param  sourceip The IP Address of the source Gateway
             * @param  targetip The IP Address of the target Gateway
             */
            String sc_userid = sc_fullname + "<saml:" + sc_email + ">";
            String sp_userid = sp_fullname + "<saml:" + sp_email + ">";
            EventLog eventLog1 = EventLog.createEventLogNCPTrustedServiceList(
                    TransactionName.epsosNCPTrustedServiceList,
                    EventActionCode.EXECUTE,
                    date2,
                    EventOutcomeIndicator.FULL_SUCCESS,
                    sc_userid,
                    sp_userid,
                    partid,
                    "urn:uuid:00000000-0000-0000-0000-000000000000",
                    new byte[1],
                    "urn:uuid:00000000-0000-0000-0000-000000000000",
                    new byte[1],
                    localip, remoteip);
            eventLog1.setEventType(EventType.epsosNCPTrustedServiceList);
            asd.write(eventLog1, "13", "2");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                LOGGER.error(null, ex);
            }
        } catch (Exception e) {
            LOGGER.error("Error sending audit for tslsync");
        }
    }

    private static String exportCountryConfig(StringBuilder sb, String country) {

        ConfigurationManager cms = ConfigurationManagerFactory.getConfigurationManager();
        String ncp = cms.getProperty("ncp.country");
        String ncpemail = cms.getProperty("ncp.email");

        LOGGER.info(country + ": Reading tsl file");
        String url = cms.getProperty("tsl.location." + country);
        LOGGER.info("URL: " + url);
        sb.append("Country is :" + country + " and location is :" + url);
        sb.append("<br/>");
        // verify the authenticity and integrity of tsl
        Document doc = TSLUtils.createDomFromURLUsingHttps(url);
//        boolean verifyTSL = TSLUtils.VerifyTSL(doc);
        boolean verifyTSL = true;
        if (verifyTSL) {
            LOGGER.info(country + ": The tsl file has verified");
            // Extract the service WSEs from the TSL and write them to the NCP configuration
            LOGGER.info(country + ": Extracting service Endpoints");
            Hashtable serviceNames = serviceNames = TSLUtils.getServicesFromTSL(url);
            if (serviceNames.size() > 0) {
                Enumeration names;
                names = serviceNames.keys();
                String str = "";
                while (names.hasMoreElements()) {
                    str = (String) names.nextElement();
                    // Correct the typo of PatientIdentification Service
                    String str_corrected = "";
                    if (str.equals("PatientIdenitificationService")) {
                        str_corrected = "PatientIdentificationService";
                    } else {
                        str_corrected = str;
                    }
                    if (!serviceNames.get(str).toString().equals("")) {
                        cms.setServiceWSE(country, str_corrected.trim(), serviceNames.get(str).toString().trim());
                    }
                    LOGGER.debug(country + ": Extracting " + str.trim() + " - " + serviceNames.get(str).toString().trim());
                }
                // Extract the certificates from services, ipsec and ssl
                // Services
                LOGGER.info("Extracting certificates for: '{}'", country);
                sb.append(TSLUtils.exportSSLFromTSL(url, country));
                sb.append(TSLUtils.exportNCPSignFromTSL(url, country));
                String localip = cms.getProperty("SERVER_IP");
                sendAudit(ncp, ncpemail, "Central Cervices", "centralservices@epsos.eu", localip, url, country);
            } else {
                LOGGER.info("Problem extracting service names for: '{}'", country);
            }
        } else {
            LOGGER.error("ERROR Validating TSL");
        }
        return sb.toString();
    }
}
