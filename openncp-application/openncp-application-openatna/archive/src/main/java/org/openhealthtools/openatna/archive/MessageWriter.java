package org.openhealthtools.openatna.archive;

import org.openhealthtools.openatna.audit.persistence.model.*;
import org.openhealthtools.openatna.audit.persistence.model.codes.CodeEntity;
import org.openhealthtools.openatna.audit.persistence.util.Base64;
import org.openhealthtools.openatna.audit.persistence.util.DataConstants;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageWriter {

    private final EntityWriter entityWriter = new EntityWriter();

    public void begin(XMLStreamWriter writer) throws XMLStreamException {

        writer.writeStartDocument();
        writer.writeStartElement(DataConstants.MESSAGES);
    }

    public void writeMessages(List<? extends MessageEntity> messageList, XMLStreamWriter writer) throws XMLStreamException {

        for (MessageEntity message : messageList) {
            writer.writeStartElement(DataConstants.MESSAGE);
            writeSourceAddress(writer, message);
            writeEventActionCode(writer, message);
            writeEventOutcome(writer, message);
            writeEventDateTime(writer, message);
            writeCodeEventId(writer, message);
            writeCodeEntity(writer, message);
            Set<MessageSourceEntity> sources = message.getMessageSources();
            writer.writeStartElement(DataConstants.MESSAGE_SOURCES);
            writeSourceEntity(writer, sources);
            writer.writeEndElement();
            Set<MessageParticipantEntity> parts = message.getMessageParticipants();
            writer.writeStartElement(DataConstants.MESSAGE_PARTICIPANTS);
            for (MessageParticipantEntity part : parts) {
                writer.writeStartElement(DataConstants.MESSAGE_PARTICIPANT);
                writeAttributeUserIsRequestor(writer, part);
                NetworkAccessPointEntity nap = part.getNetworkAccessPoint();
                writeNap(writer, nap);
                ParticipantEntity pe = part.getParticipant();
                entityWriter.writeParticipant(pe, writer);
                writer.writeEndElement();
            }
            writer.writeEndElement();

            writer.writeStartElement(DataConstants.MESSAGE_OBJECTS);
            Set<MessageObjectEntity> objs = message.getMessageObjects();
            for (MessageObjectEntity obj : objs) {
                writer.writeStartElement(DataConstants.MESSAGE_OBJECT);
                Short cyc = obj.getObjectDataLifeCycle();
                if (cyc != null) {
                    writer.writeAttribute(DataConstants.OBJECT_DATA_LIFECYCLE, Short.toString(cyc));
                }
                byte[] query = obj.getObjectQuery();
                if (query != null && query.length > 0) {
                    writer.writeStartElement(DataConstants.OBJECT_QUERY);
                    writer.writeCharacters(Base64.encode(query));
                    writer.writeEndElement();
                }
                Set<ObjectDetailEntity> details = obj.getDetails();
                for (ObjectDetailEntity detail : details) {
                    writer.writeStartElement(DataConstants.DETAIL);
                    writer.writeStartElement(DataConstants.TYPE);
                    writer.writeCharacters(detail.getType());
                    writer.writeEndElement();
                    writer.writeStartElement(DataConstants.VALUE);
                    writer.writeCharacters(Base64.encode(detail.getValue()));
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
                ObjectEntity oe = obj.getObject();
                entityWriter.writeObject(oe, writer);
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndElement();
        }
    }

    private void writeNap(XMLStreamWriter writer, NetworkAccessPointEntity nap) throws XMLStreamException {
        if (nap != null) {
            entityWriter.writeNap(nap, writer);
        }
    }

    private static void writeAttributeUserIsRequestor(XMLStreamWriter writer, MessageParticipantEntity part) throws XMLStreamException {
        if (Boolean.TRUE.equals(part.isUserIsRequestor())) {
            writer.writeAttribute(DataConstants.USER_IS_REQUESTOR, Boolean.toString(part.isUserIsRequestor()));
        }
    }

    private void writeSourceEntity(XMLStreamWriter writer, Set<MessageSourceEntity> sources) throws XMLStreamException {
        for (MessageSourceEntity source : sources) {
            writer.writeStartElement(DataConstants.MESSAGE_SOURCE);
            entityWriter.writeSource(source.getSource(), writer);
            writer.writeEndElement();
        }
    }

    private void writeCodeEntity(XMLStreamWriter writer, MessageEntity message) throws XMLStreamException {
        if (!message.getEventTypeCodes().isEmpty()) {
            List<? extends CodeEntity> l = new ArrayList<>(message.getEventTypeCodes());
            for (CodeEntity codeEntity : l) {
                entityWriter.writeCode(codeEntity, writer, DataConstants.EVT_TYPE);
            }
        }
    }

    private void writeCodeEventId(XMLStreamWriter writer, MessageEntity message) throws XMLStreamException {
        if (message.getEventId() != null) {
            entityWriter.writeCode(message.getEventId(), writer, DataConstants.EVT_ID);
        }
    }

    private static void writeEventDateTime(XMLStreamWriter writer, MessageEntity message) throws XMLStreamException {
        if (message.getEventDateTime() != null) {
            writer.writeAttribute(DataConstants.EVT_TIME, Archiver.formatDate(message.getEventDateTime()));
        }
    }

    private static void writeEventOutcome(XMLStreamWriter writer, MessageEntity message) throws XMLStreamException {
        if (message.getEventOutcome() != null) {
            writer.writeAttribute(DataConstants.EVT_OUTCOME, Integer.toString(message.getEventOutcome()));
        }
    }

    private static void writeEventActionCode(XMLStreamWriter writer, MessageEntity message) throws XMLStreamException {
        if (message.getEventActionCode() != null) {
            writer.writeAttribute(DataConstants.EVT_ACTION, message.getEventActionCode());
        }
    }

    private static void writeSourceAddress(XMLStreamWriter writer, MessageEntity message) throws XMLStreamException {
        if (message.getSourceAddress() != null) {
            writer.writeAttribute(DataConstants.SOURCE_IP, message.getSourceAddress());
        }
    }

    public void finish(XMLStreamWriter writer) throws IOException {

        try {
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }
}
