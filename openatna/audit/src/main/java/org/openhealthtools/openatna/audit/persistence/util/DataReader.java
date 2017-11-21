package org.openhealthtools.openatna.audit.persistence.util;

import org.apache.commons.lang.StringUtils;
import org.openhealthtools.openatna.anom.Timestamp;
import org.openhealthtools.openatna.audit.AtnaFactory;
import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.dao.*;
import org.openhealthtools.openatna.audit.persistence.model.*;
import org.openhealthtools.openatna.audit.persistence.model.codes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Reads an XML file and loads entities into the DB.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Sep 10, 2009: 8:37:36 AM
 * @date $Date:$ modified by $Author:$
 */
public class DataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataReader.class);

    private Document doc;

    private Map<String, CodeEntity> evtIds = new HashMap<>();
    private Map<String, CodeEntity> evtTypes = new HashMap<>();
    private Map<String, CodeEntity> sourceTypes = new HashMap<>();
    private Map<String, CodeEntity> objTypes = new HashMap<>();
    private Map<String, CodeEntity> partTypes = new HashMap<>();


    private Map<String, NetworkAccessPointEntity> naps = new HashMap<>();
    private Map<String, SourceEntity> sources = new HashMap<>();
    private Map<String, ParticipantEntity> parts = new HashMap<>();
    private Map<String, ObjectEntity> objects = new HashMap<>();
    private Set<MessageEntity> messages = new HashSet<>();

    public DataReader(InputStream in) {

        try {
            doc = newDocument(in);
            in.close();
        } catch (IOException e) {
            LOGGER.error("IOException: '{}'", e.getMessage(), e);
            throw new RuntimeException("Could not load data file");
        }
    }

    private static Document newDocument(InputStream stream) throws IOException {

        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(stream);
        } catch (ParserConfigurationException e) {
            LOGGER.error("ParserConfigurationException: '{}'", e.getMessage(), e);
        } catch (SAXException e) {
            LOGGER.error("SAXException: '{}'", e.getMessage(), e);
        }
        return doc;
    }

    public void parse() throws AtnaPersistenceException {

        readDoc();
        load();
    }

    private void load() throws AtnaPersistenceException {

        PersistencePolicies pp = new PersistencePolicies();
        pp.setErrorOnDuplicateInsert(false);
        pp.setAllowNewCodes(true);
        pp.setAllowNewNetworkAccessPoints(true);
        pp.setAllowNewObjects(true);
        pp.setAllowNewParticipants(true);
        pp.setAllowNewSources(true);
        if (evtTypes.size() > 0) {
            CodeDao dao = AtnaFactory.codeDao();
            for (CodeEntity code : evtTypes.values()) {
                dao.save(code, pp);
            }
        }
        if (evtIds.size() > 0) {
            CodeDao dao = AtnaFactory.codeDao();
            for (CodeEntity code : evtIds.values()) {
                dao.save(code, pp);
            }
        }
        if (sourceTypes.size() > 0) {
            CodeDao dao = AtnaFactory.codeDao();
            for (CodeEntity code : sourceTypes.values()) {
                dao.save(code, pp);
            }
        }
        if (objTypes.size() > 0) {
            CodeDao dao = AtnaFactory.codeDao();
            for (CodeEntity code : objTypes.values()) {
                dao.save(code, pp);
            }
        }
        if (partTypes.size() > 0) {
            CodeDao dao = AtnaFactory.codeDao();
            for (CodeEntity code : partTypes.values()) {
                dao.save(code, pp);
            }
        }
        if (naps.size() > 0) {
            NetworkAccessPointDao dao = AtnaFactory.networkAccessPointDao();
            for (NetworkAccessPointEntity nap : naps.values()) {
                dao.save(nap, pp);
            }
        }

        if (sources.size() > 0) {
            SourceDao dao = AtnaFactory.sourceDao();
            for (SourceEntity source : sources.values()) {
                dao.save(source, pp);
            }
        }
        if (parts.size() > 0) {
            ParticipantDao dao = AtnaFactory.participantDao();
            for (ParticipantEntity pe : parts.values()) {
                dao.save(pe, pp);
            }
        }
        if (objects.size() > 0) {
            ObjectDao dao = AtnaFactory.objectDao();
            for (ObjectEntity e : objects.values()) {
                dao.save(e, pp);
            }
        }
        if (!messages.isEmpty()) {
            MessageDao dao = AtnaFactory.messageDao();
            for (MessageEntity e : messages) {
                dao.save(e, pp);
            }
        }
    }

    private void readDoc() {

        Element el = doc.getDocumentElement();
        if (el.getLocalName().equals(DataConstants.ENTITIES)) {
            NodeList children = el.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    String name = (e).getLocalName();
                    if (name.equalsIgnoreCase(DataConstants.CODES)) {
                        readCodes(e);
                    } else if (name.equalsIgnoreCase(DataConstants.NETWORK_ACCESS_POINTS)) {
                        readNaps(e);
                    }
                }
            }
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    String name = (e).getLocalName();
                    if (name.equalsIgnoreCase(DataConstants.SOURCES)) {
                        readSources(e);
                    } else if (name.equalsIgnoreCase(DataConstants.PARTICIPANTS)) {
                        readParts(e);
                    } else if (name.equalsIgnoreCase(DataConstants.OBJECTS)) {
                        readObjects(e);
                    }
                }
            }
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    String name = (e).getLocalName();
                    if (name.equalsIgnoreCase(DataConstants.MESSAGE)) {
                        readMessage(e);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Unknown XML format");
        }
    }

    private void readCodes(Element codes) {

        NodeList children = codes.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase(DataConstants.CODE_TYPE)) {
                    String type = e.getAttribute("name");
                    if (type != null) {
                        readCodeTypes(e, type);
                    }
                }
            }
        }
    }

    private void readCodeTypes(Element code, String type) {

        NodeList children = code.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element) {
                if (((Element) n).getTagName().equalsIgnoreCase(DataConstants.CODE)) {
                    readCode((Element) n, type);
                }
            }
        }
    }

    private void readCode(Element el, String type) {

        CodeEntity entity = null;
        if (type.equalsIgnoreCase(DataConstants.CODE_EVENT_ID)) {
            entity = new EventIdCodeEntity();
        } else if (type.equalsIgnoreCase(DataConstants.CODE_EVENT_TYPE)) {
            entity = new EventTypeCodeEntity();
        } else if (type.equalsIgnoreCase(DataConstants.CODE_OBJ_ID_TYPE)) {
            entity = new ObjectIdTypeCodeEntity();
        } else if (type.equalsIgnoreCase(DataConstants.CODE_PARTICIPANT_TYPE)) {
            entity = new ParticipantCodeEntity();
        } else if (type.equalsIgnoreCase(DataConstants.CODE_SOURCE)) {
            entity = new SourceCodeEntity();
        }
        if (entity == null) {
            return;
        }
        String code = el.getAttribute(DataConstants.CODE);
        if (nill(code)) {
            LOGGER.info("no code defined in coded value. Not loading...");
            return;
        }
        entity.setCode(code);
        String sys = el.getAttribute(DataConstants.CODE_SYSTEM);
        String name = el.getAttribute(DataConstants.CODE_SYSTEM_NAME);
        String dis = el.getAttribute(DataConstants.DISPLAY_NAME);
        String orig = el.getAttribute(DataConstants.ORIGINAL_TEXT);
        entity.setCodeSystem(nill(sys) ? null : sys);
        entity.setCodeSystemName(nill(name) ? null : name);
        entity.setDisplayName(nill(dis) ? null : dis);
        entity.setOriginalText(nill(orig) ? null : orig);

        switch (type) {
            case DataConstants.CODE_EVENT_ID:
                evtIds.put(code, entity);
                break;
            case DataConstants.CODE_EVENT_TYPE:
                evtTypes.put(code, entity);
                break;
            case DataConstants.CODE_OBJ_ID_TYPE:
                objTypes.put(code, entity);
                break;
            case DataConstants.CODE_PARTICIPANT_TYPE:
                partTypes.put(code, entity);
                break;
            case DataConstants.CODE_SOURCE:
                sourceTypes.put(code, entity);
                break;
        }
    }

    private void readNaps(Element codes) {

        NodeList children = codes.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element && n.getLocalName().equals(DataConstants.NETWORK_ACCESS_POINT)) {
                readNap((Element) n);
            }
        }
    }

    private void readNap(Element el) {

        String netId = el.getAttribute(DataConstants.NETWORK_ACCESS_POINT_ID);
        String type = el.getAttribute(DataConstants.TYPE);
        if (nill(netId) || nill(type)) {
            LOGGER.info("no identifier or type defined in network access point. Not loading...");
            return;
        }
        NetworkAccessPointEntity e = new NetworkAccessPointEntity();
        e.setIdentifier(netId);
        e.setType(new Short(type));
        naps.put(netId, e);
    }

    private void readSources(Element codes) {

        NodeList children = codes.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element && n.getLocalName().equals(DataConstants.SOURCE)) {
                readSource((Element) n);
            }
        }
    }

    private void readSource(Element el) {

        String sourceId = el.getAttribute(DataConstants.SOURCE_ID);
        if (nill(sourceId)) {
            LOGGER.info("No Source id set. Not loading...");
            return;
        }
        String ent = el.getAttribute(DataConstants.ENT_SITE_ID);
        SourceEntity e = new SourceEntity();
        e.setSourceId(sourceId);
        e.setEnterpriseSiteId(nill(ent) ? null : ent);
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element && n.getLocalName().equals(DataConstants.SOURCE_TYPE)) {
                Element ch = (Element) n;
                String ref = ch.getAttribute(DataConstants.CODE);
                if (nill(ref)) {
                    continue;
                }
                CodeEntity code = sourceTypes.get(ref);
                if (code != null && code instanceof SourceCodeEntity) {
                    e.getSourceTypeCodes().add((SourceCodeEntity) code);
                }
            }
        }
        sources.put(sourceId, e);
    }

    private void readParts(Element codes) {

        NodeList children = codes.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element && n.getLocalName().equals(DataConstants.PARTICIPANT)) {
                readPart((Element) n);
            }
        }
    }

    private void readPart(Element el) {

        String partId = el.getAttribute(DataConstants.USER_ID);
        if (nill(partId)) {
            LOGGER.info("no active participant id defined. Not loading...");
        }
        String name = el.getAttribute(DataConstants.USER_NAME);
        String alt = el.getAttribute(DataConstants.ALT_USER_ID);
        ParticipantEntity e = new ParticipantEntity();
        e.setUserId(partId);
        e.setUserName(nill(name) ? null : name);
        e.setAlternativeUserId(nill(alt) ? null : alt);
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element && n.getLocalName().equals(DataConstants.PARTICIPANT_TYPE)) {
                Element ch = (Element) n;
                String ref = ch.getAttribute(DataConstants.CODE);
                if (nill(ref)) {
                    continue;
                }
                CodeEntity code = partTypes.get(ref);
                if (code != null && code instanceof ParticipantCodeEntity) {
                    e.getParticipantTypeCodes().add((ParticipantCodeEntity) code);
                }
            }
        }
        parts.put(partId, e);
    }

    private void readObjects(Element codes) {

        NodeList children = codes.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element && n.getLocalName().equals(DataConstants.OBJECT)) {
                readObject((Element) n);
            }
        }
    }

    private void readObject(Element el) {

        String obId = el.getAttribute(DataConstants.OBJECT_ID);
        if (nill(obId)) {
            LOGGER.info("no participating object id defined. Not loading...");
        }
        String name = el.getAttribute(DataConstants.OBJECT_NAME);
        String type = el.getAttribute(DataConstants.OBJECT_TYPE_CODE);
        String role = el.getAttribute(DataConstants.OBJECT_TYPE_CODE_ROLE);
        String sens = el.getAttribute(DataConstants.OBJECT_SENSITIVITY);
        ObjectEntity e = new ObjectEntity();
        e.setObjectId(obId);
        e.setObjectName(nill(name) ? null : name);
        e.setObjectSensitivity(nill(sens) ? null : sens);
        e.setObjectTypeCode(nill(type) ? null : Short.valueOf(type));
        e.setObjectTypeCodeRole(nill(role) ? null : Short.valueOf(role));
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element) {
                Element ele = (Element) n;

                if (ele.getLocalName().equals(DataConstants.OBJECT_ID_TYPE)) {
                    String ref = ele.getAttribute(DataConstants.CODE);
                    if (nill(ref)) {
                        LOGGER.info("no object id type defined. Not loading...");
                        return;
                    }
                    CodeEntity code = objTypes.get(ref);
                    if (code != null && code instanceof ObjectIdTypeCodeEntity) {
                        e.setObjectIdTypeCode((ObjectIdTypeCodeEntity) code);
                    } else {
                        LOGGER.info("no object id type defined. Not loading...");
                        return;
                    }
                } else if (ele.getLocalName().equals(DataConstants.OBJECT_DETAIL_KEY)) {
                    String key = ele.getAttribute(DataConstants.KEY);
                    if (key != null) {
                        e.addObjectDetailType(key);
                    }
                }
            }
        }
        if (e.getObjectIdTypeCode() == null) {
            LOGGER.info("no object id type defined. Not loading...");
            return;
        }
        objects.put(obId, e);
    }

    public void readMessage(Element el) {

        String action = el.getAttribute(DataConstants.EVT_ACTION);
        String outcome = el.getAttribute(DataConstants.EVT_OUTCOME);
        String time = el.getAttribute(DataConstants.EVT_TIME);
        Date ts = null;
        if (time != null) {
            ts = Timestamp.parseToDate(time);
        }
        if (ts == null) {
            ts = new Date();
        }
        if (nill(action) || nill(outcome)) {
            LOGGER.info("action or outcome of message is null. Not loading...");
        }
        MessageEntity ent = new MessageEntity();
        ent.setEventActionCode(action);
        ent.setEventDateTime(ts);
        ent.setEventOutcome(Integer.parseInt(outcome));
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element) {
                Element ele = (Element) n;
                if (ele.getLocalName().equals(DataConstants.EVT_ID)) {
                    String ref = ele.getAttribute(DataConstants.CODE);
                    if (nill(ref)) {
                        LOGGER.info("no event id type defined. Not loading...");
                        return;
                    }
                    CodeEntity code = evtIds.get(ref);
                    if (code != null && code instanceof EventIdCodeEntity) {
                        ent.setEventId((EventIdCodeEntity) code);
                    } else {
                        LOGGER.info("no event id type defined. Not loading...");
                        return;
                    }
                } else if (ele.getLocalName().equals(DataConstants.EVT_TYPE)) {
                    String ref = ele.getAttribute(DataConstants.CODE);
                    if (!nill(ref)) {
                        CodeEntity code = evtTypes.get(ref);
                        if (code != null && code instanceof EventTypeCodeEntity) {
                            ent.addEventTypeCode((EventTypeCodeEntity) code);
                        }
                    }
                } else if (ele.getLocalName().equals(DataConstants.PARTICIPANT)) {
                    String ref = ele.getAttribute(DataConstants.REF);
                    if (!nill(ref)) {
                        ParticipantEntity pe = parts.get(ref);
                        if (pe != null) {
                            MessageParticipantEntity p = new MessageParticipantEntity(pe);
                            String requestor = ele.getAttribute("requestor");
                            if (requestor != null) {
                                p.setUserIsRequestor(Boolean.valueOf(requestor));
                            }
                            String nap = ele.getAttribute("nap");
                            if (nap != null) {
                                NetworkAccessPointEntity net = naps.get(nap);
                                if (net != null) {
                                    p.setNetworkAccessPoint(net);
                                }
                            }
                            ent.addMessageParticipant(p);
                        }
                    }
                } else if (ele.getLocalName().equals(DataConstants.SOURCE)) {
                    String ref = ele.getAttribute(DataConstants.REF);
                    if (!nill(ref)) {
                        SourceEntity se = sources.get(ref);
                        if (se != null) {
                            MessageSourceEntity p = new MessageSourceEntity(se);
                            ent.addMessageSource(p);
                        }
                    }
                } else if (ele.getLocalName().equals(DataConstants.OBJECT)) {
                    String ref = ele.getAttribute(DataConstants.REF);
                    if (!nill(ref)) {
                        ObjectEntity oe = objects.get(ref);
                        if (oe != null) {
                            MessageObjectEntity p = new MessageObjectEntity(oe);
                            NodeList ch = ele.getChildNodes();
                            for (int j = 0; j < ch.getLength(); j++) {
                                Node node = ch.item(j);
                                if (node instanceof Element) {
                                    Element child = (Element) node;
                                    boolean enc = child.getAttribute("encoded") != null
                                            && StringUtils.equalsIgnoreCase(child.getAttribute("encoded"), "true");
                                    if (child.getLocalName().equals(DataConstants.QUERY)) {
                                        String q = child.getTextContent();
                                        if (q != null) {
                                            q = q.trim();
                                            if (!enc) {
                                                q = Base64.encodeString(q);
                                            }
                                            try {
                                                p.setObjectQuery(q.getBytes("UTF-8"));
                                            } catch (UnsupportedEncodingException e) {
                                                // shouldn't happen
                                                LOGGER.error("UnsupportedEncodingException: '{}'", e.getMessage(), e);
                                            }
                                        }
                                    } else if (child.getLocalName().equals(DataConstants.DETAIL)) {
                                        String type = child.getAttribute(DataConstants.TYPE);
                                        if (type != null) {
                                            String val = child.getTextContent();
                                            if (val != null) {
                                                val = val.trim();
                                                if (!enc) {
                                                    val = Base64.encodeString(val);
                                                }
                                                try {
                                                    ObjectDetailEntity ode
                                                            = new ObjectDetailEntity(type, val.getBytes("UTF-8"));
                                                    p.addObjectDetail(ode);
                                                } catch (UnsupportedEncodingException e) {
                                                    // shouldn't happen
                                                    LOGGER.error("UnsupportedEncodingException: '{}'", e.getMessage(), e);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            ent.addMessageObject(p);
                        }
                    }
                }
            }
        }
        if (ent.getMessageParticipants().isEmpty()) {
            LOGGER.info("message has no participants. Not loading...");
            return;
        }
        if (ent.getMessageSources().isEmpty()) {
            LOGGER.info("message has no sources. Not loading...");
            return;
        }
        messages.add(ent);
    }

    private String id(Element el) {
        String id = el.getAttribute(DataConstants.ID);
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    private boolean nill(String val) {
        return val == null || val.trim().length() == 0;
    }
}
