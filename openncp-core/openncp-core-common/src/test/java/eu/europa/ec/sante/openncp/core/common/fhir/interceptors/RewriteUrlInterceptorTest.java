package eu.europa.ec.sante.openncp.core.common.fhir.interceptors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.Constants;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuFhirContextFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RewriteUrlInterceptorTest {


    @Test
    void testRewriteAttachmentUrl() throws IOException {
        final RewriteUrlInterceptor interceptor = new RewriteUrlInterceptor();
        final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader(HttpHeaders.HOST, "localhost:8091");
        httpServletRequest.setScheme("http");
        httpServletRequest.setContextPath("/openncp-client-connector");
        final FhirContext ctx = EuFhirContextFactory.createFhirContext();
        final IParser parser = ctx.newJsonParser();
        final Bundle bundle = parser.parseResource(Bundle.class, IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("fhir/documentReferences.json"),
                StandardCharsets.UTF_8));
        interceptor.rewriteAttachmentUrl(
                null,
                null,
                bundle,
                httpServletRequest,
                null);
        final List<String> rewrittenUrls = bundle.getEntry().stream()
                .map(Bundle.BundleEntryComponent::getResource)
                .filter(resource -> resource.getResourceType() == ResourceType.DocumentReference)
                .map(resource -> (DocumentReference) resource)
                .map(DocumentReference::getContent)
                .flatMap(Collection::stream)
                .map(DocumentReference.DocumentReferenceContentComponent::getAttachment)
                .filter(attachment -> attachment.getContentType().equalsIgnoreCase(Constants.CT_FHIR_JSON_NEW))
                .map(Attachment::getUrl)
                .collect(Collectors.toList());
        assertThat(rewrittenUrls)
                .hasSize(2)
                .allMatch(url -> url.startsWith("http://localhost:8091/openncp-client-connector/"));
    }
}
