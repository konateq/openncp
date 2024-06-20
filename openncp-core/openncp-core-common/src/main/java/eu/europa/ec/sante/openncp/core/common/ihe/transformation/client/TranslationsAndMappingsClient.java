package eu.europa.ec.sante.openncp.core.common.ihe.transformation.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import eu.europa.ec.sante.openncp.common.error.OpenNCPErrorCode;
import eu.europa.ec.sante.openncp.core.common.ihe.exception.DocumentTransformationException;
import eu.europa.ec.sante.openncp.core.common.ihe.transformation.domain.TMResponseStructure;
import eu.europa.ec.sante.openncp.core.common.ihe.transformation.util.Base64Util;
import org.apache.commons.io.Charsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;

public class TranslationsAndMappingsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationsAndMappingsClient.class);

    public static TMResponseStructure translate(final Document cdaPivot, final String targetLanguage) throws DocumentTransformationException {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            LOGGER.debug("TM - TRANSLATION START.");
            final var mapper = new ObjectMapper();
            final var node = mapper.createObjectNode();
            node.put("pivotCDA", Base64Util.encode(cdaPivot));
            node.put("targetLanguageCode", targetLanguage);
            final var jsonString = node.toString();
            final var entity = new StringEntity(jsonString, HTTP.UTF_8);
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            final var postRequest = new HttpPost(getTranslationsAndMappingsWsUrl() + "/translate");
            postRequest.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
            postRequest.setEntity(entity);
            try (final CloseableHttpResponse response = httpclient.execute(postRequest)) {
                LOGGER.debug("HTTP statusCode : " + response.getStatusLine().getStatusCode());

                final var responseEntity = response.getEntity();
                final var encodingHeader = responseEntity.getContentEncoding();
                final var encoding = encodingHeader == null ? StandardCharsets.UTF_8 :
                        Charsets.toCharset(encodingHeader.getValue());

                final var json = EntityUtils.toString(responseEntity, encoding);
                LOGGER.debug("Generated json response : " + json);
                final var tmResponse = mapper.readValue(json, TMResponseStructure.class);

                LOGGER.debug("TM - TRANSLATION STOP");
                return tmResponse;
            }
        } catch (final Exception ex) {
            throw new DocumentTransformationException(OpenNCPErrorCode.ERROR_GENERIC, ex.getMessage(), ex.getMessage());
        }
    }

    public static TMResponseStructure transcode(final Document cdaFriendly) throws DocumentTransformationException {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            LOGGER.debug("TM - TRANSCODING START.");
            final var mapper = new ObjectMapper();
            final var node = mapper.createObjectNode();
            node.put("friendlyCDA", Base64Util.encode(cdaFriendly));
            final var jsonString = node.toString();
            final var entity = new StringEntity(jsonString, StandardCharsets.UTF_8);
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            final var postRequest = new HttpPost(getTranslationsAndMappingsWsUrl() + "/transcode");
            postRequest.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
            postRequest.setEntity(entity);
            try (final CloseableHttpResponse response = httpclient.execute(postRequest)) {
                LOGGER.debug("HTTP statusCode : " + response.getStatusLine().getStatusCode());

                final var responseEntity = response.getEntity();
                final var encodingHeader = responseEntity.getContentEncoding();
                final var encoding = encodingHeader == null ? StandardCharsets.UTF_8 :
                        Charsets.toCharset(encodingHeader.getValue());

                final var json = EntityUtils.toString(responseEntity, encoding);
                final var tmResponse = mapper.readValue(json, TMResponseStructure.class);

                LOGGER.debug("TM - TRANSCODING STOP");
                return tmResponse;
            }
        } catch (final Exception ex) {
            throw new DocumentTransformationException(OpenNCPErrorCode.ERROR_GENERIC, ex.getMessage(), ex.getMessage());
        }
    }


    private static String getTranslationsAndMappingsWsUrl() {
        final var translationsAndMappingsUrl = ConfigurationManagerFactory.getConfigurationManager().getProperty("TRANSLATIONS_AND_MAPPINGS_WS_URL");
        LOGGER.info("Translations and Mappings WS URL: '{}'", translationsAndMappingsUrl);
        return translationsAndMappingsUrl;
    }
}