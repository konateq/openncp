package eu.europa.ec.sante.openncp.application.client.connector.assertions;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.soap.*;
import java.io.IOException;
import java.net.HttpURLConnection;

@Service
public class AssertionServiceImpl implements AssertionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionServiceImpl.class);


    @Override
    public Assertion request() throws STSClientException {
        try {
            LOGGER.info("TRC-STS client request Assertion");
            HttpURLConnection httpConnection = (HttpURLConnection) location.openConnection();
            //Set headers
            httpConnection.setRequestProperty("Content-Type", "application/soap+xml");
            httpConnection.setRequestProperty("SOAPAction", "");
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);

            LOGGER.info("Checking SSL Hostname Verifier: '{}'", CHECK_FOR_HOSTNAME);
            if (httpConnection instanceof HttpsURLConnection) {  // Going SSL
                ((HttpsURLConnection) httpConnection).setSSLSocketFactory(getSSLSocketFactory());
                if (StringUtils.equals(CHECK_FOR_HOSTNAME, "false"))
                    ((HttpsURLConnection) httpConnection).setHostnameVerifier(
                            (hostname, sslSession) -> true);
            }

            String value = System.getProperty("javax.net.ssl.key.alias");

            //  Write and send the SOAP message
            LOGGER.info("Sending SOAP request - Default Key Alias: '{}'", StringUtils.isNotBlank(value) ? value : "N/A");
            rstMsg.writeTo(httpConnection.getOutputStream());
            SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL)
                    .createMessage(new MimeHeaders(), httpConnection.getInputStream());

            LOGGER.info("Receiving SOAP response");
            if (response.getSOAPBody().hasFault()) {

                SOAPFault newFault = response.getSOAPBody().getFault();
                String code = newFault.getFaultCode();
                var string = newFault.getFaultString();
                throw new SOAPException("Code:" + code + ", Error String:" + string);
            }
            var assertionTRCA = extractTRCAssertionFromRSTC(response);
            LOGGER.info("TRC Assertion: '{}'", assertionTRCA != null ? assertionTRCA.getID() : "TRC Assertion is NULL");
            return assertionTRCA;

        } catch (SOAPException | IOException ex) {
            throw new STSClientException("SOAP Exception: " + ex.getMessage(), ex);
        } catch (UnsupportedOperationException ex) {
            throw new STSClientException("Unsupported Operation: " + ex.getMessage(), ex);
        }    }
}
