package eu.europa.ec.sante.openncp.api.common.handler;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.ResourceType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class RewriteUrlHandler implements BundleHandler {

    private RequestDetails requestDetails;

    @Override
    public Bundle handle(final Bundle bundle) {
        if (bundle.getType() == Bundle.BundleType.SEARCHSET) {
            bundle.getEntry().stream()
                    .map(Bundle.BundleEntryComponent::getResource)
                    .filter(resource -> resource.getResourceType() == ResourceType.DocumentReference)
                    .map(resource -> (DocumentReference) resource)
                    .map(DocumentReference::getContent)
                    .flatMap(Collection::stream)
                    .map(DocumentReference.DocumentReferenceContentComponent::getAttachment)
                    .filter(attachment -> attachment.getContentType().equalsIgnoreCase(Constants.CT_FHIR_JSON))
                    .forEach(attachment -> rewriteUrl(attachment, requestDetails.getFhirServerBase()));
        }
        return bundle;
    }

    private void rewriteUrl(final Attachment attachment, final String replaceUrl) {
        attachment.setUrl(attachment.getUrl().replaceAll("^.*?(?=Bundle)", replaceUrl));
    }

    public static void main(final String[] args) throws IOException {
        final FhirContext ctx = FhirContext.forR4();
        final IParser parser = ctx.newJsonParser();
        final Bundle input = parser.parseResource(Bundle.class, IOUtils.toString(
                RewriteUrlHandler.class.getClassLoader().getResourceAsStream("documentReferences.json"),
                StandardCharsets.UTF_8));
        final RewriteUrlHandler rewriteUrlHandler = new RewriteUrlHandler();
        final Bundle result = rewriteUrlHandler.handle(input);
        System.out.println(parser.encodeResourceToString(result));
    }
}
