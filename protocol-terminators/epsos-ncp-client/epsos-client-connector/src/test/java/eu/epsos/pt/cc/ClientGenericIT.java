package eu.epsos.pt.cc;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import eu.epsos.protocolterminators.integrationtest.common.AbstractIT;
import eu.epsos.validation.datamodel.common.EpsosService;
import eu.epsos.validation.datamodel.common.NcpSide;
import eu.europa.ec.sante.ehdsi.gazelle.validation.reporting.ValidationReport;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.w3c.dom.Document;

import javax.naming.NamingException;
import javax.xml.soap.SOAPElement;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public abstract class ClientGenericIT extends AbstractIT {

    private static final String NCP_B = "/epsos-client-connector/services/ClientConnectorService";
    protected static EpsosService currentService;

    @BeforeClass
    public static void setUpClass() throws NamingException {
        try {
            Properties portalProps;
            portalProps = new Properties();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("portal.properties");
            portalProps.load(is);

            epr = portalProps.getProperty("ncpb.addr") + NCP_B;

            /* Add JDBC connection parameters to environment, instead of traditional JNDI */
            final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            final ComboPooledDataSource ds = new ComboPooledDataSource();
            try {
                ds.setDriverClass(portalProps.getProperty("db.driverclass"));
            } catch (PropertyVetoException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            ds.setJdbcUrl(portalProps.getProperty("db.jdbcurl"));
            ds.setUser(portalProps.getProperty("db.user"));
            ds.setPassword(portalProps.getProperty("db.password"));
            ds.setMaxPoolSize(1);
            ds.setMaxPoolSize(15);
            ds.setAcquireIncrement(3);
            ds.setMaxStatementsPerConnection(100);
            ds.setNumHelperThreads(20);
            builder.bind(portalProps.getProperty("db.resname"), ds);
            try {
                builder.activate();
            } catch (IllegalStateException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This method will retrieve a ISO format patient id from a given
     * AdHocQueryRequest message.
     *
     * @param queryRequestPath the path for the message XML file
     * @return the ISO format Patient ID
     */
    protected static String getPatientIdIso(String queryRequestPath) {
        String root = "";
        String extension = "";
        Document requestDoc = readDoc(queryRequestPath);
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            extension = xPath.evaluate("//patientId/extension", requestDoc);
            root = xPath.evaluate("//patientId/root", requestDoc);
        } catch (XPathExpressionException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
        return extension + "^^^&;" + root + "&;ISO";
    }

    /**
     * @param testName Test name for logging purposes
     * @param expected expected error code
     * @param request  file with PRPA_IN201305UV02
     */
    protected void testFailScenario(String testName, String expected, String request) {
        ValidationReport.cleanValidationDir(NcpSide.NCP_B);
        try {
            callService(request);  // call

            LOGGER.info(fail(testName));                                   // preaty status print to tests list
            Assert.fail(testName + "Unexpected success result; missing error exception"); // fail the test

        } catch (SOAPFaultException ex) {
            if (expected.equals((ex.getMessage()))) {        // is expected exception error?
                LOGGER.info(success(testName));

            } else {
                LOGGER.info(fail(testName));
            }

            Assert.assertEquals(testName, expected, ex.getMessage());

        } catch (RuntimeException ex) {
            LOGGER.info(fail(testName));                                   // pretty status print to tests list
            Assert.fail(testName + ": " + ex.getMessage()); // fail the test
        }
        ValidationReport.write(NcpSide.NCP_B, true);
    }

    @Override
    protected SOAPElement testGood(String testName, String request) {
        SOAPElement result;
        ValidationReport.cleanValidationDir(NcpSide.NCP_B);
        result = super.testGood(testName, request);
        ValidationReport.write(NcpSide.NCP_B, true);
        return result;
    }
}
