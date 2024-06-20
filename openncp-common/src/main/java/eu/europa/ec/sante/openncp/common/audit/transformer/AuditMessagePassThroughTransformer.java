package eu.europa.ec.sante.openncp.common.audit.transformer;

import eu.europa.ec.sante.openncp.common.NcpSide;
import eu.europa.ec.sante.openncp.common.audit.AuditService;
import eu.europa.ec.sante.openncp.common.audit.AuditTrailUtils;
import eu.europa.ec.sante.openncp.common.audit.EventLog;
import eu.europa.ec.sante.openncp.common.audit.EventType;
import eu.europa.ec.sante.openncp.common.validation.OpenNCPValidation;
import net.RFC3881.dicom.AuditMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.StringWriter;

@Component
public class AuditMessagePassThroughTransformer implements AuditMessageTransformer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    @Override
    public boolean accepts(Object message) {
        Validate.notNull(message);
        return message instanceof AuditMessage;
    }

    @Override
    public AuditMessage transform(final Object message) {
        Validate.notNull(message, "AuditMessage cannot be null.");
        //pass through
        return (AuditMessage) message;
    }
}
