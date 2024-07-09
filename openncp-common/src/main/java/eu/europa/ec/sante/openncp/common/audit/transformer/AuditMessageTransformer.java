package eu.europa.ec.sante.openncp.common.audit.transformer;

import net.RFC3881.dicom.AuditMessage;
import org.apache.commons.lang.Validate;

/**
 * Transforms several audit message formats into the RFC3881 {@link AuditMessage} that our ATNA solution understands.
 */
public interface AuditMessageTransformer {
    boolean accepts(Object message);

    AuditMessage transform(Object message);
}
