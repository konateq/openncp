package eu.europa.ec.sante.openncp.common.audit.auditmessagebuilders;

import com.google.common.io.Resources;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.audit.*;
import eu.europa.ec.sante.openncp.common.util.DateUtil;
import net.RFC3881.dicom.AuditMessage;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

public class XCAListAuditMessageBuilderTest extends XMLTestCase {


    @Test
    public void testBuild() throws Exception {
        {

            EventLog eventLog = new EventLog();
            eventLog.setEventType(EventType.PATIENT_SERVICE_LIST);
            eventLog.setNcpSide(NcpSide.NCP_A);
            eventLog.setEI_TransactionName(TransactionName.PATIENT_SERVICE_LIST);
            eventLog.setEI_EventActionCode(EventActionCode.EXECUTE);
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.FULL_SUCCESS);
            XMLGregorianCalendar now = DateUtil.getDateAsXMLGregorian(new Date());
            eventLog.setEI_EventDateTime(now);
            eventLog.setSC_UserID("ncp-ppt.pt.ehealth.testa.eu");
            eventLog.setSP_UserID("smp-test.publisher.ehealth.testa.eu");
            eventLog.setAS_AuditSourceId("PT-1");
            eventLog.setSourceip("10.200.137.161");
            eventLog.setTargetip("smp-cert-auth-test.publisher.ehealth.testa.eu");
            eventLog.setEventTargetParticipantObjectIds(Collections.singletonList("aHR0cHM6Ly9zbXAtdGVzdC5wdWJsaXNoZXIuZWhlYWx0aC50ZXN0YS5ldS9laGVhbHRoLXBhcnRpY2lwYW50aWQtcW5zJTNBJTNBdXJuJTNBZWhlYWx0aCUzQWF0JTNBbmNwLWlkcA=="));
            eventLog.setEM_ParticipantObjectDetail("errorMessage".getBytes());

            XCAListAuditMessageBuilder patientListAuditMessageBuilder = new XCAListAuditMessageBuilder();
            AuditMessage generatedAuditMessage = patientListAuditMessageBuilder.build(eventLog);

           URL url = Resources.getResource("patientservicelistauditmessage.xml");
           AuditMessage expectedAuditMessage = AuditTrailUtils.convertXMLToAuditObject(IOUtils.toInputStream(Resources.toString(url, StandardCharsets.UTF_8)));
           expectedAuditMessage.getEventIdentification().setEventDateTime(now);
           XMLUnit.setIgnoreWhitespace(true);
          assertXMLEqual(AuditTrailUtils.convertAuditObjectToXML(expectedAuditMessage), AuditTrailUtils.convertAuditObjectToXML(generatedAuditMessage));
        }

    }
}
