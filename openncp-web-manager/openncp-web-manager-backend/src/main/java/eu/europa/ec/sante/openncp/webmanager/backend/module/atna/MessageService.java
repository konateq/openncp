package eu.europa.ec.sante.openncp.webmanager.backend.module.atna;

import com.querydsl.core.types.dsl.BooleanExpression;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.domain.MessageWrapper;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.domain.old.Message;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.model.Code;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.model.MessageEntity;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.model.ParticipantEntity;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.repository.MessageRepository;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.support.MessageMapper;
import eu.europa.ec.sante.openncp.webmanager.backend.module.atna.support.SearchPredicatesBuilder;
import net.RFC3881.dicom.AuditMessage;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    private final MessageRepository messageRepository;

    private final Jaxb2Marshaller marshaller;

    public MessageService(final MessageRepository messageRepository, final Jaxb2Marshaller marshaller) {
        this.messageRepository = messageRepository;
        this.marshaller = marshaller;
    }

    public Page<Message> findMessages(final Pageable pageable) {
        final Page<MessageEntity> page = messageRepository.findAllMessages(pageable);
        return new PageImpl<>(messageMapper.map(page), pageable, page.getTotalElements());
    }

    public Page<Message> searchMessages(final String searchEventId, final Instant searchEventStartDate, final Instant searchEventEndDate,
                                        final String activeParticipantId, final String activeTypeCode, final String messageType, final Pageable pageable) {

        final SearchPredicatesBuilder builder = new SearchPredicatesBuilder();
        builder.with(MessageEntity.class, "eventId.codeName", ":", searchEventId);
        builder.with(ParticipantEntity.class, "userId", ":", activeParticipantId);
        builder.with(Code.class, "codeName", ":", activeTypeCode);
        builder.with(MessageEntity.class, "messageType", ":", messageType);
        builder.with(MessageEntity.class, "eventDateTime", ">", searchEventStartDate);
        builder.with(MessageEntity.class, "eventDateTime", "<", searchEventEndDate);

        final BooleanExpression exp = builder.build();

        final Page<MessageEntity> page = messageRepository.searchMessages(exp, pageable);
        return new PageImpl<>(messageMapper.map(page), pageable, page.getTotalElements());
    }

    public MessageWrapper getMessage(final Long id) {

        final MessageEntity entity = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        final AuditMessage auditMessage = (AuditMessage) marshaller.unmarshal(
                new StreamSource(new StringReader(new String(entity.getMessageContent(), StandardCharsets.UTF_8))));
        final StringWriter outWriter = new StringWriter();
        final StreamResult result = new StreamResult(outWriter);
        marshaller.marshal(auditMessage, result);

        final MessageWrapper messageWrapper = new MessageWrapper();
        messageWrapper.setAuditMessage(auditMessage);
        messageWrapper.setXmlMessage(outWriter.toString());
        return messageWrapper;
    }
}
