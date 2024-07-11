package eu.europa.ec.sante.openncp.core.common.tsam.config;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:${EPSOS_PROPS_PATH}tsam.properties")
public class TsamConfiguration {
    /**
     * Code of a language, which country B uses for designations in pivot
     * documents created in translation (local language of a country)
     */

    @Value("${translationLanguage}")
    private String translationLanguage;
    /**
     * Code of a language, which country A uses for designations in pivot
     * documents created in transcoding (epSOS defines it as English)
     */

    @Value("${transcodingLanguage}")
    private String transcodingLanguage;

    public TsamConfiguration() {
    }

    /**
     * @return Translation language
     */
    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(final String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    /**
     * @return Transcoding language
     */
    public String getTranscodingLanguage() {
        return transcodingLanguage;
    }

    public void setTranscodingLanguage(final String transcodingLanguage) {
        this.transcodingLanguage = transcodingLanguage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("translationLanguage", translationLanguage)
                .append("transcodingLanguage", transcodingLanguage)
                .toString();
    }
}
