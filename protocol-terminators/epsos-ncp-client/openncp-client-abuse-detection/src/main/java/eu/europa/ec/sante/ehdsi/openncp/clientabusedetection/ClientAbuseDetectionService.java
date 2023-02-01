package eu.europa.ec.sante.ehdsi.openncp.clientabusedetection;

import com.ibatis.common.jdbc.ScriptRunner;
import epsos.ccd.gnomon.auditmanager.AuditTrailUtils;
import epsos.ccd.gnomon.auditmanager.EventType;
import epsos.ccd.gnomon.auditmanager.IHEEventType;
import eu.europa.ec.sante.ehdsi.constant.ClassCode;
import eu.europa.ec.sante.ehdsi.openncp.abusedetection.*;
import net.RFC3881.ActiveParticipantType;
import net.RFC3881.AuditMessage;
import net.RFC3881.CodedValueType;
import net.RFC3881.ParticipantObjectIdentificationType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.epsos.util.Constants;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClientAbuseDetectionService implements Job {
    public static final String DESCRIPTION_ALL = "[NCP-B] Detected %d transactions within an interval of %d seconds. This is exceeding the indicated threshold of %d transactions for the defind time interval";
    public static final String DESCRIPTION_POC = "[NCP-B] Detected %d transactions within an interval of %d seconds from a specific Point of care. This is exceeding the indicated threshold of %d transactions for the defined interval";
    public static final String DESCRIPTION_PAT = "[NCP-B] Detected %d transactions within an interval of %d seconds for a specific Patient. This is exceeding the indicated threshold of %d transactions for the defined interval";
    private static final int ANOMALY_DESCRIPTION_SIZE = 2000;
    private static final int ANOMALY_TYPE_SIZE = 20;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String JDBC_OPEN_ATNA = "jdbc/OPEN_ATNA";
    private static final String JDBC_EHNCP_PROPERTY = "jdbc/ConfMgr";
    private static List<AbuseEvent> abuseList = new ArrayList<>();
    private static long lastIdAnalyzed = -1;
    private final Logger logger = LoggerFactory.getLogger(ClientAbuseDetectionService.class);

    public ClientAbuseDetectionService() {
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        logger.info("AbuseDetectionService Job is running......");
        var scheduler = jobExecutionContext.getScheduler();

        try {
            logger.info("AbuseDetectionService Job paused");
            scheduler.pauseJob(jobExecutionContext.getJobDetail().getKey());

            //Long res = (Long)jobExecutionContext.getMergedJobDataMap().get("myKey");

            try {
                String query;
                if (lastIdAnalyzed < 0) { // If no lastId is available it starts to analyze records from n days back
                    long lastFileTimeAnalyzed = LocalDateTime.now().minusDays(3).toDate().toInstant().toEpochMilli();
                    LocalDateTime dt = new LocalDateTime(lastFileTimeAnalyzed, DateTimeZone.forTimeZone(TimeZone.getDefault()));
                    DateTimeFormatter dtf = DateTimeFormat.forPattern(ClientAbuseDetectionService.PATTERN);
                    String lastDateTimeFileAnalyzed = dt.toString(dtf);
                    query = "select messages.id, eventActionCode, eventDateTime, eventOutcome, messageContent, sourceAddress, " +
                            "eventId_id, code from messages inner join codes on (messages.eventId_id = codes.id) " +
                            "where codes.code IN ('ITI-55', 'ITI-38', 'ITI-39', 'ITI-41')" +
                            "and eventDateTime > '" + lastDateTimeFileAnalyzed + "' order by " +
                            "eventDateTime ASC;";
                } else { // fetch only new records to be analyzed
                    query = "select messages.id, eventActionCode, eventDateTime, eventOutcome, messageContent, sourceAddress, " +
                            "eventId_id, code from messages inner join codes on (messages.eventId_id = codes.id) " +
                            "where codes.code IN ('ITI-55', 'ITI-38', 'ITI-39', 'ITI-41')" +
                            "and messages.id > " + lastIdAnalyzed + " order by id ASC;";
                }

                List<MessagesRecord> records = retrieveAuditEvents(query);
                if (!records.isEmpty()) {
                    records.forEach(p -> {
                        try {
                            // Read the audit message and if valid stores the corresponding id
                            AuditMessage au = readAuditString(p);
                            if (au != null) {
                                lastIdAnalyzed = p.getId();
                            }
                        } catch (JAXBException e) {
                            throw new AbuseDetectionException(e);
                        }
                    });
                    // Check for anomalies in the actual set of Audits and purge from the set those outdated
                    abuseList = checkAnomalies(abuseList);
                    logger.info("AbuseDetectionService: end of checking data");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SchedulerException e) {
            throw new AbuseDetectionException(e);
        } finally {
            try {
                scheduler.resumeJob(jobExecutionContext.getJobDetail().getKey());
                logger.info("AbuseDetectionService Job resumed");
            } catch (SchedulerException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    private Connection dbConnect(String dsName) throws NamingException, SQLException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource ds = (DataSource) envContext.lookup(dsName);
        return ds.getConnection();
    }

    private List<MessagesRecord> retrieveAuditEvents(String sqlSelect) throws Exception {

        List<MessagesRecord> listXmlRecords = new ArrayList<>();

        try (Connection sqlConnection = dbConnect(ClientAbuseDetectionService.JDBC_OPEN_ATNA);
             Statement stmt = sqlConnection.createStatement()) {

            ResultSet rs = stmt.executeQuery(sqlSelect);
            while (rs.next()) {
                MessagesRecord record = new MessagesRecord();
                record.setId(rs.getLong("id"));
                record.setXml(rs.getString("messageContent"));
                record.setEventDateTime(rs.getTimestamp("eventDateTime").toLocalDateTime());

                if (record.getXml().startsWith("<?xml")) {
                    listXmlRecords.add(record);
                }
            }
        } catch (Exception exception) {
            throw new Exception("The following error occurred during an SQL operation:", exception);
        }
        return listXmlRecords;
    }

    private void runSqlScript(String sqlScript) throws Exception {

        try (Connection sqlConnection = dbConnect(ClientAbuseDetectionService.JDBC_EHNCP_PROPERTY);
             StringReader stringReader = new StringReader(sqlScript)) {

            ScriptRunner objScriptRunner = new ScriptRunner(sqlConnection, false, true);
            objScriptRunner.setLogWriter(null);
            objScriptRunner.setErrorLogWriter(null);
            objScriptRunner.runScript(stringReader);

        } catch (Exception exception) {
            throw new Exception("The following error occurred during an SQL operation:", exception);
        }
    }

    private void setAbuseErrorEvent(AbuseType abuseType, String description, int numRequests, AbuseEvent eventBegin, AbuseEvent eventEnd) {
        String eventDescription = description.substring(0, Math.min(description.length(), ANOMALY_DESCRIPTION_SIZE));

        String type = abuseType.getType();
        type = type.substring(0, Math.min(type.length(), ANOMALY_TYPE_SIZE));

        LocalDateTime now = LocalDateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault())));
        String datetime = now.toString(PATTERN);
        String eventStartDate = eventBegin.getRequestDateTime().toString(PATTERN);
        String eventEndDate = eventEnd.getRequestDateTime().toString(PATTERN);

        if (anomalyNotPresent(eventDescription, type, eventStartDate, eventEndDate)) {
            String sqlInsertStatementError = "INSERT INTO EHNCP_ANOMALY( " +
                    "DESCRIPTION, " +
                    "TYPE, " +
                    "EVENT_DATE, " +
                    "EVENT_START_DATE, " +
                    "EVENT_END_DATE)" +
                    "VALUES (" +
                    "'" + StringUtils.replace(eventDescription, "'", "''") + "',\n" +
                    "'" + StringUtils.replace(type, "'", "''") + "',\n" +
                    "'" + datetime + "',\n" +
                    "'" + eventStartDate + "',\n" +
                    "'" + eventEndDate + "'" +
                    ");";
            try {
                this.runSqlScript(sqlInsertStatementError);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.info("Anomaly already persisted. Skipping.");
        }
    }

    private boolean anomalyNotPresent(String description, String type, String eventStartDate, String eventEndDate) {

        String sqlSelect = "SELECT id FROM EHNCP_ANOMALY WHERE " +
                "DESCRIPTION = '" + StringUtils.replace(description, "'", "''") + "' AND " +
                "TYPE = '" + StringUtils.replace(type, "'", "''") + "' AND " +
                "EVENT_START_DATE = '" + eventStartDate + "' AND " +
                "EVENT_END_DATE = '" + eventEndDate + "';";

        int recordCount = 0;
        try (Connection sqlConnection = dbConnect(ClientAbuseDetectionService.JDBC_EHNCP_PROPERTY);
             Statement stmt = sqlConnection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sqlSelect);
            while (rs.next()) {
                recordCount++;
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return recordCount == 0;
    }

    /*
    private void setAbuseErrorEventOld(AbuseEvent abuseEvent) {
        EventLog eventLog = new EventLog();

        eventLog.setEI_EventActionCode(EventActionCode.CREATE);
        eventLog.setEI_EventDateTime(abuseEvent.audit.getEventIdentification().getEventDateTime());
        eventLog.setEI_EventOutcomeIndicator(EventOutcomeIndicator.TEMPORAL_FAILURE);

        eventLog.setAS_AuditSourceId(abuseEvent.audit.getAuditSourceIdentification().get(0).getAuditSourceID());

        Optional<ParticipantObjectIdentificationType> req = abuseEvent.audit.getParticipantObjectIdentification()
                .stream().filter(p -> StringUtils.equals(p.getParticipantObjectIDTypeCode().getCode(), "req")).findFirst();
        Optional<ParticipantObjectIdentificationType> rsp = abuseEvent.audit.getParticipantObjectIdentification()
                .stream().filter(p -> StringUtils.equals(p.getParticipantObjectIDTypeCode().getCode(), "rsp")).findFirst();

        eventLog.setReqM_ParticipantObjectID(req.get().getParticipantObjectID()); // getMessageID(msgContext.getEnvelope())
        eventLog.setReqM_ParticipantObjectDetail(req.get().getParticipantObjectDetail().get(0).getValue()); // msgContext.getEnvelope().getHeader().toString()".getBytes());

        Optional<AuditMessage.ActiveParticipant> cons = abuseEvent.audit.getActiveParticipant()
                .stream().filter(p -> StringUtils.equals(p.getRoleIDCode().get(0).getCode(), "ServiceConsumer")).findFirst();
        Optional<AuditMessage.ActiveParticipant> prov = abuseEvent.audit.getActiveParticipant()
                .stream().filter(p -> StringUtils.equals(p.getRoleIDCode().get(0).getCode(), "ServiceProvider")).findFirst();

        Optional<AuditMessage.ActiveParticipant> prov2 = abuseEvent.audit.getActiveParticipant()
                .stream().filter(p -> StringUtils.equals(p.getRoleIDCode().get(0).getCode(), "ServiceProvider")).findFirst();

        eventLog.setEI_TransactionName(TransactionName.ANOMALY_DETECTED);

        eventLog.setSC_UserID(cons.get().getUserID()); //clientCommonName
        eventLog.setSP_UserID(prov.get().getUserID()); //clientCommonName
        eventLog.setHR_UserID(cons.get().getUserID());
        for(ActiveParticipantType a : abuseEvent.audit.getActiveParticipant()) {
            if(XSPAFunctionalRole.containsLabel(a.getRoleIDCode().get(0).getCode())) {
                eventLog.setHR_UserID(a.getUserID());
                eventLog.setHR_RoleID(a.getRoleIDCode().get(0).getCode());
                eventLog.setHR_UserName(a.getUserName());
                eventLog.setHR_AlternativeUserID(a.getAlternativeUserID());
                break;
            }
        }

        eventLog.setSourceip(cons.get().getNetworkAccessPointID());
        eventLog.setTargetip(prov.get().getNetworkAccessPointID());

        eventLog.setResM_ParticipantObjectID(rsp.get().getParticipantObjectID()); // getMessageID(msgContext.getEnvelope())
        eventLog.setResM_ParticipantObjectDetail(rsp.get().getParticipantObjectDetail().get(0).getValue()); // msgContext.getEnvelope().getHeader().toString()".getBytes());

        eventLog.setNcpSide(NcpSide.NCP_A);
        eventLog.setEventType(EventType.ANOMALY_DETECTED);

        AuditService auditService = AuditServiceFactory.getInstance();
        auditService.write(eventLog, "", "1");
        logger.info("EventLog: '{}' generated.", eventLog.getEventType());
    }
    */

    private AuditMessage readAuditString(MessagesRecord rec) throws JAXBException {

        try {
            if (StringUtils.contains(rec.getXml(), "AuditMessage")) {
                AuditMessage au = AuditTrailUtils.convertXMLToAuditObject(new ByteArrayInputStream(rec.getXml().getBytes(StandardCharsets.UTF_8)));

                LocalDateTime dt = new LocalDateTime(au.getEventIdentification().getEventDateTime()
                        .toGregorianCalendar()
                        .toZonedDateTime()
                        .toLocalDateTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                        DateTimeZone.forTimeZone(TimeZone.getDefault()));

//                LocalDateTime now = new LocalDateTime(DateTimeZone.forTimeZone(TimeZone.getDefault()));
//                int val = Math.max(3600, Integer.parseInt(Constants.ABUSE_ALL_REQUEST_REFERENCE_REQUEST_PERIOD));
//                Period diff = new Period(dt, now);
//                if (diff.toStandardSeconds().getSeconds() > val) {
//                    return null; // do not process file
//                }

                boolean evtPresent = false;
                AbuseTransactionType transactionType = AbuseTransactionType.TRANSACTION_UNKNOWN;
                if (StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.IDENTIFICATION_SERVICE_FIND_IDENTITY_BY_TRAITS.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.IDENTIFICATION_SERVICE_FIND_IDENTITY_BY_TRAITS.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XCPD_SERVICE_REQUEST;
                }
                if (!evtPresent && StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.PATIENT_SERVICE_LIST.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.PATIENT_SERVICE_LIST.getCode())) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        ClassCode.PS_CLASSCODE.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XCA_SERVICE_REQUEST;
                }
                if (!evtPresent && StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.PATIENT_SERVICE_RETRIEVE.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.PATIENT_SERVICE_RETRIEVE.getCode())) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        ClassCode.PS_CLASSCODE.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XCA_SERVICE_REQUEST;
                }
                if (!evtPresent && StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.PATIENT_SERVICE_LIST.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.ORDER_SERVICE_LIST.getCode())) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        ClassCode.EP_CLASSCODE.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XCA_SERVICE_REQUEST;
                }
                if (!evtPresent && StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.PATIENT_SERVICE_RETRIEVE.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.ORDER_SERVICE_RETRIEVE.getCode())) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        ClassCode.EP_CLASSCODE.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XCA_SERVICE_REQUEST;
                }
                if (!evtPresent && StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.DISPENSATION_SERVICE_INITIALIZE.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.DISPENSATION_SERVICE_DISCARD.getCode())) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        ClassCode.EDD_CLASSCODE.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XDR_SERVICE_REQUEST;
                }
                if (!evtPresent && StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.ORCD_SERVICE_LIST.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.ORCD_SERVICE_LIST.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XCA_SERVICE_REQUEST;
                }
                if (!evtPresent && StringUtils.equals(au.getEventIdentification().getEventID().getCode(),
                        IHEEventType.ORCD_SERVICE_RETRIEVE.getCode()) &&
                        au.getEventIdentification().getEventTypeCode()
                                .stream()
                                .anyMatch(c -> StringUtils.equals(c.getCode(),
                                        EventType.ORCD_SERVICE_RETRIEVE.getCode()))) {
                    evtPresent = true;
                    transactionType = AbuseTransactionType.XCA_SERVICE_REQUEST;
                }

                if (evtPresent) {
                    logger.info("Audit found: event time ['{}'}'] event id code ['{}'}'] event id display name ['{}'}'] " +
                                    "event id code system name ['{}'}'] event id codes ['{}'] active participants ['{}'}'] ",
                            dt, au.getEventIdentification().getEventID().getCode(), au.getEventIdentification().getEventID().getDisplayName(),
                            au.getEventIdentification().getEventID().getCodeSystemName(),
                            getTypeCodes(au.getEventIdentification().getEventTypeCode()), getActiveParticipants(au.getActiveParticipant()));

                    String joinedPoc = au.getActiveParticipant().stream()
                            .filter(ActiveParticipantType::isUserIsRequestor)
                            .map(ActiveParticipantType::getUserID)
                            .collect(Collectors.joining("-"));

                    String simplePoc = au.getActiveParticipant().stream()
                            .filter(auid -> auid.getAlternativeUserID() != null)
                            .filter(ActiveParticipantType::isUserIsRequestor)
                            .map(ActiveParticipantType::getUserID)
                            .collect(Collectors.joining());

                    String participant = au.getParticipantObjectIdentification().stream()
                            .filter(a -> a.getParticipantObjectTypeCode() == 1 && a.getParticipantObjectTypeCodeRole() == 1)
                            .map(ParticipantObjectIdentificationType::getParticipantObjectID)
                            .collect(Collectors.joining());

                    abuseList.add(
                            new AbuseEvent(au.getEventIdentification().getEventID(),
                                    simplePoc,
                                    participant,
                                    dt,
                                    rec.getId().toString(),
                                    transactionType,
                                    au)
                    );
                }
                return au;
            }
        } catch (Exception e) {
            throw new AbuseDetectionException(e);
        }

        return null;
    }

    private int getElapsedTimeBetweenEvents(List<AbuseEvent> listEvt, int beg, int end) {
        if (beg < 0 || end < 0) {
            return 0;
        }
        if (beg >= listEvt.size() || end >= listEvt.size()) {
            return 0;
        }
        LocalDateTime t1 = listEvt.get(beg).getRequestDateTime();
        LocalDateTime t2 = listEvt.get(end).getRequestDateTime();
        Period diff = new Period(t1, t2); // time elapsed between first and last request
        return diff.toStandardSeconds().getSeconds();
    }

    private List<AbuseEvent> checkAnomalies(List<AbuseEvent> list) {

        int areqr = Integer.parseInt(Constants.ABUSE_ALL_REQUEST_REFERENCE_REQUEST_PERIOD);
        // No Patient discovery on client only on server
        int upatr = 0; //Integer.parseInt(Constants.ABUSE_UNIQUE_PATIENT_REFERENCE_REQUEST_PERIOD);
        int upocr = Integer.parseInt(Constants.ABUSE_UNIQUE_POC_REFERENCE_REQUEST_PERIOD);

        int areqThreshold = Integer.parseInt(Constants.ABUSE_ALL_REQUEST_THRESHOLD);
        int upatThreshold = Integer.parseInt(Constants.ABUSE_UNIQUE_PATIENT_REQUEST_THRESHOLD);
        int upocThreshold = Integer.parseInt(Constants.ABUSE_UNIQUE_POC_REQUEST_THRESHOLD);

        if (areqr <= 0 && upatr <= 0 && upocr <= 0) { // no check
            return list;
        }

        List<AbuseEvent> sortedAllList = list.stream()
                .sorted(Comparator.comparing(AbuseEvent::getRequestDateTime))
                .collect(Collectors.toList());
        if (areqr > 0 && sortedAllList.size() > areqThreshold) { // Analyze ALL requests
            //            for (int i = 0; i < sortedAllList.size(); i++) {
            //                int begin;
            //                int end;
            //                begin = i;
            //                end = begin + Math.min(begin + areq_threshold, sortedAllList.size() - 1 - begin);
            //                LocalDateTime t1 = sortedAllList.get(begin).getRequestDateTime();
            //                LocalDateTime t2 = sortedAllList.get(end).getRequestDateTime();
            //                Period diff = new Period(t1, t2); // time elapsed between first and last request
            //                if (diff.toStandardSeconds().getSeconds() < areqr) { // we are inside the interval for detecting
            //                    int totreq = end - begin + 1;
            //                    if (totreq > areq_threshold) {
            //                        logger.error("WARNING_SEC_UNEXPECTED_NUMBER_OF_REQUESTS : [Total requests: '{}' exceeding " +
            //                                        "threshold of: '{}' requests inside an interval of '{}' seconds] - begin event : ['{}'] end event: ['{}']",
            //                                totreq, areq_threshold, diff.toStandardSeconds().getSeconds(), sortedAllList.get(begin), sortedAllList.get(end));
            //                        String abuseDescription = String.format(DESCRIPTION_ALL, totreq, diff.toStandardSeconds().getSeconds(), areq_threshold);
            //                        setAbuseErrorEvent(AbuseType.ALL, abuseDescription, totreq, sortedAllList.get(begin), sortedAllList.get(end));
            //                    }
            //                }
            //            }
            int index = 0;
            int lastValidIndex = 0;
            do {
                int tot = 0;
                int beg = 0;
                int end = 0;
                for (index = lastValidIndex; index < sortedAllList.size(); index++) {
                    beg = lastValidIndex;
                    end = index;
                    int elapsed = getElapsedTimeBetweenEvents(sortedAllList, beg, end);
                    if (elapsed > areqr) {
                        lastValidIndex = index;
                        break;
                    }
                    tot++;
                }
                if (tot > areqThreshold) {
                    if (lastValidIndex > 0 && index < sortedAllList.size()) {
                        end = lastValidIndex - 1;
                    } else {
                        end = sortedAllList.size() - 1;
                    }
                    int elapsed = getElapsedTimeBetweenEvents(sortedAllList, beg, end);
                    if (elapsed < areqr) {
                        logger.error("WARNING_SEC_UNEXPECTED_NUMBER_OF_REQUESTS : [Total requests: '{}' exceeding " +
                                        "threshold of: '{}' requests inside an interval of '{}' seconds] - begin event : ['{}'] end event: ['{}']",
                                tot, areqThreshold, elapsed, sortedAllList.get(beg), sortedAllList.get(end));
                        String abuseDescription = String.format(DESCRIPTION_ALL, tot, elapsed, areqThreshold);
                        setAbuseErrorEvent(AbuseType.ALL, abuseDescription, tot, sortedAllList.get(beg), sortedAllList.get(end));
                    }
                }
            } while (index < sortedAllList.size() && lastValidIndex < sortedAllList.size());
        }

        //////////////////////////////////////////////////////////////////////

        List<AbuseEvent> distinctPointOfCareIds = list.stream()
                .filter(distinctByKey(AbuseEvent::getPointOfCare))
                .collect(Collectors.toList());
        if (upocr > 0 && sortedAllList.size() > upocThreshold) { // analyze unique POC requests
            if (!distinctPointOfCareIds.isEmpty()) {
                distinctPointOfCareIds.forEach(poc -> {
                    List<AbuseEvent> sortedPocList = list.stream()
                            .filter(p -> p.getPointOfCare().equals(poc.getPointOfCare()))
                            .sorted(Comparator.comparing(AbuseEvent::getPointOfCare))
                            .sorted(Comparator.comparing(AbuseEvent::getRequestDateTime))
                            .collect(Collectors.toList());

                    Period diff = Period.ZERO;
                    int index = 0;
                    int lastValidIndex = 0;
                    do {
                        int tot = 0;
                        int beg = 0;
                        int end = 0;
                        for (index = lastValidIndex; index < sortedPocList.size(); index++) {
                            beg = lastValidIndex;
                            end = index;
                            int elapsed = getElapsedTimeBetweenEvents(sortedPocList, beg, end);
                            if (elapsed > upocr) {
                                lastValidIndex = index;
                                break;
                            }
                            tot++;
                        }
                        if (tot > upocThreshold) {
                            if (lastValidIndex > 0 && index < sortedPocList.size()) {
                                end = lastValidIndex - 1;
                            } else {
                                end = sortedPocList.size() - 1;
                            }
                            int elapsed = getElapsedTimeBetweenEvents(sortedPocList, beg, end);
                            if (elapsed < upocr) {
                                logger.error("WARNING_SEC_UNEXPECTED_NUMBER_OF_REQUESTS_FOR_UNIQUE_POINT_OF_CARE : " +
                                                "[Total requests: '{}' exceeding threshold of: '{}' requests inside an interval " +
                                                "of '{}' seconds] - begin event : ['{}'] end event : ['{}']",
                                        tot, upocThreshold, elapsed,
                                        sortedPocList.get(beg), sortedPocList.get(end));
                                String abuseDescription = String.format(DESCRIPTION_POC, tot, elapsed, upocThreshold);
                                setAbuseErrorEvent(AbuseType.POC, abuseDescription, tot, sortedPocList.get(beg), sortedPocList.get(end));
                            }
                        }
                    } while (index < sortedPocList.size() && lastValidIndex < sortedPocList.size());
                });
            }
        }

          // No Patient discovery on client only on server
//        List<AbuseEvent> distinctPatientIds = list.stream()
//                .filter(distinctByKey(AbuseEvent::getPatientId))
//                .collect(Collectors.toList());
//        if (upatr > 0 && sortedAllList.size() > upatThreshold) { // Analyze unique Patient requests
//            if (!distinctPatientIds.isEmpty()) {
//                distinctPatientIds.forEach(pat -> {
//                    List<AbuseEvent> sortedXcpdList = list.stream()
//                            .filter(p -> p.getTransactionType().equals(AbuseTransactionType.XCPD_SERVICE_REQUEST))
//                            .filter(p -> p.getPatientId().equals(pat.getPatientId()))
//                            .sorted(Comparator.comparing(AbuseEvent::getPatientId))
//                            .sorted(Comparator.comparing(AbuseEvent::getRequestDateTime))
//                            .collect(Collectors.toList());
//
//                    Period diff = Period.ZERO;
//                    int index = 0;
//                    int lastValidIndex = 0;
//                    do {
//                        int tot = 0;
//                        int beg = 0;
//                        int end = 0;
//                        for (index = lastValidIndex; index < sortedXcpdList.size(); index++) {
//                            beg = lastValidIndex;
//                            end = index;
//                            int elapsed = getElapsedTimeBetweenEvents(sortedXcpdList, beg, end);
//                            if (elapsed > upatr) {
//                                lastValidIndex = index;
//                                break;
//                            }
//                            tot++;
//                        }
//                        if (tot > upatThreshold) {
//                            if (lastValidIndex > 0 && index < sortedXcpdList.size()) {
//                                end = lastValidIndex - 1;
//                            } else {
//                                end = sortedXcpdList.size() - 1;
//                            }
//                            int elapsed = getElapsedTimeBetweenEvents(sortedXcpdList, beg, end);
//                            if (elapsed < upatr) {
//                                logger.error("WARNING_SEC_UNEXPECTED_NUMBER_OF_REQUESTS_FOR_UNIQUE_PATIENT : " +
//                                                "[Total requests: '{}' exceeding threshold of: '{}' requests inside an interval " +
//                                                "of '{}' seconds] - begin event : ['{}'] end event : ['{}']",
//                                        tot, upatThreshold, elapsed,
//                                        sortedXcpdList.get(beg), sortedXcpdList.get(end));
//                                String abuseDescription = String.format(DESCRIPTION_PAT, tot, elapsed, upatThreshold);
//                                setAbuseErrorEvent(AbuseType.PAT, abuseDescription, tot, sortedXcpdList.get(beg), sortedXcpdList.get(end));
//                            }
//                        }
//                    } while (index < sortedXcpdList.size() && lastValidIndex < sortedXcpdList.size());
//                });
//            }
//        }

        // strip from table file older than ABUSE_ALL_REQUEST_REFERENCE_REQUEST_PERIOD
        int purgeLimit = NumberUtils.max(new int[]{areqr, upocr, upatr});
        List<AbuseEvent> ret = list.stream()
                .sorted(Comparator.comparing(AbuseEvent::getRequestDateTime))
                .filter(p -> Period.fieldDifference(p.getRequestDateTime(),
                                new LocalDateTime(DateTimeZone.forTimeZone(TimeZone.getDefault())))
                        .toStandardSeconds().getSeconds() <= purgeLimit)
                .collect(Collectors.toList());

        if (ret.size() < list.size()) {
            logger.info("'{}' events purged from active list, new list size; '{}'", list.size() - ret.size(), ret.size());
        } else {
            logger.info("Events in active list: '{}'", list.size());
        }
        return ret;
    }

    private String getActiveParticipants(List<AuditMessage.ActiveParticipant> activeParticipant) {
        StringBuilder val = new StringBuilder();
        for (AuditMessage.ActiveParticipant p : activeParticipant) {
            val.append("ActiveParticipant ").append(p.getUserID()).append(" - ").append(p.isUserIsRequestor()).append(" ");
        }
        return StringUtils.trim(val.toString());
    }

    private String getTypeCodes(List<CodedValueType> eventTypeCode) {
        StringBuilder val = new StringBuilder();
        for (CodedValueType t : eventTypeCode) {
            val.append("EventTypeCode ").append(t.getCode()).append(" - ").append(t.getDisplayName()).append(" ");
        }
        return StringUtils.trim(val.toString());
    }
}
