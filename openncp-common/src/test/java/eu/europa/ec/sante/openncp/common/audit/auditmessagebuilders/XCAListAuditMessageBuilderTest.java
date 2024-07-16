package eu.europa.ec.sante.openncp.common.audit.auditmessagebuilders;

import com.google.common.io.Resources;
import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.audit.*;
import eu.europa.ec.sante.openncp.common.util.DateUtil;
import net.RFC3881.dicom.AuditMessage;
import net.sf.saxon.value.GDayValue;
import net.sf.saxon.value.SaxonXMLGregorianCalendar;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class XCAListAuditMessageBuilderTest extends XMLTestCase {


    @Test
    public void testBuild() throws Exception {
        {


            EventLog eventLog = new EventLog();
            eventLog.setAS_AuditSourceId("42");
            eventLog.setEI_EventActionCode(EventActionCode.CREATE);
            SaxonXMLGregorianCalendar EI_EventDateTime = new SaxonXMLGregorianCalendar(new GDayValue((byte) 'A', 1));
            eventLog.setEI_EventDateTime(EI_EventDateTime);
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.FULL_SUCCESS);
            eventLog.setEI_TransactionName(TransactionName.IDENTIFICATION_SERVICE_FIND_IDENTITY_BY_TRAITS);
            eventLog.setEI_TransactionNumber("42");
            eventLog.setEM_ParticipantObjectDetail("AXAXAXAX".getBytes("UTF-8"));
            eventLog.setEM_ParticipantObjectID("EM Participant Object ID");
            eventLog.setEventTargetAdditionalObjectId("42");
            eventLog.setEventTargetParticipantObjectIds(new ArrayList<>());
            eventLog.setEventType(EventType.IDENTIFICATION_SERVICE_FIND_IDENTITY_BY_TRAITS);
            eventLog.setHR_AlternativeUserID("HR Alternative User ID");
            eventLog.setHR_RoleID("HR Role ID");
            eventLog.setHR_UserID("HR User ID");
            eventLog.setHR_UserName("janedoe");
            eventLog.setHciIdentifier("42");
            eventLog.setMS_UserID("MS User ID");
            eventLog.setNcpSide(NcpSide.NCP_A);
            eventLog.setPC_RoleID("PC Role ID");
            eventLog.setPC_UserID("PC User ID");
            eventLog.setPS_ParticipantObjectID("PS Participant Object ID");
            eventLog.setPT_ParticipantObjectID("PT Participant Object ID");
            eventLog.setQueryByParameter("Query By Parameter");
            eventLog.setReqM_ParticipantObjectDetail("AXAXAXAX".getBytes("UTF-8"));
            eventLog.setReqM_ParticipantObjectID("Req M Participant Object ID");
            eventLog.setResM_ParticipantObjectDetail("AXAXAXAX".getBytes("UTF-8"));
            eventLog.setResM_ParticipantObjectID("Res M Participant Object ID");
            eventLog.setSC_UserID("SC User ID");
            eventLog.setSP_UserID("SP User ID");
            eventLog.setSourceip("127.0.0.1");
            eventLog.setTargetip("127.0.0.1");

            //EventLog eventLog = new EventLog();
            eventLog.setEventType(EventType.PATIENT_SERVICE_LIST);
            eventLog.setNcpSide(NcpSide.NCP_A);
            eventLog.setEI_TransactionName(TransactionName.PATIENT_SERVICE_LIST);
            eventLog.setEI_EventActionCode(EventActionCode.EXECUTE);
            eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.FULL_SUCCESS);
            XMLGregorianCalendar now = DateUtil.getDateAsXMLGregorian(new Date());
            eventLog.setEI_EventDateTime(now);
            eventLog.setSC_UserID("Service Consumer");
            eventLog.setSP_UserID("Service Provider");
            eventLog.setEventTargetParticipantObjectIds(Collections.singletonList("aHR0cHM6Ly9zbXAtdGVzdC5wdWJsaXNoZXIuZWhlYWx0aC50ZXN0YS5ldS9laGVhbHRoLXBhcnRpY2lwYW50aWQtcW5zJTNBJTNBdXJuJTNBZWhlYWx0aCUzQWF0JTNBbmNwLWlkcA=="));
            eventLog.setEM_ParticipantObjectDetail("errorMessage".getBytes());
            eventLog.setAS_AuditSourceId("42");
            eventLog.setEI_TransactionNumber("42");
            eventLog.setEventTargetAdditionalObjectId("42");
            eventLog.setHR_AlternativeUserID("HR Alternative User ID");
            eventLog.setHR_RoleID("HR Role ID");
            eventLog.setHR_UserID("HR User ID");
            eventLog.setHR_UserName("janedoe");
            eventLog.setHciIdentifier("42");
            eventLog.setMS_UserID("MS User ID");
            eventLog.setPC_RoleID("PC Role ID");
            eventLog.setPC_UserID("PC User ID");
            eventLog.setPS_ParticipantObjectID("PS Participant Object ID");
            eventLog.setPT_ParticipantObjectID("PT Participant Object ID");
            eventLog.setQueryByParameter("Query By Parameter");
            eventLog.setReqM_ParticipantObjectDetail("AXAXAXAX".getBytes("UTF-8"));
            eventLog.setReqM_ParticipantObjectID("Req M Participant Object ID");

            eventLog.setSourceip("127.0.0.1");
            eventLog.setTargetip("127.0.0.1");


            XCAListAuditMessageBuilder patientListAuditMessageBuilder = new XCAListAuditMessageBuilder();
            AuditMessage generatedAuditMessage = patientListAuditMessageBuilder.build(eventLog);
            System.out.println(AuditTrailUtils.convertAuditObjectToXML(generatedAuditMessage));

           URL url = Resources.getResource("patientservicelistauditmessage.xml");
           AuditMessage expectedAuditMessage = AuditTrailUtils.convertXMLToAuditObject(IOUtils.toInputStream(Resources.toString(url, StandardCharsets.UTF_8)));
           expectedAuditMessage.getEventIdentification().setEventDateTime(now);
           XMLUnit.setIgnoreWhitespace(true);
          assertXMLEqual(AuditTrailUtils.convertAuditObjectToXML(expectedAuditMessage), AuditTrailUtils.convertAuditObjectToXML(generatedAuditMessage));
        }

    }
}
