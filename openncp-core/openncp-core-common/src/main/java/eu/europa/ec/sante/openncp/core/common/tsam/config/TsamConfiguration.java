package eu.europa.ec.sante.openncp.core.common.tsam.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TsamConfiguration {
    /**
     * Code of a language, which country B uses for designations in pivot
     * documents created in translation (local language of a country)
     */
    private String translationLang;
    /**
     * Code of a language, which country A uses for designations in pivot
     * documents created in transcoding (epSOS defines it as English)
     */
    private String transcodingLang;


    public TsamConfiguration() {
    }

    /**
     * @return Translation language
     */
    public String getTranslationLang() {
        return translationLang;
    }

    public void setTranslationLang(final String translationLang) {
        this.translationLang = translationLang;
    }

    /**
     * @return Transcoding language
     */
    public String getTranscodingLang() {
        return transcodingLang;
    }

    public void setTranscodingLang(final String transcofingLang) {
        this.transcodingLang = transcofingLang;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("TsamConfiguration [");
        if (transcodingLang != null) {
            builder.append("transcodingLang=");
            builder.append(transcodingLang);
            builder.append(", ");
        }
        if (translationLang != null) {
            builder.append("translationLang=");
            builder.append(translationLang);
        }
        builder.append("]");
        return builder.toString();
    }
}
