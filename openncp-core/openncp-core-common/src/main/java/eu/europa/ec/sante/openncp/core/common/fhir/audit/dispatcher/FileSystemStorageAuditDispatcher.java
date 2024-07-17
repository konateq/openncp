package eu.europa.ec.sante.openncp.core.common.fhir.audit.dispatcher;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class FileSystemStorageAuditDispatcher implements AuditDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageAuditDispatcher.class);
    private final FhirContext fhirContext;

    @Value("${EPSOS_PROPS_PATH}")
    private String epsosPropsPath;

    public FileSystemStorageAuditDispatcher(final FhirContext fhirContext) {
        this.fhirContext = Validate.notNull(fhirContext, "fhirContext must not be null");
    }

    @Override
    public DispatchResult dispatch(final AuditEvent auditEvent) {
        final String filename = String.format("fhir_audit_%s_%s.json", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC).format(Instant.now()), RandomStringUtils.random(4, true, true));
        final Path file = Paths.get(epsosPropsPath, "validation", filename);

        final DispatchMetadata dispatchingMetadata = ImmutableDispatchMetadata.builder()
                .dispatcherUsed(this.getClass())
                .dispatchingDestination(file.toString())
                .build();

        final IParser jsonParser = fhirContext.newJsonParser().setPrettyPrint(true);
        final String jsonString = jsonParser.encodeResourceToString(auditEvent);

        try {
            Files.write(file, jsonString.getBytes());
        } catch (IOException e) {
            return DispatchResult.failure(dispatchingMetadata, "There was an error writing the audit event to the filesystem.", e);
        }

        return DispatchResult.success(dispatchingMetadata, String.format("Dispatching audit event to filesystem [%s]", file));
    }
}
